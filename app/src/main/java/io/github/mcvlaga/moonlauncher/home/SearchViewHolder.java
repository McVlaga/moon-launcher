package io.github.mcvlaga.moonlauncher.home;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import io.github.mcvlaga.moonlauncher.R;

public class SearchViewHolder extends RecyclerView.ViewHolder {

    public interface OnSearchClickListener{
        void onSearchClicked();
    }

    private OnSearchClickListener mListener;

    public View searchView;

    public SearchViewHolder(View view, OnSearchClickListener listener) {
        super(view);
        searchView = view.findViewById(R.id.search_view);
        mListener = listener;
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onSearchClicked();
            }


        });
        searchView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
    }
}
