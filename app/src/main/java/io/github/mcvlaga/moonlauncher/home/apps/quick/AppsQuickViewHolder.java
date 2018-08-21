package io.github.mcvlaga.moonlauncher.home.apps.quick;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import io.github.mcvlaga.moonlauncher.R;
import io.github.mcvlaga.moonlauncher.home.apps.data.AppModel;

public class AppsQuickViewHolder extends RecyclerView.ViewHolder {

    private ImageButton addButton;
    private TextView emptyGridTextView;
    public GridView appsGridView;
    public AppsQuickAdapter adapter;

    public AppsQuickViewHolder(View view, Context context, AppsQuickAdapter.OnAppClickListener listener) {
        super(view);
        addButton = view.findViewById(R.id.add_button);
        appsGridView = view.findViewById(R.id.apps_quick_grid_view);
        emptyGridTextView = view.findViewById(R.id.empty_apps_quick_grid);
        adapter = new AppsQuickAdapter(context, listener, new ArrayList<AppModel>(), appsGridView);
        appsGridView.setAdapter(adapter);
    }
}
