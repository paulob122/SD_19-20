package app.server;


import app.model.FileSharingSystem;
import app.utils.GeneralMessage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    //------------------------------------------------------------

    private int port = 12345;
    private ServerSocket server_socket;
    private FileSharingSystem system;

    //------------------------------------------------------------

    public Server(int port, FileSharingSystem fss) throws IOException {

        this.port = port;

        this.server_socket = new ServerSocket(port);
        this.system = fss;
    }

    //------------------------------------------------------------

    public boolean start() {

        GeneralMessage.show(1, "server", "running...", true);

        //------------------------------------------------------------------------------------

        while(true) {

            Socket client_socket = null;

            try {

                client_socket = server_socket.accept();
                GeneralMessage.show(1, "server", "client connected...", true);

            }
            catch (IOException e) { GeneralMessage.show(1, "server", "client could not connect", false); }

            //------------------------------------------------------------------------------------

            ServerWorker sv_worker = null;

            try {

                sv_worker = new ServerWorker(client_socket, this.system);

            } catch (IOException e) { GeneralMessage.show(1, "server", "error running server worker...", false);}

            //------------------------------------------------------------------------------------

            new Thread(sv_worker).start();

            //------------------------------------------------------------------------------------
        }

    }
}
