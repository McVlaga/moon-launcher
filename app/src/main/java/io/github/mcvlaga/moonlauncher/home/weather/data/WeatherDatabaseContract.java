package io.github.mcvlaga.moonlauncher.home.weather.data;

import android.net.Uri;
import android.provider.BaseColumns;

import java.util.Calendar;

public class WeatherDatabaseContract {

    public static final String CONTENT_AUTHORITY = "io.github.mcvlaga.moonlauncher.weather";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_WEATHER = "weather";
    public static final String PATH_HOURLY = "hourly";
    public static final String PATH_DAILY = "daily";

    public static final class WeatherEntry implements BaseColumns {

        public static final Uri CONTENT_URI_WEATHER = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_WEATHER)
                .build();

        public static final Uri CONTENT_URI_WEATHER_HOURLY = CONTENT_URI_WEATHER.buildUpon()
                .appendPath(PATH_HOURLY)
                .build();

        public static final Uri CONTENT_URI_WEATHER_DAILY = CONTENT_URI_WEATHER.buildUpon()
                .appendPath(PATH_DAILY)
                .build();

        public static final String TABLE_NAME_WEATHER_HOURLY = "hourly";
        public static final String TABLE_NAME_WEATHER_DAILY = "daily";

        public static final String COLUMN_NAME_TIME = "time";
        public static final String COLUMN_NAME_SUMMARY = "summary";
        public static final String COLUMN_NAME_ICON = "icon";
        public static final String COLUMN_NAME_PRECIP_PROB = "precip_probability";
        public static final String COLUMN_NAME_PRECIP_TYPE = "precip_type";
        public static final String COLUMN_NAME_HUMIDITY = "humidity";
        public static final String COLUMN_NAME_PRESSURE = "pressure";
        public static final String COLUMN_NAME_WIND_SPEED = "wind";

        public static final String COLUMN_NAME_SUNRISE_TIME = "sunrise";
        public static final String COLUMN_NAME_SUNSET_TIME = "sunset";
        public static final String COLUMN_NAME_TEMP = "temp";
        public static final String COLUMN_NAME_APPARENT_TEMP = "apparent";

        public static final String COLUMN_NAME_LOW_TEMP = "low";
        public static final String COLUMN_NAME_HIGH_TEMP = "high";

        public static String getSqlSelectForNowOnwards() {
            Calendar now = Calendar.getInstance();
            now.add(Calendar.HOUR, 1);
            long nowPlusOneHour = now.getTimeInMillis();
            return COLUMN_NAME_TIME + " > " + System.currentTimeMillis() + " and "
                    + COLUMN_NAME_TIME + " < " + nowPlusOneHour;
        }

        public static String getSqlSelectForTodayOnwards() {
            return WeatherEntry.COLUMN_NAME_TIME + " > " + System.currentTimeMillis();
        }
    }
}
