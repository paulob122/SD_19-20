
package app.utils;

public class GeneralMessage {

    private static int number_of_tabs;
    private static String source;
    private static String message;
    private static boolean store_time_and_hour;

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
