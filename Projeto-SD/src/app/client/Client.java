package app.client;

import javax.sound.midi.SysexMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    public static void main(String[] args) throws IOException {

        System.out.println("[client] connected to server...");

        System.out.println("---------------------------------------------------");
        System.out.println("               File sharing system");
        System.out.println("---------------------------------------------------\n");

        Socket cs = new Socket("localhost", 12345);

        BufferedReader in = new BufferedReader(new InputStreamReader(cs.getInputStream()));

        PrintWriter out = new PrintWriter(cs.getOutputStream());

        BufferedReader stin = new BufferedReader(new InputStreamReader(System.in));

        String message_to_server, reply_from_server;

        while (!((message_to_server = stin.readLine()).equals("exit"))) {

            out.println(message_to_server);
            out.flush();

            reply_from_server = in.readLine();

            if (reply_from_server == null) break;

            System.out.println("server says: " + reply_from_server);
        }

        cs.shutdownInput();
        cs.shutdownOutput();
        cs.close();

        System.out.println("[client] connection terminated with exit comand.");
    }

}
