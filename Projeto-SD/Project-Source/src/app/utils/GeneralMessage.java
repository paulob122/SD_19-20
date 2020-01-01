
package app.utils;

/**
 * Shows an error/hint/warning message as a formatted text to show on console.
 *
 * @author Grupo 19
 * @version 2020/01/01
 */
public class GeneralMessage {

    /**
     * number of tabs on the message
     */
    private static int number_of_tabs;
    /**
     * message source
     */
    private static String source;
    /**
     * message
     */
    private static String message;
    /**
     * true if you want to store time and hour the message occurs
     */
    private static boolean store_time_and_hour;

    /**
     * Shows the message as a string formatted by the parameters
     * @param tabs number of tabs
     * @param src message source
     * @param messg message
     * @param time to show time
     */
    public static void show(int tabs,
                              String src,
                              String messg,
                              boolean time) {

        number_of_tabs = tabs;
        source = src;
        message = messg;
        store_time_and_hour = time;

        System.out.println(format());
    }

    /**
     * @return a formatted general message wit the parameters given as parameter.
     */
    private static String format() {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < number_of_tabs; i++) {

            sb.append("\t");
        }

        sb.append("[").append(source);

        if (store_time_and_hour) {

            sb.append(": ");
            sb.append(DateAndTime.get_current_time());
        }

        sb.append("] ");
        sb.append(message);

        return sb.toString();
    }
}
