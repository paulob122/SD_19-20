package app.server;


import app.model.FileSharingSystem;
import app.server.tools.ClientIdentifier;
import app.utils.GeneralMessage;
import sun.misc.Signal;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    //------------------------------------------------------------

    private int port = 12345;
    private ServerSocket server_socket;

    private FileSharingSystem system;

    private List<ClientIdentifier> connected_clients_list;

    //------------------------------------------------------------

    public Server(int port, FileSharingSystem fss) throws IOException {

        this.port = port;

        this.server_socket = new ServerSocket(port);
        this.system = fss;

        this.connected_clients_list = new ArrayList<>();

        this.listenTo("INT");
    }

    //------------------------------------------------------------

    public boolean start() {

        GeneralMessage.show(1, "server", "running at port " + port + "...", true);

        //------------------------------------------------------------------------------------

        while(true) {

            Socket client_socket = null;

            try {

                client_socket = server_socket.accept();

                ClientIdentifier clientID = new ClientIdentifier(client_socket, "");
                this.connected_clients_list.add(clientID);

                GeneralMessage.show(1, "server", "client (" + client_socket.getRemoteSocketAddress().toString() + ") connected...", true);

            } catch (IOException e) { GeneralMessage.show(1, "server", "client could not connect", false); }

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

    private void listenTo(String signal_name) {

        Signal sg = new Signal(signal_name);
        Signal.handle(sg, this::handle);
    }

    private void handle(Signal signal) {

        this.connected_clients_list.forEach(ClientIdentifier::shutDownClient);

        GeneralMessage.show(1, "server", "server shutdown", true);

        System.exit(1);
    }
}
