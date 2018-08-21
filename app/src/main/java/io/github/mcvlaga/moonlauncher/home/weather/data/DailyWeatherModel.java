package io.github.mcvlaga.moonlauncher.home.weather.data;

import android.database.Cursor;

import io.github.mcvlaga.moonlauncher.home.weather.utils.WeatherUtils;

public class DailyWeatherModel {

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

    public DailyWeatherModel(long time, String summary, String icon, int high, int low,
                             int precipProb, String precipType, int pressure, int humidity,
                             int windSpeed) {
        mTime = time;
        mSummary = summary;
        mIcon = icon;
        mPrecipProbability = precipProb;
        mPrecipType = precipType;
        mPressure = pressure;
        mHumidity = humidity;
        mWindSpeed = windSpeed;
        mHighTemperature = high;
        mLowTemperature = low;
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

    public int getIconId() {
        return WeatherUtils.getSmallArtResourceIdForWeatherCondition(mIcon);
    }

    public static DailyWeatherModel from(Cursor cursor) {
        long time = cursor.getLong(cursor.getColumnIndexOrThrow(
                WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_TIME));
        String summary = cursor.getString(cursor.getColumnIndexOrThrow(
                WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_SUMMARY));
        String icon = cursor.getString(cursor.getColumnIndexOrThrow(
                WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_ICON));
        int highTemperature = cursor.getInt(cursor.getColumnIndexOrThrow(
                WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_HIGH_TEMP));
        int lowTemperature = cursor.getInt(cursor.getColumnIndexOrThrow(
                WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_LOW_TEMP));
        int precipProb = cursor.getInt(cursor.getColumnIndexOrThrow(
                WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_PRECIP_PROB));
        String precipType = cursor.getString(cursor.getColumnIndexOrThrow(
                WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_PRECIP_TYPE));
        int humidity = cursor.getInt(cursor.getColumnIndexOrThrow(
                WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_HUMIDITY));
        int pressure = cursor.getInt(cursor.getColumnIndexOrThrow(
                WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_PRESSURE));
        int windSpeed = cursor.getInt(cursor.getColumnIndexOrThrow(
                WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_WIND_SPEED));
        return new DailyWeatherModel(time, summary, icon, highTemperature,
                lowTemperature, precipProb, precipType, pressure,
                humidity, windSpeed);
    }
}
