package io.github.mcvlaga.moonlauncher.home.tasks.list;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import io.github.mcvlaga.moonlauncher.home.HomeLoaderProvider;
import io.github.mcvlaga.moonlauncher.home.tasks.TasksFilterType;
import io.github.mcvlaga.moonlauncher.home.tasks.data.Task;
import io.github.mcvlaga.moonlauncher.home.tasks.data.TasksRepository;

public class TasksListPresenter implements TasksListContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int TASKS_LOADER_ID = 0;
    private final static String KEY_TASKS_FILTER = "TASKS_FILTER";

    private final TasksListContract.View mTasksListView;

    private final HomeLoaderProvider mTasksListLoaderProvider;

    private final LoaderManager mLoaderManager;

    private final TasksRepository mTasksListRepository;

    private TasksFilterType mCurrentFiltering;

    public TasksListPresenter(@NonNull HomeLoaderProvider tasksListLoaderProvider, @NonNull LoaderManager loaderManager,
                              @NonNull TasksRepository tasksListRepository, @NonNull TasksListContract.View tasksListView) {
        mTasksListView = tasksListView;
        mTasksListLoaderProvider = tasksListLoaderProvider;
        mLoaderManager = loaderManager;
        mTasksListRepository = tasksListRepository;
        mCurrentFiltering = mTasksListRepository.getTasksListFilter();
        mTasksListView.setPresenter(this);
        showFilterLabel();
    }
    @Override
    public void start() {
        Bundle args = new Bundle();
        args.putSerializable(KEY_TASKS_FILTER, mCurrentFiltering);
        if (mLoaderManager.getLoader(TASKS_LOADER_ID) == null) {
            mLoaderManager.initLoader(TASKS_LOADER_ID, args, this);
        } else {
            mLoaderManager.restartLoader(TASKS_LOADER_ID, args, this);
        }
    }

    private void showFilterLabel() {
        switch (mCurrentFiltering) {
            case ACTIVE_TASKS:
                mTasksListView.showActiveFilterLabel();
                break;
            case COMPLETED_TASKS:
                mTasksListView.showCompletedFilterLabel();
                break;
            default:
                mTasksListView.showAllFilterLabel();
                break;
        }
    }

    @Override
    public void setFilterType(TasksFilterType filterType) {
        mCurrentFiltering = filterType;
        Bundle args = new Bundle();
        args.putSerializable(KEY_TASKS_FILTER, filterType);
        if (mLoaderManager.getLoader(TASKS_LOADER_ID) == null) {
            mLoaderManager.initLoader(TASKS_LOADER_ID, args, this);
        } else {
            mLoaderManager.restartLoader(TASKS_LOADER_ID, args, this);
        }
        mTasksListRepository.setTasksListFilter(filterType);
    }

    @Override
    public void removeCompletedTasks() {
        mTasksListRepository.removeCompletedTasks();
    }


    @Override
    public void completeTask(Task task) {
        mTasksListRepository.completeTask(task);
        mTasksListView.showUndoSnackBar(task);
    }

    @Override
    public void activateTask(Task task) {
        mTasksListRepository.activateTask(task);
    }

    @Override
    public void saveTask(Task task) {
        mTasksListRepository.saveTask(task);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        TasksFilterType filterType = (TasksFilterType) args.getSerializable(KEY_TASKS_FILTER);
        return mTasksListLoaderProvider.createTasksLoader(filterType);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mTasksListView.showTasks(data);
        showFilterLabel();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
