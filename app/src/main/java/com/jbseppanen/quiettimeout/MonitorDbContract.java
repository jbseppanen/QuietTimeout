package com.jbseppanen.quiettimeout;

import android.provider.BaseColumns;

public class MonitorDbContract {
    public static class MonitorsEntry implements BaseColumns {
        public static final String TABLE_NAME = "monitors";
        public static final String COLUMN_NAME_THRESHOLD = "threshold";
        public static final String COLUMN_NAME_DURATION = "duration";

        public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
                " ( " +
                _ID + " INTEGER PRIMARY KEY, " +
                COLUMN_NAME_THRESHOLD + " INTEGER, " +
                COLUMN_NAME_DURATION + " INTEGER);";

        public static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
    }
}
