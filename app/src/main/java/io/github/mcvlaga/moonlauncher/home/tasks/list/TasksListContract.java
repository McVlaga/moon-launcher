package io.github.mcvlaga.moonlauncher.home.tasks.list;

import android.database.Cursor;

import io.github.mcvlaga.moonlauncher.BasePresenter;
import io.github.mcvlaga.moonlauncher.BaseView;
import io.github.mcvlaga.moonlauncher.home.tasks.TasksFilterType;
import io.github.mcvlaga.moonlauncher.home.tasks.data.Task;

public interface TasksListContract {

    interface View extends BaseView<Presenter> {
        void showTasks(Cursor tasksData);
        void showUndoSnackBar(Task task);
        void showAllFilterLabel();
        void showCompletedFilterLabel();
        void showActiveFilterLabel();
    }

    interface Presenter extends BasePresenter {
        void completeTask(Task task);
        void activateTask(Task task);
        void saveTask(Task task);
        void setFilterType(TasksFilterType filterType);
        void removeCompletedTasks();
    }
}
