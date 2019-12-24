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
    private static final String host = "localhost"; //192.168.1.67
    private static long CLIENT_PID;

    private static String client_name;

    //------------------------------------------------------------------------------------------------------------------

    private static Socket client_socket;

    private static Scanner keyboard_input;

    private static BufferedReader in_socket;
    private static PrintWriter out_socket;

    //------------------------------------------------------------------------------------------------------------------

    private static void init_client() throws IOException {

        CLIENT_PID = ProcessHandle.current().pid();

        client_socket = new Socket(host, port);

        System.out.println("[client (" + client_socket.getLocalSocketAddress() + ")] connected to server...");

        keyboard_input = new Scanner(System.in);
        in_socket = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
        out_socket = new PrintWriter(client_socket.getOutputStream());
        out_socket.flush();
    }

    //------------------------------------------------------------------------------------------------------------------

    public static void main(String[] args) {

        //--------------------------------------------------------------------------------------------------------------

        GUI.clear_screen();

        //--------------------------------------------------------------------------------------------------------------

        try { init_client(); } catch (IOException e) {
            GeneralMessage.show(0, "client", "initialization failed...", true);
        }

        //--------------------------------------------------------------------------------------------------------------

        try {

            GUI.main_menu();

            boolean stop = false;

            while (stop == false) {

                char option = Input.read_char(keyboard_input);

                switch (Character.toLowerCase(option)) {

                    case 'e': //e: Exit the system

                        System.out.println("option> EXIT");

                        stop = true;

                        break;

                    case 'a': //a: Authenticate

                        System.out.println("option> AUTHENTICATE:");

                        String name, pass;

                        System.out.print("Name: ");
                        name = Input.read_string(keyboard_input);

                        System.out.print("Password: ");
                        pass = Input.read_string(keyboard_input);

                        authenticate_message(name, pass);

                        break;

                    case 'r': //r: Register

                        System.out.println("option> REGISTER:");

                        System.out.print("Name: ");
                        name = Input.read_string(keyboard_input);

                        System.out.print("Password: ");
                        pass = Input.read_string(keyboard_input);

                        register_message(name, pass);

                        break;

                    default:

                        GeneralMessage.show(0, "client", "insert a valid option", false);

                        break;
                }
            }

        } catch (Exception e) {

            GeneralMessage.show(0, "client", "connection failed!", true);
        }

        try {

            client_socket.shutdownInput();
            client_socket.shutdownOutput();
            client_socket.close();

        } catch (Exception e) {

            GeneralMessage.show(0, "client", "cannot close socket and buffers...", true);
        }

        GeneralMessage.show(0, "client", "client disconnected...", true);
    }

    private static void RUN_after_login(String name) {

        GUI.menu_after_login(name);

        boolean stop = false;

        while (stop == false) {

            char option = Input.read_char(keyboard_input);

            switch (Character.toLowerCase(option)) {

                case 's':

                    System.out.println("option> SEARCH:");

                    String tag_to_search;

                    System.out.print("Insert a tag: ");
                    tag_to_search = Input.read_string(keyboard_input);

                    search_message(tag_to_search);
                    
                    break;

                case 'u':

                    break;

                case 'd':

                    break;

                case 'o':

                    System.out.println("option>LOGOUT");

                    logout_message();

                    break;

                default:

                    GeneralMessage.show(0, "client", "insert a valid option", false);

                    break;
            }

        }

    }

    //------------------------------------------------------------------------------------------------------------------

    private static void authenticate_message(String name, String pass) {

        out_socket.println("AUTHENTICATE\n" + name + " " + pass);
        out_socket.flush();

        String reply_from_server = "";

        try {

            reply_from_server = in_socket.readLine();

        } catch (IOException e) { e.printStackTrace(); }

        if (reply_from_server == null) {

            return;
        }

        GeneralMessage.show(1, "server", reply_from_server, false);

        String[] reply_parts = reply_from_server.split("\\s+");

        if (reply_parts[0].equals("welcome") || reply_parts[0].equals("you")) {

            GUI.clear_screen();
            RUN_after_login(name);
            client_name = name;
        }
    }

    private static void register_message(String name, String pass) {

        out_socket.println("REGISTER\n" + name + " " + pass);
        out_socket.flush();

        String reply_from_server = "";

        try {

            reply_from_server = in_socket.readLine();

        } catch (IOException e) { e.printStackTrace(); }

        if (reply_from_server == null) {

            return;
        }

        GeneralMessage.show(1, "server", reply_from_server, false);

        String reply_parts[] = reply_from_server.split("\\s+");

        if (reply_parts[0].equals("Registration")) {

            GUI.clear_screen();
            RUN_after_login(name);
            client_name = name;
        }
    }

    private static void search_message(String tag_to_search) {

        out_socket.println("SEARCH\n" + tag_to_search);
        out_socket.flush();

        String reply_from_server = "";

        try {

            reply_from_server = in_socket.readLine();

            if (reply_from_server.equals("There is no content with the that tag...")) {

                GeneralMessage.show(1, "server", reply_from_server, false);

                return;

            } else {

                try {

                    int number_of_results = Integer.parseInt(reply_from_server), current = 0;
                    String result_line;

                    System.out.print("\n\tResults (" + number_of_results + "):\n");

                    while(current < number_of_results) {

                        result_line = in_socket.readLine();
                        System.out.println("\t# " + current + " = " + result_line);
                        current++;
                    }

                } catch (NumberFormatException e) {

                    e.printStackTrace();
                }

            }

        } catch (IOException e) { e.printStackTrace(); }
    }

    private static void logout_message() {

        out_socket.println("LOGOUT\n" + client_name);
        out_socket.flush();

        try {

            String reply_from_server = in_socket.readLine();

            GeneralMessage.show(1, "server", reply_from_server, false);

        } catch (Exception e) {

        }
    }
}
