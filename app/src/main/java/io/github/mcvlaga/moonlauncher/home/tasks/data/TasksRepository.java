package io.github.mcvlaga.moonlauncher.home.tasks.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;

import io.github.mcvlaga.moonlauncher.home.tasks.TasksFilterType;

public class TasksRepository {

    public static final String PREF_TASKS_LIST_FILTER = "tasks_list_filter";
    public static final String PREF_TASKS_LIST_DEFAULT_FILTER = "ALL_TASKS";

    private ContentResolver mContentResolver;
    private SharedPreferences mSharedPreferences;

    public TasksRepository(ContentResolver contentResolver, SharedPreferences sharedPreferences) {
        mContentResolver = contentResolver;
        mSharedPreferences = sharedPreferences;
    }

    public void completeTask(Task task) {
        ContentValues values = new ContentValues();
        values.put(TasksDatabaseContract.TaskEntry.COLUMN_NAME_COMPLETED, 1);

        String selection = TasksDatabaseContract.TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = {task.getId()};

        mContentResolver.update(TasksDatabaseContract.TaskEntry.CONTENT_URI, values, selection, selectionArgs);
    }

    public void activateTask(Task task) {
        ContentValues values = new ContentValues();
        values.put(TasksDatabaseContract.TaskEntry.COLUMN_NAME_COMPLETED, 0);

        String selection = TasksDatabaseContract.TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = {task.getId()};

        mContentResolver.update(TasksDatabaseContract.TaskEntry.CONTENT_URI, values, selection, selectionArgs);
    }

    public void saveTask(Task task) {
        ContentValues value = new ContentValues();
        value.put(TasksDatabaseContract.TaskEntry.COLUMN_NAME_ENTRY_ID, task.getId());
        value.put(TasksDatabaseContract.TaskEntry.COLUMN_NAME_TITLE, task.getTitle());
        value.put(TasksDatabaseContract.TaskEntry.COLUMN_NAME_DATE, task.getDate());
        value.put(TasksDatabaseContract.TaskEntry.COLUMN_NAME_TIME, task.getTime());
        value.put(TasksDatabaseContract.TaskEntry.COLUMN_NAME_COLOR, task.getColor());
        value.put(TasksDatabaseContract.TaskEntry.COLUMN_NAME_COMPLETED, 0);

        mContentResolver.insert(TasksDatabaseContract.TaskEntry.CONTENT_URI, value);
    }

    public void removeCompletedTasks() {
        String selection = TasksDatabaseContract.TaskEntry.getSqlSelectForCompletedTasks();
        mContentResolver.delete(
                TasksDatabaseContract.TaskEntry.CONTENT_URI,
                selection,
                null
        );
    }

    public void setTasksListFilter(TasksFilterType filterType) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        editor.putString(PREF_TASKS_LIST_FILTER, filterType.toString());
        editor.apply();
    }

    public TasksFilterType getTasksListFilter() {
        return TasksFilterType.valueOf(mSharedPreferences.getString(PREF_TASKS_LIST_FILTER,
                PREF_TASKS_LIST_DEFAULT_FILTER));
    }

    public void setMaxTasksNumber(int maxTasks, String key) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(key, maxTasks);
        editor.apply();
    }
}
