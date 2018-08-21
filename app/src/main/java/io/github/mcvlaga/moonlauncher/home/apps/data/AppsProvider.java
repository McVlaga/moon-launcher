package io.github.mcvlaga.moonlauncher.home.apps.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.github.mcvlaga.moonlauncher.home.HomeDbHelper;

public class AppsProvider extends ContentProvider {

    public static final int CODE_APPS = 100;
    public static final int CODE_APPS_HIDDEN = 101;
    public static final int CODE_APPS_QUICK = 102;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private HomeDbHelper mOpenHelper;

    public static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = AppsDatabaseContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, AppsDatabaseContract.PATH_APPS, CODE_APPS);
        matcher.addURI(authority, AppsDatabaseContract.PATH_APPS + "/"
                + AppsDatabaseContract.PATH_APPS_HIDDEN, CODE_APPS_HIDDEN);
        matcher.addURI(authority, AppsDatabaseContract.PATH_APPS + "/"
                + AppsDatabaseContract.PATH_APPS_QUICK, CODE_APPS_QUICK);
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

        switch (sUriMatcher.match(uri)) {
            case CODE_APPS:
                db.beginTransaction();
                int rowsInserted = 0;
                try {
                    for (int i = 0; i < values.length; i++) {
                        ContentValues value = values[i];

                        long _id = db.insertWithOnConflict(
                                AppsDatabaseContract.AppEntry.TABLE_NAME_APPS,
                                null,
                                value,
                                SQLiteDatabase.CONFLICT_IGNORE);

                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                    getContext().getContentResolver().notifyChange(
                            AppsDatabaseContract.AppEntry.CONTENT_URI_APPS_HIDDEN, null);
                    getContext().getContentResolver().notifyChange(
                            AppsDatabaseContract.AppEntry.CONTENT_URI_APPS_QUICK, null);
                }

                return rowsInserted;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case CODE_APPS: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        AppsDatabaseContract.AppEntry.TABLE_NAME_APPS,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValue) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {
            case CODE_APPS:
                int u = db.update(
                        AppsDatabaseContract.AppEntry.TABLE_NAME_APPS, contentValue,
                        AppsDatabaseContract.AppEntry.COLUMN_NAME_PACKAGE + " = ?",
                        new String[]{contentValue.getAsString(AppsDatabaseContract.AppEntry.COLUMN_NAME_PACKAGE)}
                );
                long _id = -1;
                if (u == 0) {
                    _id = db.insertWithOnConflict(
                            AppsDatabaseContract.AppEntry.TABLE_NAME_APPS,
                            null,
                            contentValue,
                            SQLiteDatabase.CONFLICT_REPLACE);
                }

                if (_id != -1 || u > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                    getContext().getContentResolver().notifyChange(
                            AppsDatabaseContract.AppEntry.CONTENT_URI_APPS_HIDDEN, null);
                    getContext().getContentResolver().notifyChange(
                            AppsDatabaseContract.AppEntry.CONTENT_URI_APPS_QUICK, null);
                }
                /*Cursor exists = db.query(
                        AppsDatabaseContract.AppEntry.TABLE_NAME_APPS,
                        new String[]{AppsDatabaseContract.AppEntry.COLUMN_NAME_PACKAGE},
                        AppsDatabaseContract.AppEntry.COLUMN_NAME_PACKAGE + " = ?",
                        new String[]{contentValue.getAsString(AppsDatabaseContract.AppEntry.COLUMN_NAME_PACKAGE)},
                        null,
                        null,
                        null
                );
                if (exists.moveToLast()) {
                    long _id = db.update(
                            AppsDatabaseContract.AppEntry.TABLE_NAME_APPS, contentValue,
                            AppsDatabaseContract.AppEntry.COLUMN_NAME_PACKAGE + " = ?",
                            new String[]{contentValue.getAsString(AppsDatabaseContract.AppEntry.COLUMN_NAME_PACKAGE)}
                    );
                    if (_id > 0) {
                        returnUri = AppsDatabaseContract.AppEntry.buildAppsUriWith(_id);
                    } else {
                        throw new android.database.SQLException("Failed to insert row into " + uri);
                    }
                } else {
                    long _id = db.insert(
                            AppsDatabaseContract.AppEntry.TABLE_NAME_APPS,
                            null,
                            contentValue);
                    if (_id > 0) {
                        returnUri = AppsDatabaseContract.AppEntry.buildAppsUriWith(_id);
                    } else {
                        throw new android.database.SQLException("Failed to insert row into " + uri);
                    }
                }
                exists.close();*/
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return uri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int numRowsDeleted;

        if (null == selection) selection = "1";
        switch (sUriMatcher.match(uri)) {

            case CODE_APPS:
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        AppsDatabaseContract.AppEntry.TABLE_NAME_APPS,
                        selection,
                        selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            getContext().getContentResolver().notifyChange(
                    AppsDatabaseContract.AppEntry.CONTENT_URI_APPS_HIDDEN, null);
            getContext().getContentResolver().notifyChange(
                    AppsDatabaseContract.AppEntry.CONTENT_URI_APPS_QUICK, null);
        }

        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case CODE_APPS:
                rowsUpdated = db.update(
                        AppsDatabaseContract.AppEntry.TABLE_NAME_APPS,
                        values,
                        selection,
                        selectionArgs
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            getContext().getContentResolver().notifyChange(
                    AppsDatabaseContract.AppEntry.CONTENT_URI_APPS_HIDDEN, null);
            getContext().getContentResolver().notifyChange(
                    AppsDatabaseContract.AppEntry.CONTENT_URI_APPS_QUICK, null);
        }
        return rowsUpdated;
    }
}

