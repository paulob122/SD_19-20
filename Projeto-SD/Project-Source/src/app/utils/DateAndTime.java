package app.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * To get the current time as a string.
 *
 * @author Grupo 19
 * @version 2020/01/01
 */
public class DateAndTime {

    /**
     * @return current time as a string
     */
    public static String get_current_time() {

        SimpleDateFormat formatter= new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());

        return formatter.format(date);
    }
}
