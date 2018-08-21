package io.github.mcvlaga.moonlauncher.home.apps.drawer;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import io.github.mcvlaga.moonlauncher.home.HomeLoaderProvider;
import io.github.mcvlaga.moonlauncher.home.apps.data.AppsRepository;

public class AppsDrawerPresenter implements AppsDrawerContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int APPS_LOADER_ID = 431;

    private final AppsDrawerContract.View mAppsView;

    private final HomeLoaderProvider mAppsLoaderProvider;

    private final LoaderManager mLoaderManager;

    private final AppsRepository mAppsRepository;

    public AppsDrawerPresenter(@NonNull HomeLoaderProvider appsLoaderProvider, @NonNull LoaderManager loaderManager,
                               @NonNull AppsRepository appsRepository, @NonNull AppsDrawerContract.View appsView) {
        mAppsView = appsView;
        mAppsLoaderProvider = appsLoaderProvider;
        mLoaderManager = loaderManager;
        mAppsRepository = appsRepository;
        mAppsView.setPresenter(this);
        mLoaderManager.initLoader(APPS_LOADER_ID, null, this);
    }

    @Override
    public void start() {
        reloadApps();
    }

    @Override
    public void reloadApps() {
        mAppsLoaderProvider.createAppsLoader().execute();
    }

    @Override
    public void addHiddenApp(String packageName) {
        mAppsRepository.addHiddenApp(packageName);
    }

    @Override
    public void addQuickApp(String packageName) {
        mAppsRepository.addQuickApp(packageName);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return mAppsLoaderProvider.createHiddenAppsLoader();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAppsView.showApps(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAppsView.showApps(null);
    }
}
