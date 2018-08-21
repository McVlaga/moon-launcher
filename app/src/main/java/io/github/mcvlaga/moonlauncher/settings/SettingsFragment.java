package io.github.mcvlaga.moonlauncher.settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

import io.github.mcvlaga.moonlauncher.R;
import io.github.mcvlaga.moonlauncher.home.HomeViewType;
import io.github.mcvlaga.moonlauncher.home.data.HomePreferences;
import io.github.mcvlaga.moonlauncher.home.tasks.data.TasksDatabaseContract;
import io.github.mcvlaga.moonlauncher.home.weather.data.WeatherDatabaseContract;
import io.github.mcvlaga.moonlauncher.home.weather.utils.WeatherSyncUtils;

import static android.app.Activity.RESULT_OK;

public class SettingsFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener, SettingsContract.View {

    private SettingsContract.Presenter mSettingsPresenter;

    private static final int PLACE_PICKER_REQUEST_CODE = 11;
    private static final int HIDDEN_APPS_REQUEST_CODE = 22;

    Preference locationPreference;
    ListPreference unitsPreference;
    Preference numForecastsPreference;
    Preference maxTasksPreference;
    Preference hiddenAppsPreference;

    public SettingsFragment() {

    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void setPresenter(SettingsContract.Presenter presenter) {
        mSettingsPresenter = presenter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);

        unitsPreference = (ListPreference) findPreference(getString(R.string.pref_units_key));
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        String units = sharedPreferences.getString(
                unitsPreference.getKey(), getString(R.string.pref_units_default));
        setPreferenceSummary(unitsPreference, units);

        numForecastsPreference = findPreference(
                getString(R.string.pref_num_forecast_key));
        int numberOfForecasts = Integer.parseInt(sharedPreferences.getString(
                numForecastsPreference.getKey(),
                getString(R.string.pref_num_forecast_default)));
        setPreferenceSummary(numForecastsPreference, numberOfForecasts);

        numForecastsPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showNumOfForecastsPickerDialog();
                return true;
            }
        });

        maxTasksPreference = findPreference(getString(R.string.pref_max_tasks_key));
        int maxTasks = sharedPreferences.getInt(maxTasksPreference.getKey(),
                Integer.parseInt(getString(R.string.pref_max_tasks_default)));
        setPreferenceSummary(maxTasksPreference, maxTasks);

        maxTasksPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showMaxTasksPickerDialog();
                return true;
            }
        });

        hiddenAppsPreference = findPreference(getString(R.string.pref_hidden_apps_key));
        int numberOfHiddenApps = mSettingsPresenter.getNumberOfHiddenApps();
        setHiddenAppsPreferenceSummary(numberOfHiddenApps);
        hiddenAppsPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showHiddenAppsDialog();
                return true;
            }
        });

        locationPreference = findPreference(getString(R.string.pref_location_key));
        locationPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException |
                        GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                return true;
            }
        });

        locationPreference.setSummary(HomePreferences.getLocationCity(getActivity()));
    }

    private void showHiddenAppsDialog() {
        DialogFragment dialogFrag = new HiddenAppsDialogFragment();
        dialogFrag.setTargetFragment(this, HIDDEN_APPS_REQUEST_CODE);
        dialogFrag.show(getFragmentManager(), null);
    }

    private void showMaxTasksPickerDialog() {
        final AlertDialog.Builder d = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.number_picker_dialog, null);
        d.setTitle(getString(R.string.pref_max_tasks_title));
        d.setView(dialogView);
        final NumberPicker numberPicker = dialogView.findViewById(R.id.number_picker);
        numberPicker.setMaxValue(Integer.parseInt(getString(R.string.pref_max_tasks_max)));
        numberPicker.setMinValue(Integer.parseInt(getString(R.string.pref_max_tasks_min)));
        numberPicker.setValue(Integer.parseInt(maxTasksPreference.getSummary().toString()));
        numberPicker.setWrapSelectorWheel(false);
        d.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int maxTasks = numberPicker.getValue();
                maxTasksPreference.setSummary(String.valueOf(maxTasks));
                mSettingsPresenter.setMaxTasksNumber(maxTasks, getString(R.string.pref_max_tasks_key));
            }
        });
        d.setNegativeButton("Cancel", null);
        AlertDialog alertDialog = d.create();
        alertDialog.show();
    }

    private void showNumOfForecastsPickerDialog() {
        final AlertDialog.Builder d = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.number_picker_dialog, null);
        d.setTitle(getString(R.string.pref_num_forecast_title));
        d.setView(dialogView);
        final NumberPicker numberPicker = dialogView.findViewById(R.id.number_picker);
        numberPicker.setMaxValue(Integer.parseInt(getString(R.string.pref_num_forecasts_max)));
        numberPicker.setMinValue(Integer.parseInt(getString(R.string.pref_num_forecasts_min)));
        numberPicker.setValue(Integer.parseInt(numForecastsPreference.getSummary().toString()));
        numberPicker.setWrapSelectorWheel(false);
        d.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int numForecasts = numberPicker.getValue();
                numForecastsPreference.setSummary(String.valueOf(numForecasts));
                mSettingsPresenter.setNumberOfForecasts(numForecasts, getString(R.string.pref_num_forecast_key));
            }
        });
        d.setNegativeButton("Cancel", null);
        AlertDialog alertDialog = d.create();
        alertDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                final Place place = PlacePicker.getPlace(getActivity(), data);
                LatLng latLng = place.getLatLng();
                mSettingsPresenter.setLocationDetails(latLng.latitude, latLng.longitude);

                Geocoder geocoder = new Geocoder(getActivity());
                try {
                    List<Address> addresses = geocoder.getFromLocation(
                            place.getLatLng().latitude, place.getLatLng().longitude, 1);
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    mSettingsPresenter.setLocationCity(getString(R.string.pref_location_key), city);
                    locationPreference.setSummary(city + ", " + state);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == HIDDEN_APPS_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                int numberOfHiddenApps = data.getIntExtra("number", 0);
                setHiddenAppsPreferenceSummary(numberOfHiddenApps);
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals(getString(R.string.pref_location_key))) {
            WeatherSyncUtils.startImmediateSync(getActivity());
        } else if (key.equals(getString(R.string.pref_units_key))) {
            setPreferenceSummary(unitsPreference,
                    sharedPreferences.getString(key, getString(R.string.pref_units_default)));
            WeatherSyncUtils.startImmediateSync(getActivity());
        } else if (key.equals(getString(R.string.pref_show_weather_key))) {
            boolean showWeather = sharedPreferences.getBoolean(key,
                    getResources().getBoolean(R.bool.pref_show_weather_default));
            HomeViewType.WEATHER.setShown(showWeather);
            getActivity().getContentResolver().notifyChange(
                    WeatherDatabaseContract.WeatherEntry.CONTENT_URI_WEATHER, null);
        } else if (key.equals(getString(R.string.pref_show_tasks_key))) {
            boolean showTasks = sharedPreferences.getBoolean(key,
                    getResources().getBoolean(R.bool.pref_show_tasks_default));
            HomeViewType.TASKS.setShown(showTasks);
            getActivity().getContentResolver().notifyChange(
                    TasksDatabaseContract.TaskEntry.CONTENT_URI, null);
        }
    }

    public void addHiddenApp(String packageName) {
        mSettingsPresenter.addHiddenApp(packageName);
    }

    public void removeHiddenApp(String packageName) {
        mSettingsPresenter.removeHiddenApp(packageName);
    }

    public Loader<Cursor> createAppsLoader() {
        return mSettingsPresenter.createAppsLoader();
    }

    private void setHiddenAppsPreferenceSummary(int numberOfHiddenApps) {
        if (numberOfHiddenApps == 1) {
            int hiddenFormatResourceId = R.string.format_hidden_app_summary;
            hiddenAppsPreference.setSummary(
                    String.format(getString(hiddenFormatResourceId), 1));
        } else if(numberOfHiddenApps > 1) {
            int hiddenFormatResourceId = R.string.format_hidden_apps_summary;
            hiddenAppsPreference.setSummary(
                    String.format(getString(hiddenFormatResourceId), numberOfHiddenApps));
        } else {
            hiddenAppsPreference.setSummary("Click to hide an app");
        }
    }

    private void setPreferenceSummary(Preference preference, Object value) {
        String stringValue = value.toString();
        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            // For other preferences, set the summary to the value's simple string representation.
            preference.setSummary(stringValue);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setDivider(new ColorDrawable(Color.TRANSPARENT));
        setDividerHeight(0);
    }

    @Override
    public void onStop() {
        super.onStop();
        // unregister the preference change listener
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        // register the preference change listener
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }
}
