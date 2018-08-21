package io.github.mcvlaga.moonlauncher.home.weather.utils;

import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import io.github.mcvlaga.moonlauncher.home.weather.data.WeatherDatabaseContract.WeatherEntry;

/**
 * Utility functions to handle OpenWeatherMap JSON data.
 */
public final class DarkSkyJsonUtils {

    private static final String DK_CURRENTLY = "currently";
    private static final String DK_HOURLY = "hourly";
    private static final String DK_DAILY = "daily";

    private static final String DK_DATA = "data";

    private static final String DK_TIME = "time";
    private static final String DK_SUMMARY = "summary";
    private static final String DK_ICON = "icon";
    private static final String DK_HUMIDITY = "humidity";
    private static final String DK_WIND_SPEED = "windSpeed";
    private static final String DK_PRESSURE = "pressure";
    private static final String DK_PRECIP_TYPE = "precipType";
    private static final String DK_PRECIP_PROBABILITY = "precipProbability";

    private static final String DK_TEMPERATURE = "temperature";
    private static final String DK_APPARENT_TEMPERATURE = "apparentTemperature";

    private static final String DK_TEMP_HIGH = "temperatureHigh";
    private static final String DK_TEMP_LOW = "temperatureLow";

    private static final String DK_SUNRISE_TIME = "sunriseTime";
    private static final String DK_SUNSET_TIME = "sunsetTime";

    public static ContentValues[] getWeatherHourlyContentValuesFromJson(String forecastJsonStr)
            throws JSONException {

        JSONObject forecastJson = new JSONObject(forecastJsonStr);
        JSONObject hourlyDataObject = forecastJson.getJSONObject(DK_HOURLY);
        JSONArray hourlyDataArray = hourlyDataObject.getJSONArray(DK_DATA);

        JSONObject dailyDataObject = forecastJson.getJSONObject(DK_DAILY);
        JSONArray dailyDataArray = dailyDataObject.getJSONArray(DK_DATA);

        ContentValues[] hourlyContentValues = new ContentValues[hourlyDataArray.length()];

        for (int i = 0, j = 0; i < hourlyContentValues.length; i++) {
            long hourlyTime, sunriseTime, sunsetTime;
            String summary, icon, precipType = "";
            int humidity, windSpeed,
                    pressure, precipProb;
            double humidityDouble, windSpeedDouble,
                    pressureDouble, precipProbDouble,
                    tempDouble, apparentTempDouble,
                    highTempDouble, lowTempDouble;

            JSONObject hourlyObject = hourlyDataArray.getJSONObject(i);
            JSONObject dailyObject = dailyDataArray.getJSONObject(j);
            ContentValues hourlyValues = new ContentValues();

            long dailyTime = dailyObject.getLong(DK_TIME) + TimeUnit.DAYS.toMillis(1);
            hourlyTime = hourlyObject.getLong(DK_TIME);
            if (dailyTime == hourlyTime) {
                dailyObject = dailyDataArray.getJSONObject(++j);
            }

            highTempDouble = dailyObject.getDouble(DK_TEMP_HIGH);
            int highTemp = (int) (Math.round(highTempDouble));
            lowTempDouble = dailyObject.getDouble(DK_TEMP_LOW);
            int lowTemp = (int) (Math.round(lowTempDouble));

            sunriseTime = dailyObject.getLong(DK_SUNRISE_TIME) * 1000;
            sunsetTime = dailyObject.getLong(DK_SUNSET_TIME) * 1000;

            icon = hourlyObject.getString(DK_ICON);
            summary = hourlyObject.getString(DK_SUMMARY);
            humidityDouble = hourlyObject.getDouble(DK_HUMIDITY) * 100;
            windSpeedDouble = hourlyObject.getDouble(DK_WIND_SPEED);
            pressureDouble = hourlyObject.getDouble(DK_PRESSURE);
            precipProbDouble = hourlyObject.getDouble(DK_PRECIP_PROBABILITY) * 100;
            precipProb = (int) (Math.round(precipProbDouble));
            tempDouble = hourlyObject.getDouble(DK_TEMPERATURE);
            int temp = (int) (Math.round(tempDouble));
            apparentTempDouble = hourlyObject.getDouble(DK_APPARENT_TEMPERATURE);
            int apparentTemp = (int) (Math.round(apparentTempDouble));
            if (precipProb > 0) {
                precipType = hourlyObject.getString(DK_PRECIP_TYPE);
                hourlyValues.put(WeatherEntry.COLUMN_NAME_PRECIP_TYPE, precipType);
            }

            humidity = (int) (Math.round(humidityDouble));
            windSpeed = (int) (Math.round(windSpeedDouble));
            pressure = (int) (Math.round(pressureDouble));

            hourlyValues.put(WeatherEntry.COLUMN_NAME_TIME, hourlyTime * 1000);
            hourlyValues.put(WeatherEntry.COLUMN_NAME_SUMMARY, summary);
            hourlyValues.put(WeatherEntry.COLUMN_NAME_ICON, icon);
            hourlyValues.put(WeatherEntry.COLUMN_NAME_TEMP, temp);
            hourlyValues.put(WeatherEntry.COLUMN_NAME_APPARENT_TEMP, apparentTemp);
            hourlyValues.put(WeatherEntry.COLUMN_NAME_HIGH_TEMP, highTemp);
            hourlyValues.put(WeatherEntry.COLUMN_NAME_LOW_TEMP, lowTemp);
            hourlyValues.put(WeatherEntry.COLUMN_NAME_PRECIP_PROB, precipProb);
            hourlyValues.put(WeatherEntry.COLUMN_NAME_PRECIP_TYPE, precipType);
            hourlyValues.put(WeatherEntry.COLUMN_NAME_SUNRISE_TIME, sunriseTime);
            hourlyValues.put(WeatherEntry.COLUMN_NAME_SUNSET_TIME, sunsetTime);
            hourlyValues.put(WeatherEntry.COLUMN_NAME_HUMIDITY, humidity);
            hourlyValues.put(WeatherEntry.COLUMN_NAME_WIND_SPEED, windSpeed);
            hourlyValues.put(WeatherEntry.COLUMN_NAME_PRESSURE, pressure);

            hourlyContentValues[i] = hourlyValues;
        }
        return hourlyContentValues;
    }

