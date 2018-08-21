package io.github.mcvlaga.moonlauncher.home.apps.drawer;

import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import io.github.mcvlaga.moonlauncher.R;
import io.github.mcvlaga.moonlauncher.home.apps.data.AppModel;

public class AppsDrawerAdapter extends RecyclerView.Adapter<AppsDrawerAdapter.AppsDrawerViewHolder> {

    interface OnAppClickListener {
        void onAppClicked(View v, int position);
        void onAppLongClicked(View v, int position);
    }

    private Cursor mCursor;
    private OnAppClickListener mListener;

    public AppsDrawerAdapter() {
        mCursor = null;
    }

    @Override
    public AppsDrawerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.apps_drawer_item,
                parent,
                false);
        return new AppsDrawerViewHolder(view);
    }


    @Override
    public void onBindViewHolder(AppsDrawerViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        AppModel app = AppModel.from(mCursor);
        holder.appName.setText(app.getTitle());

        holder.appImageButton.setImageBitmap(
                BitmapFactory.decodeByteArray(app.getIcon(), 0, app.getIcon().length));
    }

    @Override
    public void onViewDetachedFromWindow(AppsDrawerViewHolder holder) {
        holder.clearAnimation();
    }

    @Override
    public int getItemCount() {
        return (mCursor == null) ? 0 : mCursor.getCount();
    }

    public AppModel getApp(int position) {
        mCursor.moveToPosition(position);
        return AppModel.from(mCursor);
    }

    public void swapCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnAppClickListener listener) {
        mListener = listener;
    }

    class AppsDrawerViewHolder extends RecyclerView.ViewHolder {
        TextView appName;
        ImageButton appImageButton;

        AppsDrawerViewHolder(View itemView) {
            super(itemView);
            appName = itemView.findViewById(R.id.app_name);
            appImageButton = itemView.findViewById(R.id.app_image_button);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onAppClicked(view, getAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mListener.onAppLongClicked(view, getAdapterPosition());
                    return true;
                }
            });
        }

        public void clearAnimation() {
            itemView.clearAnimation();
        }
    }
}