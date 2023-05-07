package br.com.alimentadao.app.database;

import static android.provider.BaseColumns._ID;
import static br.com.alimentadao.app.database.TimeReaderContract.TimeEntry.COLUMN_NAME_HOUR;
import static br.com.alimentadao.app.database.TimeReaderContract.TimeEntry.COLUMN_NAME_MINUTE;
import static br.com.alimentadao.app.database.TimeReaderContract.TimeEntry.TABLE_NAME;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.intellij.lang.annotations.Language;

import java.util.ArrayList;
import java.util.List;

import br.com.alimentadao.app.time.TimeItem;

public class TimeSQLiteRepository extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "alimentadao.db";
    private static final int DATABASE_VERSION = 1;

    @Language("SQL")
    private static final String CREATE_TABLE_TIMES = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME_HOUR + " INTEGER, " +
            COLUMN_NAME_MINUTE + " INTEGER" +
            ");";

    @Language("SQL")
    private static final String DROP_TABLE_TIMES = "DROP TABLE IF EXISTS " + TABLE_NAME;

    @Language("SQL")
    private static final String SELECT_ALL = "SELECT * FROM " + TABLE_NAME;

    public TimeSQLiteRepository(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TIMES);
        Log.i("DataBase", "Table '" + TABLE_NAME + "' created." );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_TIMES);
        onCreate(db);
        Log.i("DataBase", "Upgrading database from " + oldVersion + " to " + newVersion + ".");
    }

    public void save(TimeItem time) {
        long id = -1;

        try (SQLiteDatabase db = getWritableDatabase()) {
            ContentValues newTime = new ContentValues();
            newTime.put("hour", time.getHour());
            newTime.put("minute", time.getMinute());

            id = db.insert(TABLE_NAME, null, newTime);
        } catch (SQLiteException e) {
            Log.e("DataBase", "Error when saving (" + time + "). ", e);
        }


        Log.i("DataBase", "Save (" + time + ") with id: " + id + ".");
    }

    public void deleteAll() {
        try (SQLiteDatabase db = getWritableDatabase()) {
            db.delete(TABLE_NAME, null, null);
        } catch (SQLiteException e) {
            Log.e("DataBase", "Error when deleting all times.", e);
        }

        Log.i("DataBase", "Delete all times.");
    }

    public List<TimeItem> findAll() {
        List<TimeItem> times = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(SELECT_ALL, null);

        if (cursor.moveToFirst()) {
            do {
                TimeItem time = new TimeItem();

                time.setId(cursor.getLong(0));
                time.setHour(cursor.getInt(1));
                time.setMinute(cursor.getInt(2));

                times.add(time);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        Log.i("DataBase", "Find " + times.size() + " times.");

        return times;
    }
}
