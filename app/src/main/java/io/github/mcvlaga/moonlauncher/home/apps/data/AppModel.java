package io.github.mcvlaga.moonlauncher.home.apps.data;

import android.database.Cursor;

public class AppModel {

    private String mPackage;
    private String mTitle;
    private byte[] mIcon;
    private boolean mHidden;
    private boolean mQuick;
    private int mQuickOrder = -1;

    public AppModel(String pkg, String title, byte[] icon, boolean hidden, boolean quick, int quickOrder) {
        mPackage = pkg;
        mTitle = title;
        mIcon = icon;
        mHidden = hidden;
        mQuick = quick;
        mQuickOrder = quickOrder;
    }

    public AppModel(String pkg, String title, byte[] icon) {
        mPackage = pkg;
        mTitle = title;
        mIcon = icon;
        mHidden = false;
        mQuick = false;
    }

    public String getPackage() {
        return mPackage;
    }

    public String getTitle() {
        return mTitle;
    }

    public byte[] getIcon() {
        return mIcon;
    }

    public boolean isHidden() {
        return mHidden;
    }

    public void setHidden(boolean hidden) {
        mHidden = hidden;
    }

    public boolean isQuick() {
        return mQuick;
    }

    public int getQuickOrder() {
        return mQuickOrder;
    }

    public static AppModel from(Cursor cursor) {
        String pkg = cursor.getString(cursor.getColumnIndexOrThrow(
                AppsDatabaseContract.AppEntry.COLUMN_NAME_PACKAGE));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(
                AppsDatabaseContract.AppEntry.COLUMN_NAME_TITLE));
        byte[] icon = cursor.getBlob(cursor.getColumnIndexOrThrow(
                AppsDatabaseContract.AppEntry.COLUMN_NAME_ICON));
        int hiddenInt = cursor.getInt(cursor.getColumnIndexOrThrow(
                AppsDatabaseContract.AppEntry.COLUMN_NAME_HIDDEN));
        boolean hidden = hiddenInt == 1;
        int quickInt = cursor.getInt(cursor.getColumnIndexOrThrow(
                AppsDatabaseContract.AppEntry.COLUMN_NAME_QUICK));
        boolean quick = quickInt == 1;
        int quickOrder = cursor.getInt(cursor.getColumnIndexOrThrow(
                AppsDatabaseContract.AppEntry.COLUMN_NAME_QUICK_ORDER));
        return new AppModel(pkg, title, icon, hidden, quick, quickOrder);
    }
}
