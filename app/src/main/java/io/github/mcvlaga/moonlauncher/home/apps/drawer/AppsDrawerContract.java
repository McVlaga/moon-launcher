package io.github.mcvlaga.moonlauncher.home.apps.drawer;

import android.database.Cursor;

import io.github.mcvlaga.moonlauncher.BasePresenter;
import io.github.mcvlaga.moonlauncher.BaseView;

public interface AppsDrawerContract {

    interface View extends BaseView<Presenter> {
        void showApps(Cursor cursor);
    }

    interface Presenter extends BasePresenter {
        void reloadApps();
        void addHiddenApp(String packageName);
        void addQuickApp(String packageName);
    }
}
