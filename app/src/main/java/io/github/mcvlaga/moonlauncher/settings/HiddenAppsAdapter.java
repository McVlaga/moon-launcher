package io.github.mcvlaga.moonlauncher.settings;

import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import io.github.mcvlaga.moonlauncher.R;
import io.github.mcvlaga.moonlauncher.home.apps.data.AppModel;
import io.github.mcvlaga.moonlauncher.home.apps.data.AppsDatabaseContract;

public class HiddenAppsAdapter extends RecyclerView.Adapter<HiddenAppsAdapter.ViewHolder> {

    private ArrayList<Boolean> appsChecked = new ArrayList<>();

    private Cursor mCursor;

    public HiddenAppsAdapter() {
        mCursor = null;
    }

    @Override
    public HiddenAppsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.hidden_apps_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        AppModel app = AppModel.from(mCursor);
        holder.appName.setText(app.getTitle());
        holder.appIcon.setImageBitmap(
                BitmapFactory.decodeByteArray(app.getIcon(), 0, app.getIcon().length));

        holder.checkBox.setChecked(appsChecked.get(position));

        holder.hiddenAppParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.checkBox.isChecked()) {
                    holder.checkBox.setChecked(false);
                    appsChecked.set(holder.getAdapterPosition(), false);
                } else {
                    holder.checkBox.setChecked(true);
                    appsChecked.set(holder.getAdapterPosition(), true);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return (mCursor == null) ? 0 : mCursor.getCount();
    }

    public AppModel getApp(int position) {
        mCursor.moveToPosition(position);
        return AppModel.from(mCursor);
    }

    public boolean getHiddenState(int position) {
        return appsChecked.get(position);
    }

    public void swapCursor(Cursor cursor) {
        mCursor = cursor;
        if (mCursor != null) {
            mCursor.moveToPosition(-1);
            while (mCursor.moveToNext()) {
                int hiddenInt = mCursor.getInt(mCursor.getColumnIndexOrThrow(
                        AppsDatabaseContract.AppEntry.COLUMN_NAME_HIDDEN));
                appsChecked.add(hiddenInt == 1);
            }
        }
        notifyDataSetChanged();
    }

    public ArrayList<AppModel> getApps() {
        mCursor.moveToFirst();
        ArrayList<AppModel> apps = new ArrayList<>();
        while (mCursor.moveToNext()) {
            apps.add(AppModel.from(mCursor));
        }
        return apps;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        ImageView appIcon;
        TextView appName;
        LinearLayout hiddenAppParent;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            checkBox = itemLayoutView.findViewById(R.id.hidden_app_checkbox);
            appIcon = itemLayoutView.findViewById(R.id.hidden_app_icon);
            appName = itemLayoutView.findViewById(R.id.hidden_app_name);
            hiddenAppParent = itemLayoutView.findViewById(R.id.hidden_app_parent);
        }
    }
}
