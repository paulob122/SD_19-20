package app.server;


import app.model.FileSharingSystem;
import app.server.tools.DownloadRequestsBuffer;
import app.server.tools.ClientIdentifier;
import app.utils.Config;
import app.utils.GeneralMessage;
import sun.misc.Signal;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Server class implements the server operating on a host and waiting for connections on a certain port.
 * Calls out a serverWorker for each connected client and creates a new socket.
 *
 * @author Grupo 19
 * @version 2020/01/01
 */
public class Server {

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Server port to wait for the connections.
     */
    private int port;
    /**
     * Server socket operating in that port
     */
    private ServerSocket server_socket;
    /**
     * Server config.
     */
    private Config config;

    /**
     * Stores all the business and data for the system.
     */
    private FileSharingSystem system;

    /**
     * List of connected clients.
     */
    private List<ClientIdentifier> connected_clients_list;
    /**
     * Stores the queue for download requests.
     */
    private DownloadRequestsBuffer downloadRequestBuffer;

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Parametrized constructor for Server.
     *
     * @param port server port
     * @param fss system
     * @param cnf config
     * @throws IOException server initialization, sockets, serversockets...
     */
    public Server(int port, FileSharingSystem fss, Config cnf) throws IOException {

        this.port = port;

        this.server_socket = new ServerSocket(port);
        this.system = fss;

        this.connected_clients_list = new ArrayList<>();

        this.config = cnf;

        this.port = cnf.getServer_port();

        this.listenTo("INT");

        this.downloadRequestBuffer = new DownloadRequestsBuffer(config);
    }

    //------------------------------------------------------------

    /**
     * Starts the server and waits for client connections.
     * @return true
     */
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

                sv_worker = new ServerWorker(client_socket, this.system, this.config, this.downloadRequestBuffer);

            } catch (IOException e) { GeneralMessage.show(1, "server", "error running server worker...", false);}

            //------------------------------------------------------------------------------------

            new Thread(sv_worker).start();

            //------------------------------------------------------------------------------------
        }
    }

    /**
     * Handles a signal given its name
     * @param signal_name signal type
     */
    private void listenTo(String signal_name) {

        Signal sg = new Signal(signal_name);
        Signal.handle(sg, this::handle);
    }

    /**
     * Handles a signal given its name
     * @param signal signal name
     */
    private void handle(Signal signal) {

        this.connected_clients_list.forEach(ClientIdentifier::shutDownClient);

        GeneralMessage.show(1, "server", "server shutdown", true);

        System.exit(1);
    }
}
