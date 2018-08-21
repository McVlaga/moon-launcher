package io.github.mcvlaga.moonlauncher.utils;

import android.content.Context;
import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Class for handling date conversions that are useful for Sunshine.
 */
public final class HomeDateUtils {

    /* Milliseconds in a day */
    public static final long DAY_IN_MILLIS = TimeUnit.DAYS.toMillis(1);
    public static final long HOUR_IN_MILLIS = TimeUnit.HOURS.toMillis(1);

    public static long getMillisFromDate(String dateString) throws ParseException {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date date = dateFormat.parse(dateString);
        return date.getTime();
    }

    public static long getMillisFromTime(String timeString) throws ParseException {
        final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa");
        Date time = timeFormat.parse(timeString);
        long timeInMillisUTC = time.getTime();
        int offset = TimeZone.getDefault().getOffset(timeInMillisUTC);
        return timeInMillisUTC + offset;
    }

    public static long getMillisecondsForTodayMidnight(int days) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);

        cal.add(Calendar.DATE, days);

        return cal.getTimeInMillis();
    }

    public static String getFriendlyDateStringForWeather(long millis) {

        long diff = millis - System.currentTimeMillis();

        if (diff <= 0) {
            SimpleDateFormat formatter = new SimpleDateFormat("MMM dd");
            String dateString = formatter.format(new Date(millis));
            return "Today " + dateString;
        } else {
            int days = (int) (diff / (1000*60*60*24));
            if (days == 0) {
                return "Tomorrow";
            } else if (days <= 6) {
                SimpleDateFormat formatter = new SimpleDateFormat("EEEE");
                return formatter.format(new Date(millis));
            } else {
                SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM dd");
                return formatter.format(new Date(millis));
            }
        }
    }

    public static boolean is24HourFormat(Context context) {
        return DateFormat.is24HourFormat(context);
    }
}
