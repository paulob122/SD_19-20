

package app;

import app.server.Server;
import app.utils.DateAndTime;

import java.io.IOException;
import java.util.Date;

public class ServerApp {

    private static final int port = 12345;

    public static void main(String[] args) {

        System.out.println("[app: " + DateAndTime.get_current_time() + "] system app is running...");
        long millis_start = System.currentTimeMillis();

        Server server = null;

        try {

            server = new Server(port);

        } catch (IOException e) {

            System.out.println("\t[Server_Error] Cannot create server socket for port number " + port);
        }

        server.start();

        long millis_finish = System.currentTimeMillis();
        long seconds = (millis_finish - millis_start) / 1000;

        System.out.println("\t[server: " + DateAndTime.get_current_time() + "] disconnected...");

        System.out.println("[app: "+ DateAndTime.get_current_time() + "] running finished...");
        System.out.println("\n[session: up for " + seconds + " seconds]");
    }
}
