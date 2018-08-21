package io.github.mcvlaga.moonlauncher.home;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.github.mcvlaga.moonlauncher.R;
import io.github.mcvlaga.moonlauncher.home.apps.data.AppModel;
import io.github.mcvlaga.moonlauncher.home.apps.quick.AppsQuickAdapter;
import io.github.mcvlaga.moonlauncher.home.apps.quick.AppsQuickViewHolder;
import io.github.mcvlaga.moonlauncher.home.data.HomePreferences;
import io.github.mcvlaga.moonlauncher.home.tasks.TasksViewHolder;
import io.github.mcvlaga.moonlauncher.home.tasks.data.Task;
import io.github.mcvlaga.moonlauncher.home.weather.WeatherViewHolder;
import io.github.mcvlaga.moonlauncher.home.weather.data.DailyWeatherModel;
import io.github.mcvlaga.moonlauncher.home.weather.data.HourlyWeatherModel;
import io.github.mcvlaga.moonlauncher.home.weather.utils.WeatherUtils;
import io.github.mcvlaga.moonlauncher.utils.DragAndDropHelper;

public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements DragAndDropHelper.ActionCompletionContract {


    private List<KeyCursorPair> mHomeDataList = new ArrayList<>();

    private final static HomeViewType[] homeViewTypeValues = HomeViewType.values();

    private final AppsQuickAdapter.OnAppClickListener mAppsItemListener;

    private final SearchViewHolder.OnSearchClickListener mSearchClickListener;

    private final HomeFragment.TasksListener mTasksListener;

    private Context mContext;

    public HomeAdapter(Context context,
                       HomeFragment.TasksListener tasksListener,
                       AppsQuickAdapter.OnAppClickListener appsListener,
                       SearchViewHolder.OnSearchClickListener searchListener) {
        mContext = context;
        mTasksListener = tasksListener;
        mAppsItemListener = appsListener;
        mSearchClickListener = searchListener;
        for (HomeViewType viewType : homeViewTypeValues) {
            if (viewType.isShown()) {
                mHomeDataList.add(new KeyCursorPair(viewType, null));
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view;

        switch (homeViewTypeValues[viewType]) {
            case WEATHER:
                view = inflater.inflate(R.layout.weather_home_item, parent, false);
                return new WeatherViewHolder(view);
            case TASKS:
                view = inflater.inflate(R.layout.tasks_home_item, parent, false);
                return new TasksViewHolder(view, mTasksListener);
            case APPS:
                view = inflater.inflate(R.layout.apps_quick_home_item, parent, false);
                return new AppsQuickViewHolder(view, mContext, mAppsItemListener);
            case SEARCH:
                view = inflater.inflate(R.layout.search_home_item, parent, false);
                return new SearchViewHolder(view, mSearchClickListener);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        HomeViewType viewType = homeViewTypeValues[holder.getItemViewType()];
        switch (viewType) {
            case WEATHER:
                Cursor weatherCursor = getCursorForKey(viewType);
                if (weatherCursor != null && !weatherCursor.isClosed()) {
                    if (weatherCursor.getCount() == 0) {
                        break;
                    }
                    WeatherViewHolder weatherViewHolder = (WeatherViewHolder) holder;
                    weatherCursor.moveToPosition(-1);
                    weatherCursor.moveToNext();
                    HourlyWeatherModel hourlyModel = HourlyWeatherModel.from(weatherCursor);
                    weatherViewHolder.cityTextView.setText(WeatherUtils.getCityName(mContext));
                    weatherViewHolder.temperatureTextView.setText(String.format(
                            Locale.getDefault(), "%d", hourlyModel.getTemperature()));
                    weatherViewHolder.degreesUnitTexView.setText(
                            WeatherUtils.getPreferedWeatherDegreesUnit(mContext));
                    weatherViewHolder.summaryTextView.setText(hourlyModel.getSummary());
                    weatherViewHolder.apparentTempTextView.setText(
                            WeatherUtils.getApparentTemperature(mContext, hourlyModel.getApparentTemperature()));
                    weatherViewHolder.highTextView.setText(
                            WeatherUtils.getTemperature(mContext, hourlyModel.getHighTemperature()));
                    weatherViewHolder.lowTextView.setText(
                            WeatherUtils.getTemperature(mContext, hourlyModel.getLowTemperature()));
                    weatherViewHolder.humidityTextView.setText(
                            WeatherUtils.getHumidity(mContext, hourlyModel.getHumidity()));
                    weatherViewHolder.windTextView.setText(
                            WeatherUtils.getWindSpeed(mContext, hourlyModel.getWindSpeed()));
                    weatherViewHolder.iconImageView.setImageResource(hourlyModel.getIconId());


                    ArrayList<DailyWeatherModel> dailyData = new ArrayList<>();

                    int numPrefForecasts = HomePreferences.getNumberOfForecasts(mContext);
                    int currNumForecasts = weatherCursor.getCount();
                    int numForecasts;
                    if (numPrefForecasts > currNumForecasts) {
                        numForecasts = currNumForecasts;
                    } else {
                        numForecasts = numPrefForecasts;
                    }
                    for (int i = 0; i < numForecasts; i++) {
                        weatherCursor.moveToNext();
                        dailyData.add(DailyWeatherModel.from(weatherCursor));
                    }
                    weatherViewHolder.adapter.setData(dailyData);
                }
                break;
            case TASKS:
                Cursor tasksCursor = getCursorForKey(viewType);
                if (tasksCursor != null && !tasksCursor.isClosed()) {
                    TasksViewHolder tasksViewHolder = (TasksViewHolder) holder;
                    tasksCursor.moveToPosition(-1);
                    ArrayList<Task> tasksData = new ArrayList<>();
                    int maxPrefTasks = HomePreferences.getMaxTasksNumber(mContext);
                    int currNumTasks = tasksCursor.getCount();
                    int maxTasks;
                    if (maxPrefTasks > currNumTasks) {
                        maxTasks = currNumTasks;
                    } else {
                        maxTasks = maxPrefTasks;
                    }
                    for (int i = 0; i < maxTasks; i++) {
                        tasksCursor.moveToNext();
                        tasksData.add(Task.from(tasksCursor));
                    }
                    tasksViewHolder.mAdapter.setData(tasksData);
                    if (tasksData.size() == 0) {
                        tasksViewHolder.displayEmptyListMessage(true);
                    } else {
                        tasksViewHolder.displayEmptyListMessage(false);
                    }
                }
                break;
            case APPS:
                Cursor appsCursor = getCursorForKey(viewType);
                if (appsCursor != null && !appsCursor.isClosed()) {
                    AppsQuickViewHolder appsQuickViewHolder = (AppsQuickViewHolder) holder;

                    ArrayList<AppModel> appsData = new ArrayList<>();
                    appsCursor.moveToPosition(-1);
                    while (appsCursor.moveToNext()) {
                        appsData.add(AppModel.from(appsCursor));
                    }

                    appsQuickViewHolder.adapter.setData(appsData);
                }
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        HomeViewType viewType = mHomeDataList.get(position).getViewTypeKey();
        return viewType.ordinal();
    }

    @Override
    public int getItemCount() {
        return mHomeDataList.size();
    }

    private Cursor getCursorForKey(HomeViewType viewType) {
        Cursor cursor = null;
        for (KeyCursorPair pair : mHomeDataList) {
            if (pair.getViewTypeKey() == viewType && pair.getCursor() != null) {
                cursor = pair.getCursor();
            }
        }
        return cursor;
    }

    public void swapWeatherCursor(Cursor weatherCursor) {
        int index = getIndexForKey(HomeViewType.WEATHER);

        if (!HomeViewType.WEATHER.isShown()) {
            mHomeDataList.remove(getIndexForKey(HomeViewType.WEATHER));
        } else {
            if (index > -1) {
                mHomeDataList.get(index).setCursor(weatherCursor);
            } else {
                mHomeDataList.add(new KeyCursorPair(HomeViewType.WEATHER, weatherCursor));
            }
        }
        notifyDataSetChanged();
    }

    public void swapTasksCursor(Cursor tasksCursor) {
        int index = getIndexForKey(HomeViewType.TASKS);

        if (!HomeViewType.TASKS.isShown()) {
            mHomeDataList.remove(getIndexForKey(HomeViewType.TASKS));
        } else {
            if (index > -1) {
                mHomeDataList.get(index).setCursor(tasksCursor);
            } else {
                mHomeDataList.add(new KeyCursorPair(HomeViewType.TASKS, tasksCursor));
            }
        }
        notifyDataSetChanged();
    }

    public void swapQuickAppsCursor(Cursor appsCursor) {
        int index = getIndexForKey(HomeViewType.APPS);

        if (!HomeViewType.APPS.isShown()) {
            mHomeDataList.remove(getIndexForKey(HomeViewType.APPS));
        } else {
            if (index > -1) {
                mHomeDataList.get(index).setCursor(appsCursor);
            } else {
                mHomeDataList.add(new KeyCursorPair(HomeViewType.APPS, appsCursor));
            }
        }
        notifyDataSetChanged();
    }

    public void showSearch() {
        int index = getIndexForKey(HomeViewType.SEARCH);
        if (index == -1) {
            mHomeDataList.add(new KeyCursorPair(HomeViewType.SEARCH, null));
        }
        notifyDataSetChanged();
    }

    private int getIndexForKey(HomeViewType key) {
        for (int i = 0; i < mHomeDataList.size(); i++) {
            if (mHomeDataList.get(i).getViewTypeKey() == key) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onViewMoved(int oldPosition, int newPosition) {
        KeyCursorPair pair = mHomeDataList.get(oldPosition);
        mHomeDataList.remove(oldPosition);
        mHomeDataList.add(newPosition, pair);
        notifyItemMoved(oldPosition, newPosition);
    }

    private class KeyCursorPair {
        private HomeViewType viewTypeKey;
        private Cursor cursor;

        KeyCursorPair(HomeViewType viewType, Cursor cursor) {
            viewTypeKey = viewType;
            this.cursor = cursor;
        }

        public HomeViewType getViewTypeKey() {
            return viewTypeKey;
        }

        public Cursor getCursor() {
            return cursor;
        }

        public void setCursor(Cursor cursor) {
            this.cursor = cursor;
        }
    }
}
