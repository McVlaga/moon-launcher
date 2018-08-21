package io.github.mcvlaga.moonlauncher.home;

import android.content.ContentResolver;
import android.content.SharedPreferences;

import io.github.mcvlaga.moonlauncher.home.apps.data.AppsRepository;
import io.github.mcvlaga.moonlauncher.home.tasks.data.Task;
import io.github.mcvlaga.moonlauncher.home.tasks.data.TasksRepository;

public class HomeRepository {

    private TasksRepository mTasksRepository;
    private AppsRepository mAppsRepository;

    public HomeRepository(ContentResolver contentResolver, SharedPreferences sharedPreferences) {
        mTasksRepository = new TasksRepository(contentResolver, sharedPreferences);
        mAppsRepository = new AppsRepository(contentResolver);
    }

    public void completeTask(Task task) {
        mTasksRepository.completeTask(task);
    }

    public void saveTask(Task task) {
        mTasksRepository.saveTask(task);
    }

    public void removeQuickApp(String packageName) {
        mAppsRepository.removeQuickApp(packageName);
    }
}
