package app.server.tools;

import app.utils.GeneralMessage;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.locks.Condition;

public class ClientIdentifier {

    private Socket client_socket;
    private String userName;
    private double download_size_in_bytes;

    public ClientIdentifier(Socket client_socket, String name) {
        this.client_socket = client_socket;
        this.userName = name;
    }

    public void setDownload_size_in_bytes(double size) {
        this.download_size_in_bytes = size;
    }

    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append("Client ").append(userName).append(" | Addr: ").append(client_socket.getRemoteSocketAddress()).append("\n");

        return sb.toString();
    }

    public void shutDownClient() {

        try {

            this.client_socket.shutdownOutput();
            this.client_socket.shutdownInput();
            this.client_socket.close();

        } catch (IOException e) {

            GeneralMessage.show(1, "server", "could not shutdown client...", true);
        }
    }

    public String getUserName() {

        return this.userName;
    }

    public double getDownloadSize() {

        return this.download_size_in_bytes;
    }
}
