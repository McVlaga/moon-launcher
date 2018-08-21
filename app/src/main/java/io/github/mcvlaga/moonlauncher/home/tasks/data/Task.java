package io.github.mcvlaga.moonlauncher.home.tasks.data;

import android.database.Cursor;

import java.util.UUID;

public class Task {

    private final String mId;

    private String mTitle;
    private long mDate;
    private long mTime;
    private int mColor;
    private boolean mCompleted;

    public Task(String title, long date, long time, int color, boolean completed) {
        mId = UUID.randomUUID().toString();
        mTitle = title;
        mDate = date;
        mTime = time;
        mColor = color;
        mCompleted = completed;
    }

    public Task(String id, String title, long date, long time, int color, boolean completed) {
        mId = id;
        mTitle = title;
        mDate = date;
        mTime = time;
        mColor = color;
        mCompleted = completed;
    }

    public Task(String id, String title, int color, boolean completed) {
        mId = id;
        mTitle = title;
        mDate = 0;
        mTime = 0;
        mColor = color;
        mCompleted = completed;
    }

    public Task(String id, String title, long date, int color, boolean completed) {
        mId = id;
        mTitle = title;
        mDate = date;
        mTime = 0;
        mColor = color;
        mCompleted = completed;
    }

    public String getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public long getDate() {
        return mDate;
    }

    public long getTime() {
        return mTime;
    }

    public int getColor() {
        return mColor;
    }

    public boolean isCompleted() {
        return mCompleted;
    }

    public static Task from(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndexOrThrow(
                TasksDatabaseContract.TaskEntry.COLUMN_NAME_ENTRY_ID));
        long dateInMillis = cursor.getLong(cursor.getColumnIndexOrThrow(
                TasksDatabaseContract.TaskEntry.COLUMN_NAME_DATE));
        long timeInMillis = cursor.getLong(cursor.getColumnIndexOrThrow(
                TasksDatabaseContract.TaskEntry.COLUMN_NAME_TIME));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(
                TasksDatabaseContract.TaskEntry.COLUMN_NAME_TITLE));
        int color = cursor.getInt(cursor.getColumnIndexOrThrow(
                TasksDatabaseContract.TaskEntry.COLUMN_NAME_COLOR));
        int completedInt = cursor.getInt(cursor.getColumnIndexOrThrow(
                TasksDatabaseContract.TaskEntry.COLUMN_NAME_COMPLETED));
        boolean completed = completedInt == 1;
        return new Task(id, title, dateInMillis, timeInMillis, color, completed);
    }
}
