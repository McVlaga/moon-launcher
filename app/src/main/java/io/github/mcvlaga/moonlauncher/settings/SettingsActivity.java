package io.github.mcvlaga.moonlauncher.settings;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;

import io.github.mcvlaga.moonlauncher.R;
import io.github.mcvlaga.moonlauncher.home.HomeLoaderProvider;
import io.github.mcvlaga.moonlauncher.home.apps.data.AppsRepository;
import io.github.mcvlaga.moonlauncher.home.tasks.data.TasksRepository;
import io.github.mcvlaga.moonlauncher.home.weather.data.WeatherRepository;
import io.github.mcvlaga.moonlauncher.utils.ActivityUtils;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.settings_activity);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        SettingsFragment settingsFragment =
                (SettingsFragment) getSupportFragmentManager().findFragmentById(R.id.settingsContentFrame);
        if (settingsFragment == null) {
            // Create the fragment
            settingsFragment = SettingsFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), settingsFragment, R.id.settingsContentFrame);
        }

        HomeLoaderProvider loaderProvider = new HomeLoaderProvider(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SettingsRepository settingsRepository = new SettingsRepository(
                new WeatherRepository(preferences),
                new TasksRepository(getContentResolver(), preferences),
                new AppsRepository(getContentResolver())
        );
        new SettingsPresenter(loaderProvider, settingsFragment, settingsRepository);
    }
}
