package app.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads out the config.cnf file to a class that separates all the parameters.
 *
 * @author Grupo 19
 * @version 2020/01/01
 */
public class Config {

    //------------------------------------------------------------------------

    /**
     * path to the config file
     */
    private static final String config_path = "../config/config.cnf";

    //------------------------------------------------------------------------

    /**
     * User files path.
     */
    private String user_upload_path;
    /**
     * Server files path
     */
    private String server_db_path;
    /**
     * Server host address
     */
    private String server_host_address;
    /**
     * Server port
     */
    private int server_port;
    /**
     * Max size in a transfer operation in client/server.
     */
    private int MAX_SIZE; //in bytes
    /**
     * Max downloads concurrent
     */
    private int MAX_DOWN;

    //------------------------------------------------------------------------

    /**
     * Creats an empty Config.
     */
    public Config() {

        this.user_upload_path = "";
        this.server_db_path = "";
        this.server_host_address = "";
        this.server_port = -1;
        this.MAX_SIZE = -1;
        this.MAX_DOWN = 0;
    }

    //------------------------------------------------------------------------

    /**
     * Initializes the config reading the parameters from the config.cnf file
     * @throws IOException file opening and reading
     * @throws NumberFormatException converting strings to int
     */
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

    /**
     * @return max size
     */
    public int getMAX_SIZE() {
        return MAX_SIZE; //result in bytes
    }

    /**
     * @return max downloads
     */
    public int getMAX_DOWN() {
        return MAX_DOWN; //result in bytes
    }

    /**
     * @return config path
     */
    public static String getConfig_path() {
        return config_path;
    }

    /**
     * @return user files path
     */
    public String getUser_upload_path() {
        return user_upload_path;
    }

    /**
     * @return server files path
     */
    public String getServer_db_path() {
        return server_db_path;
    }

    /**
     * @return server host
     */
    public String getServer_host_address() {
        return server_host_address;
    }

    /**
     * @return server port
     */
    public int getServer_port() {
        return server_port;
    }
}
