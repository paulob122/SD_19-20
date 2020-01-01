package app.server.tools;

import app.utils.GeneralMessage;

import java.io.IOException;
import java.net.Socket;

/**
 * Stores an identification of a Client connected via socket to manage a download request.
 *
 * @author Grupo 19
 * @version 2020/01/01
 */
public class ClientIdentifier {

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Client socket.
     */
    private Socket client_socket;
    /**
     * Client username.
     */
    private String userName;
    /**
     * Cliente download size request
     */
    private double download_size_in_bytes;

    //------------------------------------------------------------------------------------------------------------------

    /**
     * ClienteIdentifier parametrized constructor
     * @param client_socket client socket
     * @param name user name
     */
    public ClientIdentifier(Socket client_socket, String name) {
        this.client_socket = client_socket;
        this.userName = name;
    }

    /**
     * Sets up the download size request for the user.
     * @param size download size
     */
    public void setDownload_size_in_bytes(double size) {
        this.download_size_in_bytes = size;
    }

    /**
     * @return a representation of the client as a string
     */
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append("Client ").append(userName).append(" | Addr: ").append(client_socket.getRemoteSocketAddress()).append("\n");

        return sb.toString();
    }

    /**
     * Closes down the Client identification
     */
    public void shutDownClient() {

        try {

            this.client_socket.shutdownOutput();
            this.client_socket.shutdownInput();
            this.client_socket.close();

        } catch (IOException e) {

            GeneralMessage.show(1, "server", "could not shutdown client...", true);
        }
    }

    /**
     * @return user name
     */
    public String getUserName() {

        return this.userName;
    }

    /**
     * @return download size
     */
    public double getDownloadSize() {

        return this.download_size_in_bytes;
    }
}
