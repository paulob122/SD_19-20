package app.server;


import app.utils.DateAndTime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    //------------------------------------------------------------

    private int port = 12345;
    private ServerSocket server_socket;

    //------------------------------------------------------------

    public Server (int port) throws IOException {

        this.port = port;

        this.server_socket = new ServerSocket(port);
    }

    //------------------------------------------------------------

    public boolean start() {

        System.out.println("\t[server: " + DateAndTime.get_current_time() + "] running...");

        while(true) {

            Socket client_socket = null;

            try {
                client_socket = server_socket.accept();
                System.out.println("\t[server: " + DateAndTime.get_current_time() + "] client connected...");
            }
            catch (IOException e) { System.out.println("\t[server] client could not connect..."); }

            //------------------------------------------------------------------------------------

            ServerWorker sv_worker = null;

            try {
                sv_worker = new ServerWorker(client_socket);
            } catch (IOException e) {
                System.out.println("[server] error running server worker...");
            }

            new Thread(sv_worker).start();

            //------------------------------------------------------------------------------------
        }

    }
}
