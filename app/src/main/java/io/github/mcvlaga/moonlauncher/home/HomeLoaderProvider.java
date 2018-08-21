package io.github.mcvlaga.moonlauncher.home;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import io.github.mcvlaga.moonlauncher.home.apps.data.AppsDatabaseContract;
import io.github.mcvlaga.moonlauncher.home.apps.data.AppsLoader;
import io.github.mcvlaga.moonlauncher.home.tasks.TasksFilterType;
import io.github.mcvlaga.moonlauncher.home.tasks.data.TasksDatabaseContract;
import io.github.mcvlaga.moonlauncher.home.weather.data.WeatherDatabaseContract;

public class HomeLoaderProvider {

    @NonNull
    private final Context mContext;

    public HomeLoaderProvider(@NonNull Context context) {
        mContext = context;
    }

    public Loader<Cursor> createMainWeatherLoader() {
        Uri forecastQueryUri = WeatherDatabaseContract.WeatherEntry.CONTENT_URI_WEATHER;
        return new CursorLoader(mContext,
                forecastQueryUri,
                null,
                null,
                null,
                null);
    }

    public Loader<Cursor> createTasksLoaderForHome() {
        Uri tasksQueryUri = TasksDatabaseContract.TaskEntry.CONTENT_URI;
        String sortOrder = "(" + TasksDatabaseContract.TaskEntry.COLUMN_NAME_DATE + " + "
                + TasksDatabaseContract.TaskEntry.COLUMN_NAME_TIME + ")" + " ASC";
        String selection = TasksDatabaseContract.TaskEntry.getSqlSelectForHome();

        return new CursorLoader(mContext,
                tasksQueryUri,
                null,
                selection,
                null,
                sortOrder);
    }

    public Loader<Cursor> createTasksLoader(TasksFilterType filterType) {
        Uri tasksQueryUri = TasksDatabaseContract.TaskEntry.CONTENT_URI;
        String sortOrder;
        String selection = null;

        switch (filterType) {
            case ALL_TASKS:
                sortOrder = TasksDatabaseContract.TaskEntry.COLUMN_NAME_COMPLETED + " ASC, "
                        + TasksDatabaseContract.TaskEntry.COLUMN_NAME_DATE + " ASC, "
                        + TasksDatabaseContract.TaskEntry.COLUMN_NAME_TIME + " ASC";
                break;
            case COMPLETED_TASKS:
                sortOrder = TasksDatabaseContract.TaskEntry.COLUMN_NAME_DATE + " ASC, "
                        + TasksDatabaseContract.TaskEntry.COLUMN_NAME_TIME + " ASC";
                selection = TasksDatabaseContract.TaskEntry.getSqlSelectForCompletedTasks();
                break;
            default:
                sortOrder = TasksDatabaseContract.TaskEntry.COLUMN_NAME_DATE + " ASC, "
                        + TasksDatabaseContract.TaskEntry.COLUMN_NAME_TIME + " ASC";
                selection = TasksDatabaseContract.TaskEntry.getSqlSelectForActiveTasks();
                break;
        }

        return new CursorLoader(mContext,
                tasksQueryUri,
                null,
                selection,
                null,
                sortOrder);
    }

    public AsyncTask<Void, Void, Void> createAppsLoader() {
        return new AppsLoader(mContext);
    }

    public Loader<Cursor> createSettingsAppsLoader() {
        Uri appsQueryUri = AppsDatabaseContract.AppEntry.CONTENT_URI_APPS;
        String sortOrder = AppsDatabaseContract.AppEntry.getSqlOrderForApps()       ;

        return new CursorLoader(mContext,
                appsQueryUri,
                null,
                null,
                null,
                sortOrder);
    }

    public Loader<Cursor> createQuickAppsLoader() {
        Uri appsQueryUri = AppsDatabaseContract.AppEntry.CONTENT_URI_APPS;
        String selection = AppsDatabaseContract.AppEntry.getSqlSelectForQuickApps();
        String sortOrder = AppsDatabaseContract.AppEntry.getSqlOrderForQuickApps();

        return new CursorLoader(mContext,
                appsQueryUri,
                null,
                selection,
                null,
                sortOrder);
    }

    public Loader<Cursor> createHiddenAppsLoader() {
        Uri appsQueryUri = AppsDatabaseContract.AppEntry.CONTENT_URI_APPS;
        String selection = AppsDatabaseContract.AppEntry.getSqlSelectForHiddenApps();
        String sortOrder = AppsDatabaseContract.AppEntry.getSqlOrderForApps();

        return new CursorLoader(mContext,
                appsQueryUri,
                null,
                selection,
                null,
                sortOrder);
    }
}
