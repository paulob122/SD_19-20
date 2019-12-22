package app.server;

import app.model.FileSharingSystem;
import app.model.users.User;
import app.utils.GeneralMessage;

import java.io.*;
import java.net.Socket;

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

        this.client_socket = client_socket;

        this.fss_system = system;

        this.user_authenticated = null;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void run() {

        try {

            GeneralMessage.show(2, "worker", "initialized...", false);

            String message_from_client, message_to_client;

            while ((message_from_client = this.in_reader.readLine()) != null) {

                switch (message_from_client) {

                    case "AUTHENTICATE":

                        authenticate();

                        break;

                    default:

                        break;
                }

                GeneralMessage.show(2, "worker", "got: " + message_from_client, false);

                this.out_writer.println(message_from_client);
                this.out_writer.flush();
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
}
