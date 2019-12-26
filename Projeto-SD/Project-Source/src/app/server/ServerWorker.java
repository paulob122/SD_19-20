package app.server;

import app.model.FileSharingSystem;
import app.model.content.music.Music;
import app.model.users.User;
import app.utils.Config;
import app.utils.GeneralMessage;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class ServerWorker implements Runnable {

    //------------------------------------------------------------------------------------------------------------------

    private User user_authenticated;

    //------------------------------------------------------------------------------------------------------------------

    private BufferedReader in_reader;
    private PrintWriter out_writer;
    private Socket client_socket;
    private FileSharingSystem fss_system;
    private Config config;

    //------------------------------------------------------------------------------------------------------------------

    public ServerWorker(Socket client_socket, FileSharingSystem system, Config config) throws IOException {

        this.in_reader = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
        this.out_writer = new PrintWriter(client_socket.getOutputStream());
        this.out_writer.flush();

        this.client_socket = client_socket;

        this.fss_system = system;

        this.user_authenticated = null;

        this.config = config;
    }

    //------------------------------------------------------------------------------------------------------------------

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

                    case "DOWNLOAD_REQUEST": break;

                    default:
                        GeneralMessage.show(2, "worker", "got: invalid message " + message_from_client + ", from: " + client_socket.getRemoteSocketAddress(), false);
                        break;
                }

            }

            this.client_socket.shutdownOutput();
            this.client_socket.shutdownInput();
            this.client_socket.close();

        } catch (Exception e) {
            e.printStackTrace();
            GeneralMessage.show(2, "worker", "error closing socket...", true);
        }

        GeneralMessage.show(2, "worker", "stopped...", false);
        GeneralMessage.show(1, "server", "client disconnected", true);
    }

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

    private void authenticate() throws IOException {

        String command_content = in_reader.readLine();

        String[] parts = command_content.split("\\s+");

        if (fss_system.is_user_in_session(parts[0])) {

            out_writer.println("you are already authenticated in another session " + parts[0]);
            out_writer.flush();

            return;
        }

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

            this.out_writer.println("Music already exists with that title!\n");
            this.out_writer.flush();

            return;

        } else {

            this.out_writer.println("Uploading: " + fss_system.get_content(id).toString());
            this.out_writer.flush();
        }

        FileOutputStream fos = new FileOutputStream(new File(config.getServer_db_path() + desc_parts[0]));
        DataInputStream dis = new DataInputStream(client_socket.getInputStream());

        int count = 0, r = 0, sum = 0;
        byte[] chunk = new byte[config.getMAX_SIZE()];

        while ((count = dis.read(chunk)) > 0) {

            GeneralMessage.show(3, "upload", "Got/Wrote chunk " + r + " of size " + count + " from " + client_socket.getRemoteSocketAddress(), false);

            r++;
            sum+= count;

            fos.write(chunk, 0, count);
            fos.flush();

            if (sum == bytes) break;
        }

        GeneralMessage.show(3, "status", "Chunk size sum = " + sum + " bytes | File size = " + bytes + " bytes | Checksum = " + (sum==bytes?"good":"corrupted"), false);
        System.out.println();

        //temporary code, causing bugs -----FIXED
        //in_reader.readLine();
    }

}
