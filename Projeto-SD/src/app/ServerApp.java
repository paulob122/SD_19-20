
package app;

import app.model.FileSharingSystem;
import app.server.Server;
import app.utils.DateAndTime;
import app.utils.GeneralMessage;

import java.io.IOException;

public class ServerApp {

    private static final int port = 12345;

    public static void main(String[] args) {

        //--------------------------------------------------------------------------------------------------------------

        FileSharingSystem fss = new FileSharingSystem();

        fss.register_user("zizu", "secretpass1234");

        System.out.println(fss.toString());

        //--------------------------------------------------------------------------------------------------------------

        GeneralMessage.show(0, "app", "system is running", true);

        long millis_start = System.currentTimeMillis();

        //--------------------------------------------------------------------------------------------------------------

        Server server = null;

        try { server = new Server(port, fss); }
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
