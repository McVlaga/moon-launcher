package io.github.mcvlaga.moonlauncher.home;

import android.database.Cursor;

import io.github.mcvlaga.moonlauncher.BasePresenter;
import io.github.mcvlaga.moonlauncher.BaseView;
import io.github.mcvlaga.moonlauncher.home.tasks.data.Task;

public interface HomeContract {

    interface View extends BaseView<Presenter> {
        void showWeather(Cursor weatherData);
        void showTasks(Cursor tasksData);
        void showUndoSnackBar(Task task);
        void showQuickApps(Cursor appsCursor);
        void showSearch();
    }

    interface Presenter extends BasePresenter {
        void completeTask(Task task);
        void saveTask(Task task);
        void removeQuickApp(String packageName);
    }
}