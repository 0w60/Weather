package cn.weather;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

/*
JSON request: http://api.openweathermap.org/data/2.5/forecast/daily?q=Kiev&mode=json&units=metric&cnt=7
XML request: http://api.openweathermap.org/data/2.5/forecast/daily?q=London&mode=xml&units=metric&cnt=7
*/


public class WeatherDbProvider extends ContentProvider {

    static final Uri CONTENT_URI = Uri.parse(
            "content://cn.weather.WeatherDbProvider/weathertable");
    private static final String TAG = "WeatherDbProvider";
    protected static SQLiteDatabase database;

    public WeatherDbProvider() {
    }

    @Override
    public boolean onCreate() {
        database = new WeatherDbHelper(getContext()).getWritableDatabase();
        boolean isDbNull = (database == null);
        Log.i(TAG, "onCreate(): isDbNull: " + isDbNull);
        return (database != null);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int numberOfDeletedRows = database.delete(WeatherDbHelper.TABLE_NAME, selection, selectionArgs);
        return numberOfDeletedRows;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long lastRowId = database.insert(WeatherDbHelper.TABLE_NAME, WeatherDbHelper.CITY_COLUMN, values);
        Uri lastAddedItem = ContentUris.withAppendedId(CONTENT_URI, lastRowId);
        return lastAddedItem;
    }

    @Override
    public SQLiteCursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteCursor cursor = (SQLiteCursor) database.query(WeatherDbHelper.TABLE_NAME, projection, selection,
                selectionArgs, null, null, sortOrder);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    protected static class WeatherDbHelper extends SQLiteOpenHelper implements BaseColumns {
        static final String BASE_NAME = "weatherBase.db";
        static final String TABLE_NAME = "weatherTable";
        static final String ID_COLUMN = "_id";
        static final String CITY_COLUMN = "city";
        static final String DAY_COLUMN = "day";
        static final String WEATHER_CONDITION_COLUMN = "weatherCondition";
        static final String TEMPERATURE_COLUMN = "temperature";

        public WeatherDbHelper(Context context) {
            super(context, BASE_NAME, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table " + TABLE_NAME + " (" + ID_COLUMN + " integer primary key autoincrement, " +
                    CITY_COLUMN + " text, " + DAY_COLUMN + " text, " + WEATHER_CONDITION_COLUMN + " text, " +
                    TEMPERATURE_COLUMN + " text);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exists " + TABLE_NAME);
            onCreate(db);
        }
    }
}
