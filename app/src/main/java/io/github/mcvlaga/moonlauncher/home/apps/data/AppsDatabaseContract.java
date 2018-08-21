package io.github.mcvlaga.moonlauncher.home.apps.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class AppsDatabaseContract {

    public static final String CONTENT_AUTHORITY = "io.github.mcvlaga.moonlauncher.apps";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_APPS = "apps";
    public static final String PATH_APPS_HIDDEN = "hidden";
    public static final String PATH_APPS_QUICK = "quick";

    public static final class AppEntry implements BaseColumns {

        public static final Uri CONTENT_URI_APPS = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_APPS)
                .build();

        public static final Uri CONTENT_URI_APPS_HIDDEN = CONTENT_URI_APPS.buildUpon()
                .appendPath(PATH_APPS_HIDDEN)
                .build();

        public static final Uri CONTENT_URI_APPS_QUICK = CONTENT_URI_APPS.buildUpon()
                .appendPath(PATH_APPS_QUICK)
                .build();

        public static final String TABLE_NAME_APPS = "apps";
        public static final String COLUMN_NAME_PACKAGE = "package";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_ICON = "icon";
        public static final String COLUMN_NAME_HIDDEN = "hidden";
        public static final String COLUMN_NAME_QUICK = "quick";
        public static final String COLUMN_NAME_QUICK_ORDER = "quick_order";

        public static Uri buildAppsUriWith(long id) {
            return ContentUris.withAppendedId(CONTENT_URI_APPS, id);
        }

        public static String getSqlSelectForHiddenApps() {
            return AppEntry.COLUMN_NAME_HIDDEN + " = " + 0;
        }

        public static String getSqlSelectForQuickApps() {
            return AppEntry.COLUMN_NAME_QUICK + " = " + 1;
        }

        public static String getSqlOrderForQuickApps() {
            return AppEntry.COLUMN_NAME_QUICK_ORDER + " ASC";
        }

        public static String getSqlOrderForApps() {
            return AppEntry.COLUMN_NAME_TITLE + " COLLATE NOCASE ASC";
        }
    }
}
