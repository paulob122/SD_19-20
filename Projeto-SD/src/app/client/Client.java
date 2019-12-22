package app.client;

import app.utils.GUI;
import app.utils.GeneralMessage;
import app.utils.Input;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    //------------------------------------------------------------------------------------------------------------------

    private static final int port = 12345;
    private static final String host = "localhost";

    //------------------------------------------------------------------------------------------------------------------

    private static Socket client_socket;

    private static Scanner keyboard_input;

    private static BufferedReader in_socket;
    private static PrintWriter out_socket;

    //------------------------------------------------------------------------------------------------------------------

    private static void init_client() throws IOException {

        client_socket = new Socket(host, port);

        System.out.println("[client] connected to server...");

        keyboard_input = new Scanner(System.in);
        in_socket = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
        out_socket = new PrintWriter(client_socket.getOutputStream());
    }

    //------------------------------------------------------------------------------------------------------------------

    public static void main(String[] args) {

        //--------------------------------------------------------------------------------------------------------------

        try { init_client(); } catch (IOException e) {
            GeneralMessage.show(0, "client", "initialization failed...", true);
        }

        //--------------------------------------------------------------------------------------------------------------

        String reply_from_server = "not_set";

        //--------------------------------------------------------------------------------------------------------------

        GUI.main_menu();

        try {

            boolean stop = false;

            while (stop == false) {

                char option = Input.read_char(keyboard_input);

                switch (option) {

                    case 'e': //e: Exit the system

                        System.out.println("EXIT");

                        stop = true;

                        break;

                    case 'a': //a: Authenticate

                        System.out.println("AUTHENTICATE");

                        String name, pass;

                        System.out.print("Name: ");
                        name = keyboard_input.nextLine();

                        System.out.print("Password: ");
                        pass = keyboard_input.nextLine();

                        login(name, pass);

                        break;

                    case 'r': //r: Register


                        break;

                    default:

                        GeneralMessage.show(0, "client", "insert a valid option", false);

                        break;
                }
            }

        } catch (Exception e) {


        }

        try {

            client_socket.shutdownInput();
            client_socket.shutdownOutput();
            client_socket.close();

        } catch (Exception e) {

            GeneralMessage.show(0, "client", "error closing socket and buffers...", true);
        }

        GeneralMessage.show(0, "client", "disconnected with exit command...", true);
    }

    //------------------------------------------------------------------------------------------------------------------

    private static int login(String name, String pass) {

        out_socket.println("AUTHENTICATE\n" + name + " " + pass);
        out_socket.flush();

        String reply_from_server = "";

        try {

            reply_from_server = in_socket.readLine();

        } catch (IOException e) { e.printStackTrace(); }

        if (reply_from_server == null) {

            return -1;
        }

        GeneralMessage.show(1, "server", reply_from_server, false);

        return 1;
    }

    //------------------------------------------------------------------------------------------------------------------

}
