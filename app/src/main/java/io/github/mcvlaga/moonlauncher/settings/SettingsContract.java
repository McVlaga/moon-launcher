package io.github.mcvlaga.moonlauncher.settings;

import android.database.Cursor;
import android.support.v4.content.Loader;

import io.github.mcvlaga.moonlauncher.BasePresenter;
import io.github.mcvlaga.moonlauncher.BaseView;

public interface SettingsContract {

    interface View extends BaseView<Presenter> {

    }

    interface Presenter extends BasePresenter {
        void addHiddenApp(String packageName);
        void removeHiddenApp(String packageName);
        void setMaxTasksNumber(int maxTasks, String key);
        void setNumberOfForecasts(int numForecasts, String key);
        void setLocationCity(String key, String city);
        void setLocationDetails(double latitude, double longitude);
        int getNumberOfHiddenApps();
        Loader<Cursor> createAppsLoader();
    }
}
