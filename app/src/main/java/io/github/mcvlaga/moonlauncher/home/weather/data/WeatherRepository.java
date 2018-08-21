package io.github.mcvlaga.moonlauncher.home.weather.data;

import android.content.SharedPreferences;

public class WeatherRepository {

    public static final String PREF_COORD_LAT = "coord_lat";
    public static final String PREF_COORD_LONG = "coord_long";

    private SharedPreferences mSharedPreferences;

    public WeatherRepository(SharedPreferences sharedPreferences) {
        mSharedPreferences = sharedPreferences;
    }

    public void setNumberOfForecasts(int numOfForecasts, String key) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, Integer.toString(numOfForecasts));
        editor.apply();
    }

    public void setLocationCity(String key, String city) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, city);
        editor.apply();
    }

    public void setLocationDetails(double lat, double lon) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        editor.putLong(PREF_COORD_LAT, Double.doubleToRawLongBits(lat));
        editor.putLong(PREF_COORD_LONG, Double.doubleToRawLongBits(lon));
        editor.apply();
    }
}
