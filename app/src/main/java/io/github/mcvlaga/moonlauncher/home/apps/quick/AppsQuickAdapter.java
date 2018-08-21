package io.github.mcvlaga.moonlauncher.home.apps.quick;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.github.mcvlaga.moonlauncher.R;
import io.github.mcvlaga.moonlauncher.home.apps.data.AppModel;
import io.github.mcvlaga.moonlauncher.utils.ListUtils;

public class AppsQuickAdapter extends ArrayAdapter<AppModel> {

    public interface OnAppClickListener {
        void onAppClicked(View view, AppModel app);
        void onAppLongClicked(View view, AppModel app);
    }

    public static class ViewHolder {
        public ImageButton appImageButton;
        public TextView appTitle;
    }

    private OnAppClickListener mListener;

    private List<AppModel> appsItems = new ArrayList<>();
    private GridView mGridView;

    public AppsQuickAdapter(Context context, OnAppClickListener listener,
                            ArrayList<AppModel> apps, GridView gridView) {
        super(context, R.layout.apps_quick_item, apps);
        mListener = listener;
        appsItems = apps;
        mGridView = gridView;
    }

    public void setData(ArrayList<AppModel> appsData) {
        appsItems.clear();
        appsItems.addAll(appsData);
        ListUtils.setGridViewHeightBasedOnChildren(mGridView);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // Get the data item for this position
        final AppModel app = appsItems.get(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.apps_quick_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.appImageButton = convertView.findViewById(R.id.app_image_button);
            viewHolder.appTitle = convertView.findViewById(R.id.app_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.appTitle.setText(app.getTitle());
        viewHolder.appImageButton.setImageBitmap(
                BitmapFactory.decodeByteArray(app.getIcon(), 0, app.getIcon().length));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onAppClicked(view, app);
            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mListener.onAppLongClicked(view, app);
                return true;
            }
        });

        return convertView;
    }
}
