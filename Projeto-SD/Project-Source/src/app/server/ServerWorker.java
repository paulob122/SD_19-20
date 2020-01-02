package app.server;

import app.model.FileSharingSystem;
import app.model.content.music.Music;
import app.model.users.User;
import app.server.tools.ClientIdentifier;
import app.server.tools.DownloadRequestsBuffer;
import app.utils.Config;
import app.utils.GeneralMessage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * Handles the clients that connect to the server and parses its messages through the socket.
 *
 * @author Grupo 19
 * @version 2020/01/01
 */
public class ServerWorker implements Runnable {

    //------------------------------------------------------------------------------------------------------------------

    /**
     * User authenticated.
     */
    private User user_authenticated;

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Reader for the socket input stream
     */
    private BufferedReader in_reader;
    /**
     * Writter for the socket output stream
     */
    private PrintWriter out_writer;
    /**
     * Socket for the client connection
     */
    private Socket client_socket;

    /**
     * System reference from the server.
     */
    private FileSharingSystem fss_system;
    /**
     * Server config
     */
    private Config config;
    /**
     * Download Requests buffer.
     */
    private DownloadRequestsBuffer download_requests_buffer;

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Parametrized construcotr for a ServerWorker given all the server configurations an client socket.
     * @param client_socket client socket
     * @param system system
     * @param config server config
     * @param downloadRequestBuffer server download requests buffer
     * @throws IOException in case buffers initialization exception occurs
     */
    public ServerWorker(Socket client_socket, FileSharingSystem system, Config config, DownloadRequestsBuffer downloadRequestBuffer) throws IOException {

        this.in_reader = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
        this.out_writer = new PrintWriter(client_socket.getOutputStream());
        this.out_writer.flush();

        this.client_socket = client_socket;

        this.fss_system = system;

        this.user_authenticated = null;

        this.config = config;

        this.download_requests_buffer = downloadRequestBuffer;
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Runs the server worker and waits for the client input.
     * Parses the input, uses system to control data and writes answers to the client.
     */
    @Override
    public void run() {

        try {

            GeneralMessage.show(2, "worker", "initialized...", false);

            String message_from_client;

            while ((message_from_client = this.in_reader.readLine()) != null) {

                switch (message_from_client) {

                    case "AUTHENTICATE":
                        authenticate();
                        GeneralMessage.show(2, "worker", "got: " + message_from_client + ", from: " + client_socket.getRemoteSocketAddress(), false);
                        break;

                    case "REGISTER":
                        register();
                        GeneralMessage.show(2, "worker", "got: " + message_from_client + ", from: " + client_socket.getRemoteSocketAddress(), false);
                    break;

                    case "SEARCH":
                        search();
                        GeneralMessage.show(2, "worker", "got: " + message_from_client + ", from: " + client_socket.getRemoteSocketAddress(), false);
                        break;

                    case "LOGOUT":
                        logout();
                        GeneralMessage.show(2, "worker", "got: " + message_from_client + ", from: " + client_socket.getRemoteSocketAddress(), false);
                        break;

                    case "UPLOAD_REQUEST": upload(); break;

                    case "DOWNLOAD_REQUEST": download(); break;

                    default:
                        GeneralMessage.show(2, "worker", "got: invalid message " + message_from_client + ", from: " + client_socket.getRemoteSocketAddress(), false);
                        break;
                }

            }

            this.client_socket.shutdownOutput();
            this.client_socket.shutdownInput();
            this.client_socket.close();

        } catch (Exception e) {

            GeneralMessage.show(2, "worker", "error closing socket...", true);
        }

        GeneralMessage.show(2, "worker", "stopped...", false);
        GeneralMessage.show(1, "server", "client disconnected", true);
    }

    /**
     * Logs out user from the system.
     * @throws IOException socket IO
     */
    private void logout() throws IOException {

        String username = in_reader.readLine();

        if (fss_system.is_user_in_session(username)) {

            fss_system.logout_user(username);

            out_writer.println("logout successfull");
            out_writer.flush();

        } else {

            out_writer.println("youre already disconnected");
            out_writer.flush();
        }
    }

    /**
     * logs in user in the system
     * @throws IOException socket IO
     */
    private void authenticate() throws IOException {

        String command_content = in_reader.readLine();

        String[] parts = command_content.split("\\s+");

        boolean ok = fss_system.authenticate(parts[0], parts[1]);

        if (ok) {

            user_authenticated = fss_system.get_user(parts[0]);

            out_writer.println("welcome " + parts[0] + "!");
            out_writer.flush();


        } else {

            out_writer.println("credentials are wrong");
            out_writer.flush();
        }
    }

    /**
     * Creats user given its parameters
     * @throws IOException socket IO
     */
    private void register() throws IOException {

        String command_content = in_reader.readLine();

        String[] parts = command_content.split("\\s+");

        boolean ok = fss_system.register_user(parts[0], parts[1]);

        if (ok) {

            user_authenticated = fss_system.get_user(parts[0]);

            this.fss_system.authenticate(parts[0], parts[1]);

            out_writer.println("Registration successful. Welcome " + parts[0] + "!");
            out_writer.flush();

        } else {

            out_writer.println("could not create user with the given input");
            out_writer.flush();
        }
    }

    /**
     * Searchs for a music tag and shows the result to the user, line by line.
     * @throws IOException socket IO
     */
    private void search() throws IOException {

        String command_content = in_reader.readLine();

        String[] parts = command_content.split("\\s+");

        List<Music> musics_with_tag = fss_system.search(parts[0]);

        if (musics_with_tag.isEmpty()) {

            out_writer.println("There is no content with that tag...");
            out_writer.flush();

        } else {

            out_writer.println(musics_with_tag.size() + "");
            out_writer.flush();

            int i = 0;
            while (i < musics_with_tag.size()) {

                out_writer.println(musics_with_tag.get(i).toString());
                out_writer.flush();
                i++;
            }

        }
    }

    /**
     * Uploads content to the system reading file bytes from the socket and recreates the file in the server side.
     * @throws IOException socket IO
     */
    private void upload() throws IOException {

        GeneralMessage.show(2, "worker", "got: " + "UPLOAD, from: " + client_socket.getRemoteSocketAddress(), false);

        String file_size_as_string = in_reader.readLine();
        String desc = in_reader.readLine();

        double bytes = Double.parseDouble(file_size_as_string);

        double size_in_kb = bytes / 1024;

        String[] desc_parts = desc.split(":");
        String[] splitted_tags = desc_parts[3].split("\\s+");
        List<String> list_of_tags = Arrays.asList(splitted_tags);

        int id = fss_system.add_content(desc_parts[0], desc_parts[1], Integer.parseInt(desc_parts[2]), list_of_tags);

        if (id == -1) {

            this.out_writer.println("Music already exists with that title!");
            this.out_writer.flush();

            return;

        } else {

            this.out_writer.println("Uploading: " + fss_system.get_content(id).toString());
            this.out_writer.flush();
        }

        FileOutputStream fos = new FileOutputStream(new File(config.getServer_db_path() + desc_parts[0]));

        int count = 0, r = 0, sum = 0;
        String str;
        while ((str = in_reader.readLine()) != null) {

            byte[] chunk = Base64.getDecoder().decode(str);
            count = chunk.length;
            sum += count;

            fos.write(chunk);
            fos.flush();

            GeneralMessage.show(3, "upload", "Got/Wrote chunk " + r + " of size " + count + " from " + client_socket.getRemoteSocketAddress(), false);

            r++;

            if (sum == bytes) break;
        }

        GeneralMessage.show(3, "status", "Chunk size sum = " + sum + " bytes | File size = " + bytes + " bytes | Checksum = " + (sum==bytes?"good":"corrupted"), false);
        System.out.println();

        //temporary code, causing bugs -----FIXED
        //in_reader.readLine();
    }

    /**
     * Downloads content from the server and sends files bytes by chunks in the socket.
     * @throws IOException socket IO
     */
    private void download() throws IOException {

        GeneralMessage.show(2, "worker", "got: " + "DOWNLOAD, from: " + client_socket.getRemoteSocketAddress(), false);

        String content_id_as_string = in_reader.readLine();

        int id = Integer.parseInt(content_id_as_string);

        if (!fss_system.has_content(id)) {

            this.out_writer.println("Content was not found on server database!");
            this.out_writer.flush();

            return;
        }

        StringBuilder content_download_sb = new StringBuilder();

        //withoud considering multiple downloads. assuming client can download
        fss_system.download_content(id);

        content_download_sb.append("Downloading content:\n");
        Music m = fss_system.get_content(id);
        content_download_sb.append(m.toString()).append("\n");

        //TODO: could return null
        //assuming file to download exists on server database
        File download_file = new File(config.getServer_db_path() + m.getTitle());

        double file_size_bytes = download_file.length();

        /*START: TEMPORARY CODE*/

        ClientIdentifier client_requesting_download = new ClientIdentifier(this.client_socket, this.user_authenticated.getName());
        client_requesting_download.setDownload_size_in_bytes(file_size_bytes);
        this.download_requests_buffer.push(client_requesting_download);

        /*
        try {
            Thread.sleep(7500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */

        /*END: TEMPORARY CODE*/

        this.out_writer.print(content_download_sb.toString());
        this.out_writer.flush();

        //Sent file size to client
        StringBuilder download_request = new StringBuilder();
        download_request.append(file_size_bytes).append("\n");
        download_request.append(m.getTitle()).append("\n");
        this.out_writer.print(download_request.toString());
        this.out_writer.flush();

        byte[] chunk = new byte[config.getMAX_SIZE()];

        FileInputStream fis = new FileInputStream(download_file);

        int bytes_written = 0;
        int chunk_nr = 0;
        int bytes_sum = 0;

        while ((bytes_written = fis.read(chunk)) > 0) {

            out_writer.println(Base64.getEncoder().encodeToString(Arrays.copyOfRange(chunk, 0, bytes_written)));
            out_writer.flush();

            GeneralMessage.show(3, "download: " + this.user_authenticated.getName(), "Sent chunk " + chunk_nr + " of size " + bytes_written, false);

            bytes_sum += bytes_written;

            chunk_nr++;
        }

        System.out.println();
        GeneralMessage.show(3, "status: " + this.user_authenticated.getName(), "Chunk size sum = " + bytes_sum + " bytes | File size = " + file_size_bytes + " bytes | Checksum = " + (bytes_sum==file_size_bytes?"good":"corrupted"), false);
        System.out.println();

        try {
            this.download_requests_buffer.pop();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

