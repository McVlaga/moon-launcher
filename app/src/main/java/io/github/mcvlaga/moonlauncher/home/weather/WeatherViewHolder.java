package io.github.mcvlaga.moonlauncher.home.weather;


import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import io.github.mcvlaga.moonlauncher.R;
import io.github.mcvlaga.moonlauncher.home.weather.data.DailyWeatherModel;
import io.github.mcvlaga.moonlauncher.utils.ListUtils;

public class WeatherViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {

    // each data item is just a string in this case
    public TextView cityTextView;
    public TextView summaryTextView;
    public TextView temperatureTextView;
    public TextView degreesUnitTexView;
    public ImageView iconImageView;
    public TextView apparentTempTextView;
    public TextView highTextView;
    public TextView lowTextView;
    public TextView humidityTextView;
    public TextView windTextView;

    public CardView cardView;
    private ImageButton expandCardButton;
    private ListView weatherForecastListView;
    public WeatherAdapter adapter;
    public LinearLayout cardLinearLayout;
    public RelativeLayout expandButtonParent;

    private int mTotalHeight = -1;

    private boolean listViewIsExpanded = false;

    public WeatherViewHolder(View view) {
        super(view);
        cardView = (CardView) view;
        cardLinearLayout = view.findViewById(R.id.card_linear_layout);
        cityTextView = view.findViewById(R.id.city_text_view);
        summaryTextView = view.findViewById(R.id.summary_text_view);
        temperatureTextView = view.findViewById(R.id.temperature_text_view);
        degreesUnitTexView = view.findViewById(R.id.degrees_unit_text_view);
        iconImageView = view.findViewById(R.id.icon_image_view);
        apparentTempTextView = view.findViewById(R.id.apparent_temp_text_view);
        highTextView = view.findViewById(R.id.high_text_view);
        lowTextView = view.findViewById(R.id.low_text_view);
        humidityTextView = view.findViewById(R.id.humidity_text_view);
        windTextView = view.findViewById(R.id.wind_text_view);

        expandCardButton = view.findViewById(R.id.expand_card_button);
        expandButtonParent = view.findViewById(R.id.expand_button_parent);
        expandButtonParent.setOnClickListener(this);

        weatherForecastListView = view.findViewById(R.id.weather_forecast_list_view);

        adapter = new WeatherAdapter(
                new ArrayList<DailyWeatherModel>(),
                cardView.getContext(),
                weatherForecastListView);

        weatherForecastListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        weatherForecastListView.setAdapter(adapter);
        weatherForecastListView.setVerticalScrollBarEnabled(false);
    }

    @Override
    public void onClick(View view) {
        mTotalHeight = ListUtils.getListViewHeight(weatherForecastListView);

        if (listViewIsExpanded) {
            ListUtils.setListViewHeightTo(weatherForecastListView, 0);
            listViewIsExpanded = false;
        } else {
            ListUtils.setListViewHeightTo(weatherForecastListView, mTotalHeight);
            listViewIsExpanded = true;
        }
        final int[] stateSet = {android.R.attr.state_checked * (listViewIsExpanded ? 1 : -1)};
        expandCardButton.setImageState(stateSet, true);
    }
}
