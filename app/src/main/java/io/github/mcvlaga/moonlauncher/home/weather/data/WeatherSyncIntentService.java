package io.github.mcvlaga.moonlauncher.home.weather.data;

import android.app.IntentService;
import android.content.Intent;

public class WeatherSyncIntentService extends IntentService {

    //  COMPLETED (6) Create a constructor that calls super and passes the name of this class
    public WeatherSyncIntentService() {
        super("SunshineSyncIntentService");
    }

    //  COMPLETED (7) Override onHandleIntent, and within it, call SunshineSyncTask.syncWeather
    @Override
    protected void onHandleIntent(Intent intent) {
        WeatherSyncTask.syncWeather(this);
    }
}