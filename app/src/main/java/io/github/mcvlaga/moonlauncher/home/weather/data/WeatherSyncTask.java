package io.github.mcvlaga.moonlauncher.home.weather.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import java.net.URL;

import io.github.mcvlaga.moonlauncher.home.weather.utils.DarkSkyJsonUtils;
import io.github.mcvlaga.moonlauncher.home.weather.utils.NetworkUtils;

public class WeatherSyncTask {

    /**
     * Performs the network request for updated weather, parses the JSON from that request, and
     * inserts the new weather information into our ContentProvider. Will notify the user that new
     * weather has been loaded if the user hasn't been notified of the weather within the last day
     * AND they haven't disabled notifications in the preferences screen.
     *
     * @param context Used to access utility methods and the ContentResolver
     */
    synchronized public static void syncWeather(Context context) {


//      COMPLETED (3) Within syncWeather, fetch new weather data

        try {
            /*
             * The getUrl method will return the URL that we need to get the forecast JSON for the
             * weather. It will decide whether to create a URL based off of the latitude and
             * longitude or off of a simple location as a String.
             */
            URL weatherRequestUrl = NetworkUtils.getUrl(context);

            /* Use the URL to retrieve the JSON */
            String jsonWeatherResponse = NetworkUtils.getResponseFromHttpUrl(weatherRequestUrl);

            ContentValues[] weatherHourlyValues = DarkSkyJsonUtils
                    .getWeatherHourlyContentValuesFromJson(jsonWeatherResponse);

            ContentValues[] weatherDailyValues = DarkSkyJsonUtils
                    .getWeatherDailyContentValuesFromJson(jsonWeatherResponse);

            /*
             * In cases where our JSON contained an error code, getWeatherContentValuesFromJson
             * would have returned null. We need to check for those cases here to prevent any
             * NullPointerExceptions being thrown. We also have no reason to insert fresh data if
             * there isn't any to insert.
             */
            if (weatherDailyValues != null && weatherDailyValues.length != 0
                    && weatherHourlyValues != null && weatherHourlyValues.length != 0) {

                ContentResolver contentResolver = context.getContentResolver();

                contentResolver.delete(
                        WeatherDatabaseContract.WeatherEntry.CONTENT_URI_WEATHER_HOURLY,
                        null,
                        null);

                contentResolver.delete(
                        WeatherDatabaseContract.WeatherEntry.CONTENT_URI_WEATHER_DAILY,
                        null,
                        null);

                contentResolver.bulkInsert(
                        WeatherDatabaseContract.WeatherEntry.CONTENT_URI_WEATHER_HOURLY,
                        weatherHourlyValues);

                contentResolver.bulkInsert(
                        WeatherDatabaseContract.WeatherEntry.CONTENT_URI_WEATHER_DAILY,
                        weatherDailyValues);
            }
        } catch (Exception e) {
            /* Server probably invalid */
            e.printStackTrace();
        }
    }

}
