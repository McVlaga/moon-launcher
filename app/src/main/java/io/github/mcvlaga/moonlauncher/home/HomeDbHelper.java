package io.github.mcvlaga.moonlauncher.home;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import io.github.mcvlaga.moonlauncher.home.apps.data.AppsDatabaseContract;
import io.github.mcvlaga.moonlauncher.home.tasks.data.TasksDatabaseContract;
import io.github.mcvlaga.moonlauncher.home.weather.data.WeatherDatabaseContract;

public class HomeDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 115;

    public static final String DATABASE_NAME = "Home.db";

    private static final String TEXT_TYPE = " TEXT";

    private static final String INTEGER_TYPE = " INTEGER";

    private static final String BOOLEAN_TYPE = " INTEGER";

    private static final String BLOB_TYPE = " BLOB";

    private static final String NOT_NULL = " NOT NULL";

    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_WEATHER_HOURLY_TABLE =
            "CREATE TABLE " + WeatherDatabaseContract.WeatherEntry.TABLE_NAME_WEATHER_HOURLY + " (" +
                    WeatherDatabaseContract.WeatherEntry._ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT, " +
                    WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_TIME + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
                    WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_SUMMARY + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                    WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_ICON + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                    WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_TEMP + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
                    WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_APPARENT_TEMP + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
                    WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_LOW_TEMP + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
                    WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_HIGH_TEMP + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
                    WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_PRECIP_PROB + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
                    WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_PRECIP_TYPE + TEXT_TYPE + COMMA_SEP +
                    WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_SUNRISE_TIME + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
                    WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_SUNSET_TIME + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
                    WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_HUMIDITY + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
                    WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_PRESSURE + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
                    WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_WIND_SPEED + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
                    " UNIQUE (" + WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_TIME + ") ON CONFLICT REPLACE" + "" +
                    ");";

    private static final String SQL_CREATE_WEATHER_DAILY_TABLE =
            "CREATE TABLE " + WeatherDatabaseContract.WeatherEntry.TABLE_NAME_WEATHER_DAILY + " (" +
                    WeatherDatabaseContract.WeatherEntry._ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT, " +
                    WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_TIME + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
                    WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_SUMMARY + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                    WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_ICON + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                    WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_LOW_TEMP + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
                    WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_HIGH_TEMP + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
                    WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_PRECIP_PROB + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
                    WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_PRECIP_TYPE + TEXT_TYPE + COMMA_SEP +
                    WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_HUMIDITY + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
                    WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_PRESSURE + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
                    WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_WIND_SPEED + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
                    " UNIQUE (" + WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_TIME + ") ON CONFLICT REPLACE" +
            ");";

    private static final String SQL_CREATE_TASKS_TABLE =
            "CREATE TABLE " + TasksDatabaseContract.TaskEntry.TABLE_NAME_TASKS + " (" +
                    TasksDatabaseContract.TaskEntry._ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT, " +
                    TasksDatabaseContract.TaskEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                    TasksDatabaseContract.TaskEntry.COLUMN_NAME_TITLE + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                    TasksDatabaseContract.TaskEntry.COLUMN_NAME_DATE + INTEGER_TYPE + COMMA_SEP +
                    TasksDatabaseContract.TaskEntry.COLUMN_NAME_TIME + INTEGER_TYPE + COMMA_SEP +
                    TasksDatabaseContract.TaskEntry.COLUMN_NAME_COLOR + INTEGER_TYPE + COMMA_SEP +
                    TasksDatabaseContract.TaskEntry.COLUMN_NAME_COMPLETED + BOOLEAN_TYPE +
            ");";

    private static final String SQL_CREATE_APPS_TABLE =
            "CREATE TABLE " + AppsDatabaseContract.AppEntry.TABLE_NAME_APPS + " (" +
                    AppsDatabaseContract.AppEntry._ID + INTEGER_TYPE + " PRIMARY KEY, " +
                    AppsDatabaseContract.AppEntry.COLUMN_NAME_PACKAGE + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                    AppsDatabaseContract.AppEntry.COLUMN_NAME_TITLE + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                    AppsDatabaseContract.AppEntry.COLUMN_NAME_ICON + BLOB_TYPE + NOT_NULL + COMMA_SEP +
                    AppsDatabaseContract.AppEntry.COLUMN_NAME_HIDDEN + BOOLEAN_TYPE + NOT_NULL + COMMA_SEP +
                    AppsDatabaseContract.AppEntry.COLUMN_NAME_QUICK + BOOLEAN_TYPE + NOT_NULL + COMMA_SEP +
                    AppsDatabaseContract.AppEntry.COLUMN_NAME_QUICK_ORDER + INTEGER_TYPE + COMMA_SEP +
                    " UNIQUE (" + AppsDatabaseContract.AppEntry.COLUMN_NAME_PACKAGE + ") ON CONFLICT REPLACE" +
                    ");";

    public HomeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_WEATHER_HOURLY_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_WEATHER_DAILY_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TASKS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_APPS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " +
                WeatherDatabaseContract.WeatherEntry.TABLE_NAME_WEATHER_HOURLY);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " +
                WeatherDatabaseContract.WeatherEntry.TABLE_NAME_WEATHER_DAILY);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " +
                TasksDatabaseContract.TaskEntry.TABLE_NAME_TASKS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " +
                AppsDatabaseContract.AppEntry.TABLE_NAME_APPS);
        onCreate(sqLiteDatabase);
    }

    public static String getTableAsString(SQLiteDatabase db) {
        String tableString = String.format("Table %s:\n", AppsDatabaseContract.AppEntry.TABLE_NAME_APPS);

        Cursor allRows = db.rawQuery("SELECT _id, package, title, hidden, quick FROM " + AppsDatabaseContract.AppEntry.TABLE_NAME_APPS, null);
        if (allRows.moveToFirst()) {
            String[] columnNames = allRows.getColumnNames();
            do {
                for (String name : columnNames) {
                    tableString += String.format("%s: %s\n", name,
                            allRows.getString(allRows.getColumnIndex(name)));
                }
                tableString += "\n";

            } while (allRows.moveToNext());
        }

        return tableString;
    }
}
