package io.github.mcvlaga.moonlauncher.home.tasks.list;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import io.github.mcvlaga.moonlauncher.R;
import io.github.mcvlaga.moonlauncher.home.HomeLoaderProvider;
import io.github.mcvlaga.moonlauncher.home.tasks.data.TasksRepository;
import io.github.mcvlaga.moonlauncher.utils.ActivityUtils;

public class TasksListActivity extends AppCompatActivity{

    private TasksListPresenter mTasksListPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.tasks_list_activity);

        TasksListFragment tasksListFragment =
                (TasksListFragment) getSupportFragmentManager().findFragmentById(R.id.tasksContentFrame);
        if (tasksListFragment == null) {
            // Create the fragment
            tasksListFragment = TasksListFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), tasksListFragment, R.id.tasksContentFrame);
        }


        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        TasksRepository repository = new TasksRepository(getContentResolver(), sp);
        HomeLoaderProvider homeLoaderProvider = new HomeLoaderProvider(this);
        mTasksListPresenter = new TasksListPresenter(homeLoaderProvider, getSupportLoaderManager(),
                repository, tasksListFragment);
    }
}