    public static ContentValues[] getWeatherDailyContentValuesFromJson(String forecastJsonStr)
            throws JSONException {

        JSONObject forecastJson = new JSONObject(forecastJsonStr);
        JSONObject dailyObject = forecastJson.getJSONObject(DK_DAILY);
        JSONArray dailyDataArray = dailyObject.getJSONArray(DK_DATA);

        ContentValues[] weatherContentValues = new ContentValues[7];

        for (int i = 1, j = 0; j < weatherContentValues.length; i++, j++) {
            JSONObject dayForecast = dailyDataArray.getJSONObject(i);

            ContentValues weatherValues = new ContentValues();

            long dailyTime;
            String summary, icon, precipType = "";
            int humidity, windSpeed,
                    pressure, precipProb;
            double humidityDouble, windSpeedDouble,
                    pressureDouble, precipProbDouble,
                    highDouble, lowDouble;

            dailyTime = dayForecast.getLong(DK_TIME) * 1000;
            icon = dayForecast.getString(DK_ICON);
            summary = dayForecast.getString(DK_SUMMARY);
            humidityDouble = dayForecast.getDouble(DK_HUMIDITY) * 100;
            windSpeedDouble = dayForecast.getDouble(DK_WIND_SPEED);
            pressureDouble = dayForecast.getDouble(DK_PRESSURE);
            precipProbDouble = dayForecast.getDouble(DK_PRECIP_PROBABILITY) * 100;
            precipProb = (int) (Math.round(precipProbDouble));
            highDouble = dayForecast.getDouble(DK_TEMP_HIGH);
            int high = (int) (Math.round(highDouble));
            lowDouble = dayForecast.getDouble(DK_TEMP_LOW);
            int low = (int) (Math.round(lowDouble));
            if (precipProb > 0) {
                precipType = dayForecast.getString(DK_PRECIP_TYPE);
                weatherValues.put(WeatherEntry.COLUMN_NAME_PRECIP_TYPE, precipType);
            }

            humidity = (int) (Math.round(humidityDouble));
            windSpeed = (int) (Math.round(windSpeedDouble));
            pressure = (int) (Math.round(pressureDouble));

            weatherValues.put(WeatherEntry.COLUMN_NAME_TIME, dailyTime);
            weatherValues.put(WeatherEntry.COLUMN_NAME_SUMMARY, summary);
            weatherValues.put(WeatherEntry.COLUMN_NAME_ICON, icon);
            weatherValues.put(WeatherEntry.COLUMN_NAME_LOW_TEMP, low);
            weatherValues.put(WeatherEntry.COLUMN_NAME_HIGH_TEMP, high);
            weatherValues.put(WeatherEntry.COLUMN_NAME_PRECIP_PROB, precipProb);
            weatherValues.put(WeatherEntry.COLUMN_NAME_PRECIP_TYPE, precipType);
            weatherValues.put(WeatherEntry.COLUMN_NAME_HUMIDITY, humidity);
            weatherValues.put(WeatherEntry.COLUMN_NAME_WIND_SPEED, windSpeed);
            weatherValues.put(WeatherEntry.COLUMN_NAME_PRESSURE, pressure);

            weatherContentValues[j] = weatherValues;
        }
        return weatherContentValues;
    }
}
