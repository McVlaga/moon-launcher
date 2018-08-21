package io.github.mcvlaga.moonlauncher.home.tasks.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.github.mcvlaga.moonlauncher.home.HomeDbHelper;

public class TasksProvider extends ContentProvider {

    public static final int CODE_TASKS = 100;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private HomeDbHelper mOpenHelper;

    public static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = TasksDatabaseContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, TasksDatabaseContract.PATH_TASKS, CODE_TASKS);
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
            case CODE_TASKS:
                db.beginTransaction();
                int rowsInserted = 0;
                try {
                    for (int i = 0; i < values.length; i++) {
                        ContentValues value = values[i];
                        long _id = db.insert(
                                TasksDatabaseContract.TaskEntry.TABLE_NAME_TASKS,
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

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
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
            case CODE_TASKS: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        TasksDatabaseContract.TaskEntry.TABLE_NAME_TASKS,
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

        Uri returnUri;

        switch (sUriMatcher.match(uri)) {
            case CODE_TASKS:
                Cursor exists = db.query(
                        TasksDatabaseContract.TaskEntry.TABLE_NAME_TASKS,
                        new String[]{TasksDatabaseContract.TaskEntry.COLUMN_NAME_ENTRY_ID},
                        TasksDatabaseContract.TaskEntry.COLUMN_NAME_ENTRY_ID + " = ?",
                        new String[]{contentValue.getAsString(TasksDatabaseContract.TaskEntry.COLUMN_NAME_ENTRY_ID)},
                        null,
                        null,
                        null
                );
                if (exists.moveToLast()) {
                    long _id = db.update(
                            TasksDatabaseContract.TaskEntry.TABLE_NAME_TASKS, contentValue,
                            TasksDatabaseContract.TaskEntry.COLUMN_NAME_ENTRY_ID + " = ?",
                            new String[]{contentValue.getAsString(TasksDatabaseContract.TaskEntry.COLUMN_NAME_ENTRY_ID)}
                    );
                    if (_id > 0) {
                        returnUri = TasksDatabaseContract.TaskEntry.buildTasksUriWith(_id);
                    } else {
                        throw new android.database.SQLException("Failed to insert row into " + uri);
                    }
                } else {
                    long _id = db.insert(
                            TasksDatabaseContract.TaskEntry.TABLE_NAME_TASKS,
                            null,
                            contentValue);
                    if (_id > 0) {
                        returnUri = TasksDatabaseContract.TaskEntry.buildTasksUriWith(_id);
                    } else {
                        throw new android.database.SQLException("Failed to insert row into " + uri);
                    }
                }
                exists.close();
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int numRowsDeleted;

        if (null == selection) selection = "1";
        switch (sUriMatcher.match(uri)) {

            case CODE_TASKS:
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        TasksDatabaseContract.TaskEntry.TABLE_NAME_TASKS,
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
    public int update(@NonNull Uri uri, @Nullable ContentValues values,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case CODE_TASKS:
                rowsUpdated = db.update(TasksDatabaseContract.TaskEntry.TABLE_NAME_TASKS, values, selection,
                        selectionArgs
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
