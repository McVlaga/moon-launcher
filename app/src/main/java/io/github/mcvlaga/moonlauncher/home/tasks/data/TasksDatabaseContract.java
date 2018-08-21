package io.github.mcvlaga.moonlauncher.home.tasks.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class TasksDatabaseContract {

    public static final String CONTENT_AUTHORITY = "io.github.mcvlaga.moonlauncher.tasks";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_TASKS = "tasks";

    public static final class TaskEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_TASKS)
                .build();

        public static final String TABLE_NAME_TASKS = "tasks";
        public static final String COLUMN_NAME_ENTRY_ID = "entryid";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_TIME = "time";
        public static final String COLUMN_NAME_COLOR = "color";
        public static final String COLUMN_NAME_COMPLETED = "completed";

        public static Uri buildTasksUriWith(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getSqlSelectForHome() {
            return "(" +
                    "(" + COLUMN_NAME_TIME + " + " + COLUMN_NAME_DATE + ")" +
                    " >= " + System.currentTimeMillis() +
                    " or " + COLUMN_NAME_DATE + " = " + 0 +") " +
                    " and " + COLUMN_NAME_COMPLETED + " = " + 0;
        }

        public static String getSqlSelectForActiveTasks() {
            return "(" +
                    "(" + COLUMN_NAME_TIME + " + " + COLUMN_NAME_DATE + ")" +
                    " >= " + System.currentTimeMillis() +
                    " or " + COLUMN_NAME_DATE + " = " + 0 +") " +
                    " and " + COLUMN_NAME_COMPLETED + " = " + 0;
        }

        public static String getSqlSelectForCompletedTasks() {
            return COLUMN_NAME_COMPLETED + " = " + 1;
        }

        public static String getSqlSelectForAllTasks() {
            return "(" + COLUMN_NAME_TIME + " + " + COLUMN_NAME_DATE + ")" +
                    " >= " + System.currentTimeMillis() +
                    " or " + COLUMN_NAME_DATE + " = " + 0;
        }
    }
}