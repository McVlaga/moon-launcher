package io.github.mcvlaga.moonlauncher.home.weather.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import io.github.mcvlaga.moonlauncher.home.data.HomePreferences;

/**
 * These utilities will be used to communicate with the weather servers.
 */
public final class NetworkUtils {

    private static final String DARK_SKY_BASE_URL =
            "https://api.darksky.net/forecast";

    private static final String DARK_SKY_API_KEY =
            "68f6dc18cb61d0a35ebfd49a2c47297b";

    private static final String UNITS_PARAM = "units";
    private static final String units_ca = "ca";
    private static final String units_us = "us";

    private static final String EXCLUDE_PARAM = "exclude";
    private static final String EXTEND_PARAM = "extend";

    private static final String hourly = "hourly";

    private static final String flags = "flags";
    private static final String minutely = "minutely";
    private static final String currently = "currently";

    public static URL getUrl(Context context) {
        double[] coordinates = HomePreferences.getLocationCoordinates(context);
        String units = HomePreferences.getPreferedUnits(context);
        if (units.equals("imperial")) {
            units = units_us;
        } else if(units.equals("metric")) {
            units = units_ca;
        }
        return buildUrlWithLatitudeLongitudeUnits(coordinates[0], coordinates[1], units);
    }

    public static URL buildUrlWithLatitudeLongitudeUnits(Double latitude, Double longitude, String units) {

        Uri weatherQueryUri = Uri.parse(DARK_SKY_BASE_URL).buildUpon()
                .appendPath(DARK_SKY_API_KEY)
                .appendPath(latitude + "," + longitude)
                .appendQueryParameter(UNITS_PARAM, units)
                .appendQueryParameter(EXCLUDE_PARAM, flags + "," + minutely + "," + currently)
                .appendQueryParameter(EXTEND_PARAM, hourly)
                .build();

        try {
            URL weatherQueryUrl = new URL(weatherQueryUri.toString());
            Log.d("huy", "URL: " + weatherQueryUrl);
            return weatherQueryUrl;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            String response = null;
            if (hasInput) {
                response = scanner.next();
            }
            scanner.close();
            return response;
        } finally {
            urlConnection.disconnect();
        }
    }
}