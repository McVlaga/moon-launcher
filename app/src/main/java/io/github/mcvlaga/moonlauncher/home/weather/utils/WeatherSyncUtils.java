package io.github.mcvlaga.moonlauncher.home.weather.utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

import io.github.mcvlaga.moonlauncher.home.weather.data.WeatherDatabaseContract;
import io.github.mcvlaga.moonlauncher.home.weather.data.WeatherFirebaseJobService;
import io.github.mcvlaga.moonlauncher.home.weather.data.WeatherSyncIntentService;

public class WeatherSyncUtils {

    private static final int SYNC_INTERVAL_HOURS = 1;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 3;

    private static boolean sInitialized;

    //  COMPLETED (11) Add a sync tag to identify our sync job
    private static final String SUNSHINE_SYNC_TAG = "sunshine-sync";

//  COMPLETED (12) Create a method to schedule our periodic weather sync
    /**
     * Schedules a repeating sync of Sunshine's weather data using FirebaseJobDispatcher.
     * @param context Context used to create the GooglePlayDriver that powers the
     *                FirebaseJobDispatcher
     */
    static void scheduleFirebaseJobDispatcherSync(@NonNull final Context context) {

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        /* Create the Job to periodically sync Sunshine */
        Job syncSunshineJob = dispatcher.newJobBuilder()
                /* The Service that will be used to sync Sunshine's data */
                .setService(WeatherFirebaseJobService.class)
                /* Set the UNIQUE tag used to identify this Job */
                .setTag(SUNSHINE_SYNC_TAG)
                /*
                 * Network constraints on which this Job should run. We choose to run on any
                 * network, but you can also choose to run only on un-metered networks or when the
                 * device is charging. It might be a good idea to include a preference for this,
                 * as some users may not want to download any data on their mobile plan. ($$$)
                 */
                .setConstraints(Constraint.ON_ANY_NETWORK)
                /*
                 * setLifetime sets how long this job should persist. The options are to keep the
                 * Job "forever" or to have it die the next time the device boots up.
                 */
                .setLifetime(Lifetime.FOREVER)
                /*
                 * We want Sunshine's weather data to stay up to date, so we tell this Job to recur.
                 */
                .setRecurring(true)
                /*
                 * We want the weather data to be synced every 3 to 4 hours. The first argument for
                 * Trigger's static executionWindow method is the start of the time frame when the
                 * sync should be performed. The second argument is the latest point in time at
                 * which the data should be synced. Please note that this end time is not
                 * guaranteed, but is more of a guideline for FirebaseJobDispatcher to go off of.
                 */
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                /*
                 * If a Job with the tag with provided already exists, this new job will replace
                 * the old one.
                 */
                .setReplaceCurrent(true)
                /* Once the Job is ready, call the builder's build method to return the Job */
                .build();

        /* Schedule the Job with the dispatcher */
        dispatcher.schedule(syncSunshineJob);
    }

    synchronized public static void initialize(@NonNull final Context context) {


        /*
         * Only perform initialization once per app lifetime. If initialization has already been
         * performed, we have nothing to do in this method.
         */
        if (sInitialized) return;

        sInitialized = true;

        scheduleFirebaseJobDispatcherSync(context);

        /*
         * We need to check to see if our ContentProvider has data to display in our forecast
         * list. However, performing a query on the main thread is a bad idea as this may
         * cause our UI to lag. Therefore, we create a thread in which we will run the query
         * to check the contents of our ContentProvider.
         */
        new AsyncTask<Void, Void, Void>() {
            @Override
            public Void doInBackground( Void... voids ) {

                Uri hourlyQueryUri = WeatherDatabaseContract.WeatherEntry.CONTENT_URI_WEATHER_HOURLY;

                String[] hourlyProjectionColumns = {WeatherDatabaseContract.WeatherEntry._ID};
                String hourlySelectionStatement = WeatherDatabaseContract.WeatherEntry.getSqlSelectForNowOnwards();

                Cursor hourlyCursor = context.getContentResolver().query(
                        hourlyQueryUri,
                        hourlyProjectionColumns,
                        hourlySelectionStatement,
                        null,
                        null);

                Uri dailyQueryUri = WeatherDatabaseContract.WeatherEntry.CONTENT_URI_WEATHER_DAILY;

                String[] dailyProjectionColumns = {WeatherDatabaseContract.WeatherEntry._ID};
                String dailySelectionStatement = WeatherDatabaseContract.WeatherEntry.getSqlSelectForTodayOnwards();

                Cursor dailyCursor = context.getContentResolver().query(
                        dailyQueryUri,
                        dailyProjectionColumns,
                        dailySelectionStatement,
                        null,
                        null);

                if (null == dailyCursor || dailyCursor.getCount() == 0
                        || null == hourlyCursor || hourlyCursor.getCount() == 0) {
                    startImmediateSync(context);
                }
                if (hourlyCursor != null) {
                    hourlyCursor.close();
                }
                if (dailyCursor != null) {
                    dailyCursor.close();
                }

                return null;
            }
        }.execute();
    }
    public static void startImmediateSync(@NonNull final Context context) {

        Intent intentToSyncImmediately = new Intent(context, WeatherSyncIntentService.class);
        context.startService(intentToSyncImmediately);
    }
}