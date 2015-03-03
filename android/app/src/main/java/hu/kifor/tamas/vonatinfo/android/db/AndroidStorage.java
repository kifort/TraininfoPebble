package hu.kifor.tamas.vonatinfo.android.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tamas on 15. 02. 16..
 */
public class AndroidStorage extends SQLiteOpenHelper {
    private static final String LOG_TAG = "AndroidStorage";
    public static final String DATABASE_NAME = "traininfo";
    private static final int DATABASE_VERSION = 1;
    public static final String FAVOURITE_STATIONS_TABLE_NAME = "favourite_stations";
    public static final String ID_FIELD = "id";
    public static final String NAME_FIELD = "name";

    private static final String FAVOURITE_STATIONS_TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + FAVOURITE_STATIONS_TABLE_NAME + " (" +
                    ID_FIELD + " INTEGER PRIMARY KEY NOT NULL, " +
                    NAME_FIELD + " TEXT NOT NULL);";

    public AndroidStorage(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(FAVOURITE_STATIONS_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new UnsupportedOperationException();
        //db.execSQL("DROP TABLE " + DATABASE_NAME + ";");
        //onCreate(db);
    }

}
