package io.github.mcvlaga.moonlauncher.settings;

import io.github.mcvlaga.moonlauncher.home.apps.data.AppsRepository;
import io.github.mcvlaga.moonlauncher.home.tasks.data.TasksRepository;
import io.github.mcvlaga.moonlauncher.home.weather.data.WeatherRepository;

public class SettingsRepository {

    private WeatherRepository mWeatherRepository;
    private TasksRepository mTasksRepository;
    private AppsRepository mAppsRepository;

    public SettingsRepository(WeatherRepository weatherRepository,
                              TasksRepository tasksRepository,
                              AppsRepository appsRepository) {
        mWeatherRepository = weatherRepository;
        mTasksRepository = tasksRepository;
        mAppsRepository = appsRepository;
    }

    public void setNumberOfForecasts(int numOfForecasts, String key) {
        mWeatherRepository.setNumberOfForecasts(numOfForecasts, key);
    }

    public void setLocationCity(String key, String city) {
        mWeatherRepository.setLocationCity(key, city);
    }

    public void setLocationDetails(double lat, double lon) {
        mWeatherRepository.setLocationDetails(lat, lon);
    }

    public void addHiddenApp(String packageName) {
        mAppsRepository.addHiddenApp(packageName);
    }

    public void removeHiddenApp(String packageName) {
        mAppsRepository.removeHiddenApp(packageName);
    }

    public int getNumberOfHiddenApps() {
        return mAppsRepository.getNumberOfHiddenApps();
    }

    public void setMaxTasksNumber(int maxTasks, String key) {
        mTasksRepository.setMaxTasksNumber(maxTasks, key);
    }
}
