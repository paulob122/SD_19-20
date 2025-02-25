
package app;

import app.model.FileSharingSystem;
import app.server.Server;
import app.utils.Config;
import app.utils.GUI;
import app.utils.GeneralMessage;

import java.io.IOException;

/**
 * Main Server app that loads the config, server socket and runs the server.
 *
 * @author Grupo 19
 * @version 2020/01/01
 */
public class ServerApp {

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Server Port
     */
    private static final int port = 12345;

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Runs the server and initializes the configs
     * @param args not used
     */
    public static void main(String[] args) {

        //--------------------------------------------------------------------------------------------------------------

        GUI.clear_screen();

        //--------------------------------------------------------------------------------------------------------------

        Config cnf = new Config();

        try { cnf.init(); } catch (Exception e) { GeneralMessage.show(0, "app", "error initializing config", true); return;}

        FileSharingSystem fss = new FileSharingSystem();

        fss.register_user("zizu", "pass");
        fss.authenticate("zizu",  "pass");
        fss.logout_user("zizu");

        //--------------------------------------------------------------------------------------------------------------

        GeneralMessage.show(0, "app", "system is running", true);

        long millis_start = System.currentTimeMillis();

        //--------------------------------------------------------------------------------------------------------------

        Server server = null;

        try { server = new Server(port, fss, cnf); }
        catch (IOException e) {
            GeneralMessage.show(0, "app", "cannot create server socket for port number " + port, true);
        }

        server.start();

        //--------------------------------------------------------------------------------------------------------------

        long millis_finish = System.currentTimeMillis();

        GeneralMessage.show(1, "server", "server session ended", true);
        GeneralMessage.show(0, "app", "session last " + ((millis_finish - millis_start) / 1000) + " seconds", true);

        //--------------------------------------------------------------------------------------------------------------
    }
}
