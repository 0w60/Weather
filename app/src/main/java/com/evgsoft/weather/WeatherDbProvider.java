package com.evgsoft.weather;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;

/*
JSON request: http://api.openweathermap.org/data/2.5/forecast/daily?q=Kiev&mode=json&units=metric&cnt=7
XML request: http://api.openweathermap.org/data/2.5/forecast/daily?q=London&mode=xml&units=metric&cnt=7
*/



public class WeatherDbProvider extends ContentProvider {

    private static final Uri CONTENT_URI = Uri.parse(
            "content://com.evgsoft.weather.weatherprovider/weathertable");
    private SQLiteDatabase database;

    public WeatherDbProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
        database = new WeatherDbHelper(getContext()).getWritableDatabase();
        return (database != null);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    protected class WeatherDbHelper extends SQLiteOpenHelper implements BaseColumns {

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

            //TEST
            ContentValues values = new ContentValues();
            //Preparing the row for inserting into the table
            values.put(CITY_COLUMN, "London");
            values.put(DAY_COLUMN, "Monday");
            values.put(WEATHER_CONDITION_COLUMN, "sunny");
            values.put(TEMPERATURE_COLUMN, "+22");
            //inserting the row into the table
            db.insert(TABLE_NAME, CITY_COLUMN, values);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exists " + TABLE_NAME);
            onCreate(db);
        }
    }
}
