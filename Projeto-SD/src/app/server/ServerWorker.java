package app.server;

import app.model.FileSharingSystem;
import app.model.content.music.Music;
import app.model.users.User;
import app.utils.GeneralMessage;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ServerWorker implements Runnable {

    //------------------------------------------------------------------------------------------------------------------

    private User user_authenticated;

    //------------------------------------------------------------------------------------------------------------------

    private BufferedReader in_reader;
    private PrintWriter out_writer;
    private Socket client_socket;
    private FileSharingSystem fss_system;

    //------------------------------------------------------------------------------------------------------------------

    public ServerWorker(Socket client_socket, FileSharingSystem system) throws IOException {

        this.in_reader = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
        this.out_writer = new PrintWriter(client_socket.getOutputStream());
        this.out_writer.flush();

        this.client_socket = client_socket;

        this.fss_system = system;

        this.user_authenticated = null;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void run() {

        try {

            GeneralMessage.show(2, "worker", "initialized...", false);

            String message_from_client;

            while ((message_from_client = this.in_reader.readLine()) != null) {

                switch (message_from_client) {

                    case "AUTHENTICATE": authenticate(); break;

                    case "REGISTER": register(); break;

                    case "SEARCH": search(); break;

                    case "LOGOUT": logout(); break;

                    default: break;
                }

                GeneralMessage.show(2, "worker", "got: " + message_from_client, false);
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

        System.out.println(fss_system.toString());

        String command_content = in_reader.readLine();

        String[] parts = command_content.split("\\s+");

        if (fss_system.is_user_in_session(parts[0])) {

            out_writer.println("you are already authenticated " + parts[0]);
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

            out_writer.println("There is no content with the that tag...");
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
}
