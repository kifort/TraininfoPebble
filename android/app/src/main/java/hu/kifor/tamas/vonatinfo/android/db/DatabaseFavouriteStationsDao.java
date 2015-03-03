package hu.kifor.tamas.vonatinfo.android.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import hu.kifor.tamas.vonatinfo.FavouriteStationsDao;

/**
 * Created by tamas on 15. 02. 16..
 */
public class DatabaseFavouriteStationsDao implements FavouriteStationsDao {
    private static final String LOG_TAG = "DatabaseFavouriteStationsDao";

    private AndroidStorage androidStorage;

    public DatabaseFavouriteStationsDao(Context context) {
        androidStorage = new AndroidStorage(context);
        initDb();
    }

    @Override
    public List<String> getStations() {
        List<String> stations = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = androidStorage.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = sqLiteDatabase.rawQuery(String.format("select %s from %s order by %s asc",
                    AndroidStorage.NAME_FIELD,
                    AndroidStorage.FAVOURITE_STATIONS_TABLE_NAME,
                    AndroidStorage.ID_FIELD), null);
            while (cursor.moveToNext()) {
                String station = cursor.getString(0);
                if(station == null) {
                    station = "";
                } else {
                    station = station.trim();
                }
                stations.add(station);
            }
        } catch(SQLiteException sqLiteException) {
            Log.e(LOG_TAG, "Unable to query stations from the database", sqLiteException);
        } finally {
            if(cursor != null) {
                cursor.close();
            }
       }

        return stations;
    }

    @Override
    public void setStations(List<String> stations) {
        SQLiteDatabase sqLiteDatabase = androidStorage.getWritableDatabase();

        sqLiteDatabase.beginTransaction();
        for(int i=0; i<stations.size(); i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(AndroidStorage.NAME_FIELD, stations.get(i));
            String[] whereArgs = {""+i};
            sqLiteDatabase.update(
                    AndroidStorage.FAVOURITE_STATIONS_TABLE_NAME,
                    contentValues, AndroidStorage.ID_FIELD + "=" + i, null);
        }
        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();
    }

    private void initDb() {
        SQLiteDatabase sqLiteDatabase = androidStorage.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = sqLiteDatabase.rawQuery(String.format("select count(*) from %s",
                    AndroidStorage.FAVOURITE_STATIONS_TABLE_NAME), null);
            if (cursor.moveToNext()) {
                int numberOfStations = cursor.getInt(0);
                if(numberOfStations == 0) {
                    sqLiteDatabase.beginTransaction();
                    for(int i=0; i<5; i++) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(AndroidStorage.ID_FIELD, i);
                        contentValues.put(AndroidStorage.NAME_FIELD, "");
                        String[] whereArgs = {""+i};
                        sqLiteDatabase.insert(AndroidStorage.FAVOURITE_STATIONS_TABLE_NAME, null, contentValues);
                    }
                    sqLiteDatabase.setTransactionSuccessful();
                    sqLiteDatabase.endTransaction();
                }
            }
        } catch(SQLiteException sqLiteException) {
            Log.e(LOG_TAG, "Unable to query stations from the database", sqLiteException);
        } finally {
            if(cursor != null) {
                cursor.close();
            }
        }
    }
}
