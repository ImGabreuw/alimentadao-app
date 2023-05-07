package br.com.alimentadao.app.database;

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

    private static final String DATABASE_NAME = "alimentadao";
    private static final String TIMES_TABLE_NAME = "times";
    private static final int DATABASE_VERSION = 1;

    @Language("SQL")
    private static final String CREATE_TABLE_TIMES = "CREATE TABLE IF NOT EXISTS " + TIMES_TABLE_NAME + " (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "hour INTEGER, " +
            "minute INTEGER" +
            ");";

    @Language("SQL")
    private static final String DROP_TABLE_TIMES = "DROP TABLE IF EXISTS " + TIMES_TABLE_NAME;

    @Language("SQL")
    private static final String SELECT_ALL = "SELECT * FROM " + TIMES_TABLE_NAME;

    public TimeSQLiteRepository(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TIMES);
        Log.i("DataBase", "Table '" + TIMES_TABLE_NAME + "' created." );
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

            id = db.insert(TIMES_TABLE_NAME, null, newTime);
        } catch (SQLiteException e) {
            Log.e("DataBase", "Error when saving (" + time + "). ", e);
        }


        Log.i("DataBase", "Save (" + time + ") with id: " + id + ".");
    }

    public void deleteAll() {
        try (SQLiteDatabase db = getWritableDatabase()) {
            db.delete(TIMES_TABLE_NAME, null, null);
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
