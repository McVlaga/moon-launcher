package io.github.mcvlaga.moonlauncher.home.apps.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

public class AppsRepository {
    private ContentResolver mContentResolver;

    public AppsRepository(ContentResolver contentResolver) {
        mContentResolver = contentResolver;
    }

    public void addHiddenApp(String packageName) {
        ContentValues hiddenAppValues = new ContentValues();
        hiddenAppValues.put(AppsDatabaseContract.AppEntry.COLUMN_NAME_HIDDEN, 1);

        mContentResolver.update(AppsDatabaseContract.AppEntry.CONTENT_URI_APPS,
                hiddenAppValues,
                AppsDatabaseContract.AppEntry.COLUMN_NAME_PACKAGE + " = ?",
                new String[]{packageName});
    }

    public void removeHiddenApp(String packageName) {
        ContentValues hiddenAppValues = new ContentValues();
        hiddenAppValues.put(AppsDatabaseContract.AppEntry.COLUMN_NAME_HIDDEN, 0);

        mContentResolver.update(AppsDatabaseContract.AppEntry.CONTENT_URI_APPS,
                hiddenAppValues,
                AppsDatabaseContract.AppEntry.COLUMN_NAME_PACKAGE + " = ?",
                new String[]{packageName});
    }

    public int getNumberOfHiddenApps() {
        String selection = AppsDatabaseContract.AppEntry.COLUMN_NAME_HIDDEN + " = " + 1;
        Cursor cursor = mContentResolver.query(AppsDatabaseContract.AppEntry.CONTENT_URI_APPS,
                null, selection, null, null);
        if (cursor != null) {
            return cursor.getCount();
        } else {
            return 0;
        }
    }

    public void addQuickApp(String packageName) {
        ContentValues hiddenAppValues = new ContentValues();
        hiddenAppValues.put(AppsDatabaseContract.AppEntry.COLUMN_NAME_QUICK, 1);

        mContentResolver.update(AppsDatabaseContract.AppEntry.CONTENT_URI_APPS,
                hiddenAppValues,
                AppsDatabaseContract.AppEntry.COLUMN_NAME_PACKAGE + " = ?",
                new String[]{packageName});
    }

    public void removeQuickApp(String packageName) {
        ContentValues quickAppValues = new ContentValues();
        quickAppValues.put(AppsDatabaseContract.AppEntry.COLUMN_NAME_QUICK, 0);

        mContentResolver.update(AppsDatabaseContract.AppEntry.CONTENT_URI_APPS,
                quickAppValues,
                AppsDatabaseContract.AppEntry.COLUMN_NAME_PACKAGE + " = ?",
                new String[]{packageName});
    }
}
