package io.github.mcvlaga.moonlauncher.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ListUtils {

    public static void setListViewHeightTo(ListView listView, int height) {
        ViewGroup.LayoutParams layoutParams = listView.getLayoutParams();
        layoutParams.height = height;
        listView.requestLayout();
    }

    public static int getListViewHeight(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return 0;
        }

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);

            if (listItem != null){

                listItem.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                totalHeight += listItem.getMeasuredHeight();
            }
        }

        totalHeight = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));

        return totalHeight;
    }

    public static int getGridViewHeight(GridView gridView) {

        ListAdapter listAdapter = gridView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return 0;
        }

        int totalHeight = gridView.getPaddingTop() + gridView.getPaddingBottom();
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(gridView.getWidth(), View.MeasureSpec.AT_MOST);
        int numberOfRowsRemainder = listAdapter.getCount() % 5;
        int numberOfRows = numberOfRowsRemainder == 0 ? listAdapter.getCount() / 5 : listAdapter.getCount() / 5 + 1;
        for (int i = 0; i < numberOfRows; i++) {
            View listItem = listAdapter.getView(i, null, gridView);

            if (listItem != null){

                listItem.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                totalHeight += listItem.getMeasuredHeight();
            }
        }

        return totalHeight;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = getListViewHeight(listView);
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static void setGridViewHeightBasedOnChildren(GridView gridView) {
        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = getGridViewHeight(gridView);
        gridView.setLayoutParams(params);
        gridView.requestLayout();
    }
}
