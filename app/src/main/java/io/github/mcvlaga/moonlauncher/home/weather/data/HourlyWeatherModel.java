package io.github.mcvlaga.moonlauncher.home.weather.data;

import android.database.Cursor;

import io.github.mcvlaga.moonlauncher.home.weather.utils.WeatherUtils;

public class HourlyWeatherModel {

    private long mTime;
    private String mSummary;
    private String mIcon;
    private int mPrecipProbability;
    private String mPrecipType;
    private int mPressure;
    private int mHumidity;
    private int mWindSpeed;
    private int mHighTemperature;
    private int mLowTemperature;
    private long mSunriseTime;

    private long mSunsetTime;

    private int mTemperature;
    private int mApparentTemperature;

    public HourlyWeatherModel(long time, String summary, String icon, int temp, int apparentTemp,
                              int high, int low, int precipProb, String precipType,
                              long sunriseTime, long sunsetTime, int pressure, int humidity,
                              int windSpeed) {
        mTime = time;
        mSummary = summary;
        mIcon = icon;
        mPrecipProbability = precipProb;
        mPrecipType = precipType;
        mPressure = pressure;
        mHumidity = humidity;
        mWindSpeed = windSpeed;
        mTemperature = temp;
        mApparentTemperature = apparentTemp;
        mHighTemperature = high;
        mLowTemperature = low;
        mSunriseTime = sunriseTime;
        mSunsetTime = sunsetTime;
    }

    public int getTemperature() {
        return mTemperature;
    }

    public int getApparentTemperature() {
        return mApparentTemperature;
    }

    public static HourlyWeatherModel from(Cursor cursor) {
        long time = cursor.getLong(cursor.getColumnIndexOrThrow(
                WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_TIME));
        String summary = cursor.getString(cursor.getColumnIndexOrThrow(
                WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_SUMMARY));
        String icon = cursor.getString(cursor.getColumnIndexOrThrow(
                WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_ICON));
        int temp = cursor.getInt(cursor.getColumnIndexOrThrow(
                WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_TEMP));
        int apparentTemp = cursor.getInt(cursor.getColumnIndexOrThrow(
                WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_APPARENT_TEMP));
        int highTemperature = cursor.getInt(cursor.getColumnIndexOrThrow(
                WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_HIGH_TEMP));
        int lowTemperature = cursor.getInt(cursor.getColumnIndexOrThrow(
                WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_LOW_TEMP));
        int precipProb = cursor.getInt(cursor.getColumnIndexOrThrow(
                WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_PRECIP_PROB));
        String precipType = cursor.getString(cursor.getColumnIndexOrThrow(
                WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_PRECIP_TYPE));
        long sunriseTime = cursor.getLong(cursor.getColumnIndexOrThrow(
                WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_SUNRISE_TIME));
        long sunsetTime = cursor.getLong(cursor.getColumnIndexOrThrow(
                WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_SUNSET_TIME));
        int humidity = cursor.getInt(cursor.getColumnIndexOrThrow(
                WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_HUMIDITY));
        int pressure = cursor.getInt(cursor.getColumnIndexOrThrow(
                WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_PRESSURE));
        int windSpeed = cursor.getInt(cursor.getColumnIndexOrThrow(
                WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_WIND_SPEED));
        return new HourlyWeatherModel(time, summary, icon, temp, apparentTemp, highTemperature,
                lowTemperature, precipProb, precipType, sunriseTime, sunsetTime, pressure,
                humidity, windSpeed);
    }

    public int getIconId() {
        return WeatherUtils.getLargeArtResourceIdForWeatherCondition(mIcon);
    }

    public long getTime() {
        return mTime;
    }

    public String getSummary() {
        return mSummary;
    }

    public String getIcon() {
        return mIcon;
    }

    public int getPrecipProbability() {
        return mPrecipProbability;
    }

    public String getPrecipType() {
        return mPrecipType;
    }

    public int getPressure() {
        return mPressure;
    }

    public int getHumidity() {
        return mHumidity;
    }

    public int getWindSpeed() {
        return mWindSpeed;
    }

    public int getHighTemperature() {
        return mHighTemperature;
    }

    public int getLowTemperature() {
        return mLowTemperature;
    }

    public long getSunriseTime() {
        return mSunriseTime;
    }

    public long getSunsetTime() {
        return mSunsetTime;
    }
}
