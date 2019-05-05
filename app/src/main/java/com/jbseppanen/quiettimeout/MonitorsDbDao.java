package com.jbseppanen.quiettimeout;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.util.ArrayList;

public class MonitorsDbDao {
    private static SQLiteDatabase db;

    public static void initializeInstance(Context context) {
        if (db == null) {
            MonitorsDbHelper helper = new MonitorsDbHelper(context);
            db = helper.getWritableDatabase();
        }
    }

    public static Monitor readMonitor(String id) {
        if (db != null) {
            Cursor cursor = db.rawQuery(String.format("SELECT * FROM %s WHERE %s = '%s'",
                    MonitorDbContract.MonitorsEntry.TABLE_NAME,
                    MonitorDbContract.MonitorsEntry._ID,
                    id),
                    null);
            Monitor monitor;
            if (cursor.moveToNext()) {
                monitor = getMonitorFromCursor(cursor);
            } else {
                monitor = null;
            }
            cursor.close();
            return monitor;

        } else {
            return null;
        }
    }

    @NonNull
    private static Monitor getMonitorFromCursor(Cursor cursor) {
        int index;
        Monitor monitor;
        index = cursor.getColumnIndexOrThrow(MonitorDbContract.MonitorsEntry._ID);
        int id = cursor.getInt(index);
        index = cursor.getColumnIndexOrThrow(MonitorDbContract.MonitorsEntry.COLUMN_NAME_THRESHOLD);
        int threshold = cursor.getInt(index);
        index = cursor.getColumnIndexOrThrow(MonitorDbContract.MonitorsEntry.COLUMN_NAME_DURATION);
        int duration = cursor.getInt(index);
        monitor = new Monitor(id, threshold, duration);
        return monitor;
    }

    public static ArrayList<Monitor> readAllMonitors() {
        if (db != null) {
            Cursor cursor = db.rawQuery(String.format("SELECT * FROM %s;",
                    MonitorDbContract.MonitorsEntry.TABLE_NAME),
                    null);

            ArrayList<Monitor> monitorsList = new ArrayList<>();
            while (cursor.moveToNext()) {
                monitorsList.add(getMonitorFromCursor(cursor));
            }
            cursor.close();
            return monitorsList;

        } else {
            return new ArrayList<>();
        }
    }

    public static int createMonitor(Monitor monitor) {
        int resultId = Monitor.NO_ID;
        if (db != null) {
            ContentValues values = new ContentValues();
            values.put(MonitorDbContract.MonitorsEntry.COLUMN_NAME_DURATION, monitor.getDuration());
            values.put(MonitorDbContract.MonitorsEntry.COLUMN_NAME_THRESHOLD, monitor.getThreshold());

            resultId = (int) db.insert(MonitorDbContract.MonitorsEntry.TABLE_NAME, null, values);
        }
        return resultId;
    }

    public static void updateMonitor(Monitor monitor) {
        if (db != null) {
            String whereClause = String.format("%s = '%s'",
                    MonitorDbContract.MonitorsEntry._ID,
                    monitor.getId());

            final Cursor cursor = db.rawQuery(String.format("SELECT * FROM %s WHERE %s",
                    MonitorDbContract.MonitorsEntry.TABLE_NAME,
                    whereClause),
                    null);

            if (cursor.getCount() == 1) {
                ContentValues values = new ContentValues();
                values.put(MonitorDbContract.MonitorsEntry.COLUMN_NAME_DURATION, monitor.getDuration());
                values.put(MonitorDbContract.MonitorsEntry.COLUMN_NAME_THRESHOLD, monitor.getThreshold());
                final int affectedRows = db.update(MonitorDbContract.MonitorsEntry.TABLE_NAME, values, whereClause, null);
            }
        }
    }

    public static void deleteMonitor(Monitor monitor) {
        if (db != null) {
            String whereClause = String.format("%s = '%s'",
                    MonitorDbContract.MonitorsEntry._ID,
                    String.valueOf(monitor.getId()));

            int affectedRows = db.delete(MonitorDbContract.MonitorsEntry.TABLE_NAME, whereClause, null);
        }
    }
}
