package app.server.tools;

import app.utils.GeneralMessage;

import java.io.IOException;
import java.net.Socket;

public class ClientIdentifier {

    private Socket client_socket;

    public ClientIdentifier(Socket client_socket, String client_name_ID) {
        this.client_socket = client_socket;
    }

    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append("Client socket remote addr: ").append(client_socket.getRemoteSocketAddress()).append("\n");

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
}
