package io.github.mcvlaga.moonlauncher.home.weather;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.github.mcvlaga.moonlauncher.R;
import io.github.mcvlaga.moonlauncher.home.weather.data.DailyWeatherModel;
import io.github.mcvlaga.moonlauncher.home.weather.utils.WeatherUtils;
import io.github.mcvlaga.moonlauncher.utils.HomeDateUtils;
import io.github.mcvlaga.moonlauncher.utils.ListUtils;

public class WeatherAdapter extends ArrayAdapter<DailyWeatherModel> {

    // View lookup cache
    private static class ViewHolder {
        TextView date;
        ImageView icon;
        TextView high;
        TextView low;
    }

    private List<DailyWeatherModel> forecastItems = new ArrayList<>();

    private ListView weatherListView;

    public WeatherAdapter(ArrayList<DailyWeatherModel> data, Context context, ListView listView) {
        super(context, R.layout.weather_item, data);
        forecastItems = data;
        weatherListView = listView;
    }

    public void setData(ArrayList<DailyWeatherModel> dailyData) {
        forecastItems.clear();
        forecastItems.addAll(dailyData);
        if (weatherListView.getLayoutParams().height != 0) {
            ListUtils.setListViewHeightBasedOnChildren(weatherListView);
        }
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        DailyWeatherModel weatherModel = forecastItems.get(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.weather_item, parent, false);
            viewHolder.date = convertView.findViewById(R.id.forecast_date);
            viewHolder.high = convertView.findViewById(R.id.forecast_high);
            viewHolder.low = convertView.findViewById(R.id.forecast_low);
            viewHolder.icon = convertView.findViewById(R.id.forecast_icon);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.date.setText(
                HomeDateUtils.getFriendlyDateStringForWeather(weatherModel.getTime()));
        viewHolder.high.setText(
                WeatherUtils.getTemperature(getContext(), weatherModel.getHighTemperature()));
        viewHolder.low.setText(
                WeatherUtils.getTemperature(getContext(), weatherModel.getLowTemperature()));
        viewHolder.icon.setImageResource(weatherModel.getIconId());
        // Return the completed view to render on screen
        return convertView;
    }
}