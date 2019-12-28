package app.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Config {

    //------------------------------------------------------------------------

    private static final String config_path = "../config/config.cnf";

    //------------------------------------------------------------------------

    private String user_upload_path;
    private String server_db_path;
    private String server_host_address;
    private int server_port;
    private int MAX_SIZE; //in bytes
    private int MAX_DOWN;

    //------------------------------------------------------------------------

    public Config() {

        this.user_upload_path = "";
        this.server_db_path = "";
        this.server_host_address = "";
        this.server_port = -1;
        this.MAX_SIZE = -1;
        this.MAX_DOWN = 0;
    }

    //------------------------------------------------------------------------

    public void init() throws IOException, NumberFormatException {

        File config_file = new File(config_path);

        BufferedReader br = new BufferedReader(new FileReader(config_file));

        String st;
        List<String> config_values = new ArrayList<>();
        while ((st = br.readLine()) != null) {

            String[] parts = st.split("=");

            config_values.add(parts[1]);
        }

        this.user_upload_path = config_values.get(0);
        this.server_db_path = config_values.get(1);
        this.server_host_address = config_values.get(2);
        this.server_port = Integer.parseInt(config_values.get(3));
        this.MAX_SIZE = Integer.parseInt(config_values.get(4));
        this.MAX_DOWN = Integer.parseInt(config_values.get(5));
    }

    public int getMAX_SIZE() {
        return MAX_SIZE; //result in bytes
    }

    public int getMAX_DOWN() {
        return MAX_DOWN; //result in bytes
    }

    public static String getConfig_path() {
        return config_path;
    }

    public String getUser_upload_path() {
        return user_upload_path;
    }

    public String getServer_db_path() {
        return server_db_path;
    }

    public String getServer_host_address() {
        return server_host_address;
    }

    public int getServer_port() {
        return server_port;
    }
}
