package io.github.mcvlaga.moonlauncher.home.tasks.utils;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.github.mcvlaga.moonlauncher.utils.HomeDateUtils;

import static io.github.mcvlaga.moonlauncher.utils.HomeDateUtils.getMillisecondsForTodayMidnight;

public class TasksUtils {


    public static String getFriendlyDateStringForWidget(Context context, long dateMillis, long timeMillis) {
        if (dateMillis == 0) {
            return "";
        }

        long day = 1000*60*60*24;
        long millis = dateMillis + timeMillis;
        long todayMidnightMillis = getMillisecondsForTodayMidnight(1);
        long diffMidnight = millis - todayMidnightMillis;
        double days = diffMidnight / day;

        if (diffMidnight < 0) {
            if (diffMidnight < -day) {
                SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM d", Locale.getDefault());
                return formatter.format(new Date(millis));
            }
            if (timeMillis == 0) {
                return "Today";
            }
            return getTimeStringForDialog(context, timeMillis);
        } else {
            if (days < 1) {
                if (timeMillis == 0) {
                    return "Tomorrow";
                }
                return "Tomorrow, " + getTimeStringForDialog(context, timeMillis);
            } else if(days <= 5) {
                SimpleDateFormat formatter = new SimpleDateFormat("EEEE", Locale.getDefault());
                return formatter.format(new Date(millis));
            } else {
                SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM d", Locale.getDefault());
                return formatter.format(new Date(millis));
            }
        }
    }


    public static String getDateStringForDialog(long millis) {

        long todayMidnightMillis = getMillisecondsForTodayMidnight(0);
        long diffMidnight = millis - todayMidnightMillis;
        double days = diffMidnight / (1000*60*60*24);

        if (diffMidnight < 0) {
            SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM d", Locale.getDefault());
            return formatter.format(new Date(millis));
        } else {
            if (days >= 0 && days < 1) {
                return "Today";
            } else if (days < 2) {
                return "Tomorrow";
            } else if(days <= 6) {
                SimpleDateFormat formatter = new SimpleDateFormat("EEEE", Locale.getDefault());
                return formatter.format(new Date(millis));
            } else {
                SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM d", Locale.getDefault());
                return formatter.format(new Date(millis));
            }
        }
    }

    public static String getTimeStringForDialog(Context context, long millis) {
        long hourOfDay = TimeUnit.MILLISECONDS.toHours(millis);

        String AM_PM = "";
        if (!HomeDateUtils.is24HourFormat(context)) {
            if (hourOfDay < 12) {
                AM_PM = " AM";
                if (hourOfDay == 0) {
                    hourOfDay = 12;
                }
            } else {
                if (hourOfDay != 12) {
                    hourOfDay -= 12;
                }
                AM_PM = " PM";
            }
        }
        return String.format(
                Locale.getDefault(),
                "%d:%02d%s",
                hourOfDay,
                TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                AM_PM
        );
    }
}
