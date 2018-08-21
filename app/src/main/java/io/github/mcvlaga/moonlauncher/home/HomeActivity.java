package io.github.mcvlaga.moonlauncher.home;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import io.github.mcvlaga.moonlauncher.R;
import io.github.mcvlaga.moonlauncher.home.apps.data.AppsRepository;
import io.github.mcvlaga.moonlauncher.home.apps.drawer.AppsDrawerFragment;
import io.github.mcvlaga.moonlauncher.home.apps.drawer.AppsDrawerPresenter;
import io.github.mcvlaga.moonlauncher.home.weather.utils.WeatherSyncUtils;
import io.github.mcvlaga.moonlauncher.utils.ActivityUtils;

public class HomeActivity extends AppCompatActivity {

    private HomePresenter mHomePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        setUpSharedPreferences();
        hideActionBar();

        HomeFragment homeFragment =
                (HomeFragment) getSupportFragmentManager().findFragmentById(R.id.homeContentFrame);
        if (homeFragment == null) {
            // Create the fragment
            homeFragment = HomeFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), homeFragment, R.id.homeContentFrame);
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        HomeRepository homeRepository = new HomeRepository(getContentResolver(), sharedPreferences);
        HomeLoaderProvider homeLoaderProvider = new HomeLoaderProvider(this);
        mHomePresenter = new HomePresenter(homeLoaderProvider, getSupportLoaderManager(),
                homeRepository, homeFragment);

        AppsDrawerFragment appsDrawerFragment =
                (AppsDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.appDrawerContentFrame);
        if (appsDrawerFragment == null) {
            // Create the fragment
            appsDrawerFragment = AppsDrawerFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), appsDrawerFragment, R.id.appDrawerContentFrame);
        }

        AppsRepository appsRepository = new AppsRepository(getContentResolver());

        new AppsDrawerPresenter(homeLoaderProvider, getSupportLoaderManager(),
                appsRepository, appsDrawerFragment);

        WeatherSyncUtils.initialize(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    private void setUpSharedPreferences() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);

        boolean showWeather = sharedPreferences.getBoolean(
                getString(R.string.pref_show_weather_key),
                getResources().getBoolean(R.bool.pref_show_weather_default));

        boolean showTasks = sharedPreferences.getBoolean(
                getString(R.string.pref_show_tasks_key),
                getResources().getBoolean(R.bool.pref_show_tasks_default));

        HomeViewType.WEATHER.setShown(showWeather);
        HomeViewType.TASKS.setShown(showTasks);
    }

    private void hideActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    @Override
    public void onBackPressed() {
        // Do nothing
    }
}
