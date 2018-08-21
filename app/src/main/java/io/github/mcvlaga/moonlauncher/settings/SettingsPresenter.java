package io.github.mcvlaga.moonlauncher.settings;

import android.database.Cursor;
import android.support.v4.content.Loader;

import io.github.mcvlaga.moonlauncher.home.HomeLoaderProvider;

public class SettingsPresenter implements SettingsContract.Presenter {

    private SettingsRepository mSettingsRepository;
    private SettingsContract.View mSettingsView;
    private HomeLoaderProvider mSettingsLoaderProvider;

    public SettingsPresenter(HomeLoaderProvider loaderProvider, SettingsContract.View view,
                             SettingsRepository repository) {
        mSettingsLoaderProvider = loaderProvider;
        mSettingsView = view;
        mSettingsRepository = repository;
        mSettingsView.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void addHiddenApp(String packageName) {
        mSettingsRepository.addHiddenApp(packageName);
    }

    @Override
    public void removeHiddenApp(String packageName) {
        mSettingsRepository.removeHiddenApp(packageName);
    }

    @Override
    public void setMaxTasksNumber(int maxTasks, String key) {
        mSettingsRepository.setMaxTasksNumber(maxTasks, key);
    }

    @Override
    public void setNumberOfForecasts(int numForecasts, String key) {
        mSettingsRepository.setNumberOfForecasts(numForecasts, key);
    }

    @Override
    public void setLocationCity(String key, String city) {
        mSettingsRepository.setLocationCity(key, city);
    }

    @Override
    public void setLocationDetails(double latitude, double longitude) {
        mSettingsRepository.setLocationDetails(latitude, longitude);
    }

    @Override
    public int getNumberOfHiddenApps() {
        return mSettingsRepository.getNumberOfHiddenApps();
    }

    @Override
    public Loader<Cursor> createAppsLoader() {
        return mSettingsLoaderProvider.createSettingsAppsLoader();
    }
}
