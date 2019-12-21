package app.server;

import app.utils.DateAndTime;

import java.io.*;
import java.net.Socket;

public class ServerWorker implements Runnable {

    private BufferedReader in_reader;
    private PrintWriter out_writer;
    private Socket client_socket;

    public ServerWorker(Socket client_socket) throws IOException {

        this.in_reader = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
        this.out_writer = new PrintWriter(client_socket.getOutputStream());

        this.client_socket = client_socket;
    }

    @Override
    public void run() {

        try {

            System.out.println("\t\t[worker: " + DateAndTime.get_current_time() + "] initialized...");

            String message_from_client;
            while ((message_from_client = this.in_reader.readLine()) != null) {

                System.out.println("\t\t[worker] client says = " + message_from_client);

                this.out_writer.println("ok");
                this.out_writer.flush();
            }

            this.client_socket.shutdownOutput();
            this.client_socket.shutdownInput();
            this.client_socket.close();

        } catch (Exception e) {

            System.out.println("\t\t[worker: " + DateAndTime.get_current_time() + "] error closing socket...");
        }

        System.out.println("\t\t[worker: " + DateAndTime.get_current_time() + "] stopped...");
        System.out.println("\t[server: " + DateAndTime.get_current_time() + "] client disconnected...");
    }
}
