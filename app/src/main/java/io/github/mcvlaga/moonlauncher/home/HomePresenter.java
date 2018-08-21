package io.github.mcvlaga.moonlauncher.home;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import io.github.mcvlaga.moonlauncher.home.tasks.data.Task;

public class HomePresenter implements HomeContract.Presenter {

    private static final int WEATHER_LOADER_ID = 0;
    private static final int TASKS_LOADER_ID = 1;
    private static final int QUICK_APPS_LOADER_ID = 2;

    private final HomeContract.View mHomeView;

    private final HomeLoaderProvider mHomeLoaderProvider;

    private final LoaderManager mLoaderManager;

    private final HomeRepository mHomeRepository;

    public HomePresenter(@NonNull HomeLoaderProvider homeLoaderProvider, @NonNull LoaderManager loaderManager,
                         @NonNull HomeRepository homeRepository, @NonNull HomeContract.View homeView) {
        mHomeView = homeView;
        mHomeLoaderProvider = homeLoaderProvider;
        mLoaderManager = loaderManager;
        mHomeRepository = homeRepository;
        mHomeView.setPresenter(this);
    }

    @Override
    public void start() {
        mHomeView.showSearch();

        if (HomeViewType.WEATHER.isShown()) {
            if (mLoaderManager.getLoader(WEATHER_LOADER_ID) == null) {
                mLoaderManager.initLoader(WEATHER_LOADER_ID, null, weatherLoaderListener);
            } else {
                mLoaderManager.restartLoader(WEATHER_LOADER_ID, null, weatherLoaderListener);
            }
        }

        if (HomeViewType.TASKS.isShown()) {
            if (mLoaderManager.getLoader(TASKS_LOADER_ID) == null) {
                mLoaderManager.initLoader(TASKS_LOADER_ID, null, tasksLoaderListener);
            } else {
                mLoaderManager.restartLoader(TASKS_LOADER_ID, null, tasksLoaderListener);
            }
        }

        if (HomeViewType.APPS.isShown()) {
            if (mLoaderManager.getLoader(QUICK_APPS_LOADER_ID) == null) {
                mLoaderManager.initLoader(QUICK_APPS_LOADER_ID, null, quickAppsLoaderListener);
            } else {
                mLoaderManager.restartLoader(QUICK_APPS_LOADER_ID, null, quickAppsLoaderListener);
            }
        }
    }

    @Override
    public void saveTask(Task task) {
        mHomeRepository.saveTask(task);
    }

    @Override
    public void removeQuickApp(String packageName) {
        mHomeRepository.removeQuickApp(packageName);
    }

    @Override
    public void completeTask(Task task) {
        mHomeRepository.completeTask(task);
        mHomeView.showUndoSnackBar(task);
    }

    private LoaderManager.LoaderCallbacks<Cursor> weatherLoaderListener
            = new LoaderManager.LoaderCallbacks<Cursor>() {

        @NonNull
        @Override
        public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
            return mHomeLoaderProvider.createMainWeatherLoader();
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
            mHomeView.showWeather(cursor);
        }

        @Override
        public void onLoaderReset(@NonNull Loader<Cursor> loader) {
            mHomeView.showWeather(null);
        }
    };

    private LoaderManager.LoaderCallbacks<Cursor> tasksLoaderListener
            = new LoaderManager.LoaderCallbacks<Cursor>() {

        @NonNull
        @Override
        public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
            return mHomeLoaderProvider.createTasksLoaderForHome();
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
            mHomeView.showTasks(cursor);
        }

        @Override
        public void onLoaderReset(@NonNull Loader<Cursor> loader) {
            mHomeView.showTasks(null);
        }
    };

    private LoaderManager.LoaderCallbacks<Cursor> quickAppsLoaderListener
            = new LoaderManager.LoaderCallbacks<Cursor>() {

        @NonNull
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return mHomeLoaderProvider.createQuickAppsLoader();
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
            mHomeView.showQuickApps(data);
        }

        @Override
        public void onLoaderReset(@NonNull Loader<Cursor> loader) {
            mHomeView.showQuickApps(null);
        }
    };
}
