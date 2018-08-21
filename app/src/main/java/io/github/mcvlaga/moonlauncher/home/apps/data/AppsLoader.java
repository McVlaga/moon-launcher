package io.github.mcvlaga.moonlauncher.home.apps.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import io.github.mcvlaga.moonlauncher.BuildConfig;

public class AppsLoader extends AsyncTask<Void, Void, Void> {

    private final PackageManager mPm;
    private final ContentResolver mContentResolver;

    private static final String appPackageName = BuildConfig.APPLICATION_ID;

    public AppsLoader(Context context) {
        mPm = context.getPackageManager();
        mContentResolver = context.getContentResolver();
    }

    @Override
    protected Void doInBackground(Void... voids) {

        List<ApplicationInfo> apps = mPm.getInstalledApplications(0);

        // Delete apps that are not installed from the database
        deleteNonExistentApps(apps);

        ArrayList<ContentValues> appsValues = new ArrayList<>();
        if (apps == null) {
            apps = new ArrayList<>();
        }

        for (int i = 0; i < apps.size(); i++) {
            ApplicationInfo appInfo = apps.get(i);
            String pkg = appInfo.packageName;

            if (mPm.getLaunchIntentForPackage(pkg) != null
                    && !pkg.equals(appPackageName)) {

                CharSequence label = appInfo.loadLabel(mPm);
                String appTitle = label != null ? label.toString() : pkg;

                byte[] iconBlob = getByteArrayFromDrawable(appInfo.loadIcon(mPm));

                ContentValues appValues = new ContentValues();
                appValues.put(AppsDatabaseContract.AppEntry.COLUMN_NAME_PACKAGE, pkg);
                appValues.put(AppsDatabaseContract.AppEntry.COLUMN_NAME_TITLE, appTitle);
                appValues.put(AppsDatabaseContract.AppEntry.COLUMN_NAME_ICON, iconBlob);
                appValues.put(AppsDatabaseContract.AppEntry.COLUMN_NAME_HIDDEN, 0);
                appValues.put(AppsDatabaseContract.AppEntry.COLUMN_NAME_QUICK, 0);
                appsValues.add(appValues);
            }
        }

        mContentResolver.bulkInsert(AppsDatabaseContract.AppEntry.CONTENT_URI_APPS,
                appsValues.toArray(new ContentValues[appsValues.size()]));
        return null;
    }

    private void deleteNonExistentApps(List<ApplicationInfo> apps) {
        Cursor currentAppsCursor = mContentResolver.query(
                AppsDatabaseContract.AppEntry.CONTENT_URI_APPS, null, null, null, null);

        if (currentAppsCursor != null) {
            while (currentAppsCursor.moveToNext()) {
                String packageName = currentAppsCursor.getString(currentAppsCursor.getColumnIndexOrThrow(
                        AppsDatabaseContract.AppEntry.COLUMN_NAME_PACKAGE));
                boolean exists = false;
                for (ApplicationInfo appInfo : apps) {
                    if (appInfo.packageName.equals(packageName)) {
                        exists = true;
                    }
                }

                if (!exists) {
                    mContentResolver.delete(AppsDatabaseContract.AppEntry.CONTENT_URI_APPS,
                            AppsDatabaseContract.AppEntry.COLUMN_NAME_PACKAGE + " = ?",
                            new String[]{packageName});
                }
            }
            currentAppsCursor.close();
        }
    }

    private byte[] getByteArrayFromDrawable(Drawable icon) {
        final Bitmap bmp = Bitmap.createBitmap(icon.getIntrinsicWidth(), icon.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bmp);
        icon.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        icon.draw(canvas);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();

    }
}
