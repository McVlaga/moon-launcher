package io.github.mcvlaga.moonlauncher.home.weather.utils;

import android.content.Context;

import io.github.mcvlaga.moonlauncher.R;
import io.github.mcvlaga.moonlauncher.home.data.HomePreferences;

public class WeatherUtils {

    public static String getTemperature(Context context, int temperature) {
        int temperatureFormatResourceId = R.string.format_temperature;

        /* For presentation, assume the user doesn't care about tenths of a degree. */
        return String.format(context.getString(temperatureFormatResourceId), temperature);
    }

    public static String getApparentTemperature(Context context, int temperature) {
        int temperatureFormatResourceId = R.string.format_apparent_temperature;

        /* For presentation, assume the user doesn't care about tenths of a degree. */
        return String.format(context.getString(temperatureFormatResourceId), temperature);
    }

    public static String getHumidity(Context context, int humidity) {
        int humidityFormatResourceId = R.string.format_humidity;

        return String.format(context.getString(humidityFormatResourceId), humidity);
    }

    public static String getWindSpeed(Context context, int windSpeed) {
        int windSpeedFormatResourceId;
        if (HomePreferences.isMetric(context)) {
            windSpeedFormatResourceId = R.string.format_wind_speed_metric;
        } else {
            windSpeedFormatResourceId = R.string.format_wind_speed_imperial;
        }
        return String.format(context.getString(windSpeedFormatResourceId), windSpeed);
    }

    public static String getPreferedWeatherDegreesUnit(Context context) {
        if (HomePreferences.isMetric(context)) {
            return context.getString(R.string.weather_degrees_metric);
        } else {
            return context.getString(R.string.weather_degrees_imperial);
        }
    }

    public static String getCityName(Context context) {
        return HomePreferences.getLocationCity(context);
    }

    public static int getSmallArtResourceIdForWeatherCondition(String condition) {

        switch (condition) {
            case "clear-day":
                return R.drawable.weather_small_clear_day;
            case "clear-night":
                return R.drawable.weather_small_clear_night;
            case "rain":
                return R.drawable.weather_small_rain;
            case "snow":
                return R.drawable.weather_small_snow;
            case "sleet":
                return R.drawable.weather_small_rain;
            case "wind":
                return R.drawable.weather_small_wind;
            case "fog":
                return R.drawable.weather_small_fog;
            case "cloudy":
                return R.drawable.weather_small_cloudy;
            case "partly-cloudy-day":
                return R.drawable.weather_small_partly_cloudy_day;
            case "partly-cloudy-night":
                return R.drawable.weather_small_clear_day;
            default:
                return R.drawable.weather_small_cloudy;
        }
    }



    public static int getLargeArtResourceIdForWeatherCondition(String condition) {

        switch (condition) {
            case "clear-day":
                return R.drawable.weather_large_clear_day;
            case "clear-night":
                return R.drawable.weather_large_clear_night;
            case "rain":
                return R.drawable.weather_large_rain;
            case "snow":
                return R.drawable.weather_large_snow;
            case "sleet":
                return R.drawable.weather_large_rain;
            case "wind":
                return R.drawable.weather_large_wind;
            case "fog":
                return R.drawable.weather_large_fog;
            case "cloudy":
                return R.drawable.weather_large_cloudy;
            case "partly-cloudy-day":
                return R.drawable.weather_large_partly_cloudy_day;
            case "partly-cloudy-night":
                return R.drawable.weather_large_partly_cloudy_night;
            default:
                return R.drawable.weather_large_cloudy;
        }
    }
}
