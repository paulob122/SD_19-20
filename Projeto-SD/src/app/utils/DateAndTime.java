package app.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateAndTime {

    public static String get_current_time() {

        SimpleDateFormat formatter= new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());

        return formatter.format(date);
    }

    public static long get_seconds_between_dates(Date x, Date y) {

        long seconds = (y.getTime()-x.getTime())/1000;

        return seconds;
    }
}
