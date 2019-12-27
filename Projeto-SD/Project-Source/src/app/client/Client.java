package app.client;

import app.utils.Config;
import app.utils.GUI;
import app.utils.GeneralMessage;
import app.utils.Input;
import com.sun.tools.javac.Main;

import java.io.*;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Client {

    //------------------------------------------------------------------------------------------------------------------

    private static int port = 12345;
    private static String host = "localhost"; //192.168.1.67
    private static long CLIENT_PID;

    private static String client_name;

    private static Config config;

    //------------------------------------------------------------------------------------------------------------------

    private static Socket client_socket;

    private static Scanner keyboard_input;

    private static BufferedReader in_socket;
    private static PrintWriter out_socket;

    //------------------------------------------------------------------------------------------------------------------

    private static void init_client() throws IOException {

        config = new Config();
        config.init();
        host = config.getServer_host_address();
        port = config.getServer_port();

        CLIENT_PID = ProcessHandle.current().pid();

        client_socket = new Socket(host, port);

        System.out.println("[client (" + client_socket.getLocalSocketAddress() + ")] connected to server...");

        keyboard_input = new Scanner(System.in);
        in_socket = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
        out_socket = new PrintWriter(client_socket.getOutputStream());
        out_socket.flush();

        client_name = "";
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

                System.out.print("option> ");

                char option = Input.read_char(keyboard_input);

                switch (Character.toLowerCase(option)) {

                    case 'e': //e: Exit the system

                        System.out.println("Goodbye =)");

                        stop = true;

                        break;

                    case 'a': //a: Authenticate

                        String name, pass;

                        System.out.print("Name: ");
                        name = Input.read_string(keyboard_input);

                        System.out.print("Password: ");
                        pass = Input.read_string(keyboard_input);

                        authenticate_message(name, pass);

                        break;

                    case 'r': //r: Register

                        System.out.println("> Insert your credentials:");

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

        client_name = name;

        GUI.menu_after_login(name);

        boolean stop = false;

        while (!stop) {

            System.out.print("option> ");

            char option = Input.read_char(keyboard_input);

            switch (Character.toLowerCase(option)) {

                case 's': //search

                    String tag_to_search;

                    System.out.print("tag> ");
                    tag_to_search = Input.read_string(keyboard_input);

                    search_message(tag_to_search);
                    
                    break;

                case 'u': //upload

                    String title, artist;
                    int year;
                    String[] tags_splitted;

                    System.out.print("title: ");
                    title = Input.read_string(keyboard_input);
                    System.out.print("artist: ");
                    artist = Input.read_string(keyboard_input);
                    System.out.print("year: ");
                    year = Input.read_year(keyboard_input);

                    System.out.print("insert some tags (separated by a space): ");

                    tags_splitted = Input.read_string(keyboard_input).split("\\s+");

                    Set<String> SET_OF_TAGS = new HashSet<>();

                    if (tags_splitted.length == 0) {

                        SET_OF_TAGS.add("not_set");

                    } else {

                        SET_OF_TAGS = new HashSet<>(Arrays.asList(tags_splitted));
                    }

                    upload_message(title, artist, year, SET_OF_TAGS);

                    break;

                case 'd'://download

                    System.out.print("Content ID? (press 0 to go back and search): ");

                    int id = Input.read_integer(keyboard_input);

                    if (id == 0) break;

                    download_message(id);

                    break;

                case 'o'://logout

                    System.out.println("Going back to main menu...");

                    logout_message();

                    break;

                case 'h':

                    GUI.clear_screen();
                    RUN_after_login(name);

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

        if (reply_parts[0].equals("welcome")) {

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

        String[] reply_parts = reply_from_server.split("\\s+");

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

            if (reply_from_server.equals("There is no content with that tag...")) {

                GeneralMessage.show(1, "server", reply_from_server, false);

                return;

            } else {

                try {

                    int number_of_results = Integer.parseInt(reply_from_server), current = 0;
                    String result_line;

                    System.out.print("\n\tResults (" + number_of_results + "):\n");

                    while(current < number_of_results) {

                        result_line = in_socket.readLine();
                        System.out.println("\t#" + current + "| " + result_line);
                        current++;
                    }

                } catch (NumberFormatException e) {

                    e.printStackTrace();
                }

            }

        } catch (IOException e) {

            GeneralMessage.show(0, "client", "could not read server reply", false);
        }

        System.out.println();
    }

    private static void logout_message() {

        out_socket.println("LOGOUT\n" + client_name);
        out_socket.flush();

        try {

            String reply_from_server = in_socket.readLine();

            if (reply_from_server.equals("logout successfull")) {

                main(null);
            }

            GeneralMessage.show(1, "server", reply_from_server, false);

        } catch (Exception e) {

            GeneralMessage.show(0, "client", "could not read server reply", false);
        }
    }

    private static void upload_message(String title, String artist, int year, Set<String> set_of_tags) {

        String tags = set_of_tags.stream().map(s -> s + " ").collect(Collectors.joining());
        String music_description = title + ":" + artist + ":" + year + ":" + tags;

        StringBuilder uploadMessage = new StringBuilder();

        File user_folder = new File(config.getUser_upload_path());
        File[] files_in_folder = user_folder.listFiles();
        File matching_file = null;

        boolean exists = false;

        for (File f: files_in_folder) {

            if (f.isFile()) {

                String[] filename_parts = f.getName().split("\\.(?=[^\\.]+$)");

                if (filename_parts[0].equalsIgnoreCase(title)) {

                    exists = true;
                    matching_file = f;
                    break;
                }

            }
        }

        if (exists) {

            double bytes = matching_file.length();

            uploadMessage.append("UPLOAD_REQUEST\n").append(bytes).append("\n");
            uploadMessage.append(music_description).append("\n");

            double kilobytes = bytes / 1024;

            System.out.println();

            out_socket.print(uploadMessage.toString());
            out_socket.flush();

            String reply_from_server = "";

            try {

                reply_from_server = in_socket.readLine();

            } catch (Exception e) {

                GeneralMessage.show(0, "client", "could not read server reply", false);
            }

            if (reply_from_server.equals("Music already exists with that title!")) {

                GeneralMessage.show(1, "server", reply_from_server, false);
                return;

            } else {

                GeneralMessage.show(1, "server", reply_from_server, false);
            }

            GeneralMessage.show(1, "client", "Requesting a " + kilobytes + " kb file upload to the server...\n", false);

            try {

                byte[] chunk = new byte[config.getMAX_SIZE()];

                FileInputStream fis = new FileInputStream(matching_file);
                DataOutputStream dos = new DataOutputStream(client_socket.getOutputStream());

                int count = 0, r = 0, sum = 0;
                while ((count = fis.read(chunk)) > 0) {

                    dos.write(chunk, 0, count);
                    dos.flush();

                    GeneralMessage.show(1, "client", "Sent chunk " + r + " of size " + count, false);

                    sum+= count;

                    r++;
                }

                System.out.println();
                GeneralMessage.show(1, "status", "Chunk size sum = " + sum + " bytes | File size = " + bytes + " bytes | Checksum = " + (sum==bytes?"good":"corrupted"), false);
                System.out.println();

            } catch (IOException e) {

                GeneralMessage.show(0, "server", "Error reading server response. Try to upload later...", true);
                return;
            }

        } else {

            GeneralMessage.show(0, "error", "file with the given title doesn't exist", false);
        }
    }

    private static void download_message(int id) {

        System.out.println();
        GeneralMessage.show(1, "client", "Requesting download of content ID = " + id + "...", false);

        StringBuilder msg = new StringBuilder();

        msg.append("DOWNLOAD_REQUEST\n");
        msg.append(id).append("\n");

        out_socket.print(msg.toString());
        out_socket.flush();

        String reply_1_from_server = "";
        String reply_2_from_server = "";

        try {

            reply_1_from_server = in_socket.readLine();

        } catch (Exception e) {

            GeneralMessage.show(0, "client", "could not read server reply\n", false);
        }

        if (reply_1_from_server.equals("Content was not found on server database!")) {

            GeneralMessage.show(1, "server", reply_1_from_server + "\n", false);

            return;

        } else { //Can dowload...

            GeneralMessage.show(1, "server", reply_1_from_server, false);

            try {

                reply_2_from_server = in_socket.readLine();

            } catch (Exception e) {

                GeneralMessage.show(0, "client", "could not read server reply", false);
            }
        }

        //download should be starting at this point

        //1: get file bytes from server reply
        String bytes_as_string = "";
        String file_name = "";
        try {

            bytes_as_string = in_socket.readLine();
            file_name = in_socket.readLine();

        } catch (IOException e) {
            GeneralMessage.show(0, "client", "could not read server reply", false);
        }

        double bytes = Double.parseDouble(bytes_as_string);

        //2: create output files and data streams

        int count = 0, r = 0, sum = 0;

        FileOutputStream fos = null;
        DataInputStream dis = null;
        try {

            fos = new FileOutputStream(new File(config.getUser_upload_path() + file_name));
            dis = new DataInputStream(client_socket.getInputStream());

        } catch (FileNotFoundException e) { e.printStackTrace(); }
          catch (IOException e) { e.printStackTrace(); }

        //3: get all chunks from server answers

        byte[] chunk = new byte[config.getMAX_SIZE()];

        try {

            while ((count = dis.read(chunk)) > 0) {

                GeneralMessage.show(3, "download", "Got/Wrote chunk " + r + " of size " + count + " from " + client_socket.getRemoteSocketAddress(), false);

                r++;
                sum += count;

                fos.write(chunk, 0, count);
                fos.flush();

                if (sum == bytes) break;
            }

        } catch (IOException e) { GeneralMessage.show(3, "status", "could not read/write chunk", false); }

        GeneralMessage.show(3, "status", "Chunk size sum = " + sum + " bytes | File size = " + bytes + " bytes | Checksum = " + (sum==bytes?"good":"corrupted"), false);
        System.out.println();
        GeneralMessage.show(1, "server", reply_2_from_server + "\n", false);
        System.out.println();
    }
}
