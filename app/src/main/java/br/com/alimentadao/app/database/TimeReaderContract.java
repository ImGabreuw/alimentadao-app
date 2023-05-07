package br.com.alimentadao.app.database;

import android.provider.BaseColumns;

public final class TimeReaderContract {

    private TimeReaderContract() {
    }

    public static class TimeEntry implements BaseColumns {
        public static final String TABLE_NAME = "times";
        public static final String COLUMN_NAME_HOUR = "hour";
        public static final String COLUMN_NAME_MINUTE = "minute";
    }
}
