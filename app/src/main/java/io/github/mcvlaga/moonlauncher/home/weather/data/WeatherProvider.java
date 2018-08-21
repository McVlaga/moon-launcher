package io.github.mcvlaga.moonlauncher.home.weather.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MergeCursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.github.mcvlaga.moonlauncher.home.HomeDbHelper;

public class WeatherProvider extends ContentProvider {

    public static final int CODE_WEATHER_HOURLY = 100;
    public static final int CODE_WEATHER_HOURLY_WITH_DATE = 101;
    public static final int CODE_WEATHER_DAILY = 102;
    public static final int CODE_WEATHER_DAILY_WITH_DATE = 103;
    public static final int CODE_WEATHER = 104;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private HomeDbHelper mOpenHelper;

    public static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = WeatherDatabaseContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, WeatherDatabaseContract.PATH_WEATHER + "/"
                + WeatherDatabaseContract.PATH_HOURLY, CODE_WEATHER_HOURLY);
        matcher.addURI(authority, WeatherDatabaseContract.PATH_WEATHER + "/"
                + WeatherDatabaseContract.PATH_HOURLY + "/#", CODE_WEATHER_HOURLY_WITH_DATE);
        matcher.addURI(authority, WeatherDatabaseContract.PATH_WEATHER + "/"
                + WeatherDatabaseContract.PATH_DAILY, CODE_WEATHER_DAILY);
        matcher.addURI(authority, WeatherDatabaseContract.PATH_WEATHER + "/"
                + WeatherDatabaseContract.PATH_DAILY + "/#", CODE_WEATHER_DAILY_WITH_DATE);
        matcher.addURI(authority, WeatherDatabaseContract.PATH_WEATHER, CODE_WEATHER);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new HomeDbHelper(getContext());
        return true;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int rowsInserted = 0;
        switch (sUriMatcher.match(uri)) {
            case CODE_WEATHER_HOURLY:
                db.beginTransaction();
                try {
                    for (int i = 0; i < values.length; i++) {
                        ContentValues value = values[i];
                        long _id = db.insert(
                                WeatherDatabaseContract.WeatherEntry.TABLE_NAME_WEATHER_HOURLY,
                                null,
                                value);

                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case CODE_WEATHER_DAILY:
                db.beginTransaction();
                rowsInserted = 0;
                try {
                    for (int i = 0; i < values.length; i++) {
                        ContentValues value = values[i];
                        long _id = db.insert(
                                WeatherDatabaseContract.WeatherEntry.TABLE_NAME_WEATHER_DAILY,
                                null,
                                value);

                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            default:
                rowsInserted = super.bulkInsert(uri, values);
        }

        if (rowsInserted > 0) {
            getContext().getContentResolver().notifyChange(WeatherDatabaseContract.WeatherEntry.CONTENT_URI_WEATHER, null);
        }
        return rowsInserted;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case CODE_WEATHER_HOURLY: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        WeatherDatabaseContract.WeatherEntry.TABLE_NAME_WEATHER_HOURLY,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case CODE_WEATHER_HOURLY_WITH_DATE: {
                String normalizedUtcDateString = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{normalizedUtcDateString};

                cursor = mOpenHelper.getReadableDatabase().query(
                        /* Table we are going to query */
                        WeatherDatabaseContract.WeatherEntry.TABLE_NAME_WEATHER_HOURLY,
                        projection,
                        WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_TIME + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case CODE_WEATHER_DAILY: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        WeatherDatabaseContract.WeatherEntry.TABLE_NAME_WEATHER_DAILY,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case CODE_WEATHER_DAILY_WITH_DATE: {
                String normalizedUtcDateString = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{normalizedUtcDateString};

                cursor = mOpenHelper.getReadableDatabase().query(
                        /* Table we are going to query */
                        WeatherDatabaseContract.WeatherEntry.TABLE_NAME_WEATHER_DAILY,
                        projection,
                        WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_TIME + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case CODE_WEATHER: {
                String hourlySelection = WeatherDatabaseContract.WeatherEntry.getSqlSelectForNowOnwards();
                Cursor hourlyCursor = mOpenHelper.getReadableDatabase().query(
                        WeatherDatabaseContract.WeatherEntry.TABLE_NAME_WEATHER_HOURLY,
                        null,
                        hourlySelection,
                        null,
                        null,
                        null,
                        null);

                String dailyOrder = WeatherDatabaseContract.WeatherEntry.COLUMN_NAME_TIME + " ASC";
                String dailySelection = WeatherDatabaseContract.WeatherEntry.getSqlSelectForTodayOnwards();
                Cursor dailyCursor = mOpenHelper.getReadableDatabase().query(
                        WeatherDatabaseContract.WeatherEntry.TABLE_NAME_WEATHER_DAILY,
                        null,
                        dailySelection,
                        null,
                        null,
                        null,
                        dailyOrder);


                hourlyCursor.setNotificationUri(getContext().getContentResolver(), uri);
                dailyCursor.setNotificationUri(getContext().getContentResolver(), uri);
                cursor = new MergeCursor(new Cursor[]{hourlyCursor, dailyCursor});
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int numRowsDeleted;

        if (null == selection) selection = "1";
        switch (sUriMatcher.match(uri)) {
            case CODE_WEATHER_HOURLY:
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        WeatherDatabaseContract.WeatherEntry.TABLE_NAME_WEATHER_HOURLY,
                        selection,
                        selectionArgs);
                break;
            case CODE_WEATHER_DAILY:
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        WeatherDatabaseContract.WeatherEntry.TABLE_NAME_WEATHER_DAILY,
                        selection,
                        selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
