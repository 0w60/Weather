package com.evgsoft.weather;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import java.net.URL;
import java.util.ArrayList;

/*
JSON request: http://api.openweathermap.org/data/2.5/forecast/daily?q=Kiev&mode=json&units=metric&cnt=7
XML request: http://api.openweathermap.org/data/2.5/forecast/daily?q=London&mode=xml&units=metric&cnt=7
*/


public class WeatherDbProvider extends ContentProvider {

    static final Uri CONTENT_URI = Uri.parse(
            "content://com.evgsoft.weather.WeatherDbProvider/weathertable");
    private static final String TAG = "WeatherDbProvider";
    protected static SQLiteDatabase database;
    protected static long  totalRowsNumberInDB = 0;
    static URL webServiceUrl;
    static ArrayList<Weather> weatherList;

    public WeatherDbProvider() {
    }

    @Override
    public boolean onCreate() {
        database = new WeatherDbHelper(getContext()).getWritableDatabase();
        boolean isDbNull = (database == null);
        Log.i(TAG, "onCreate(): isDbNull: " + isDbNull);
        return (database != null);
    }

    /*private void getDataFromServerToWeatherList() {
        try {
            String city = MainActivity.city;
            int numberOfDays = MainActivity.daysNumber;

            Log.i(TAG, "getDataFromServerToWeatherList(): city: " + city + " numberOfDays: " + numberOfDays);

            webServiceUrl = new URL(
                    "http://api.openweathermap.org/data/2.5/forecast/daily?q=" + city + "&mode=json&units=metric&cnt=" + numberOfDays);

            Log.i(TAG, "getDataFromServerToWeatherList(): webServiceUrl: " + webServiceUrl);

            String jsonString = connectToServiceAndGetJsonString(webServiceUrl);

            Log.i(TAG, "getDataFromServerToWeatherList(): jsonString: " + jsonString);

            weatherList = jsonStringDeserialize(jsonString);

            Log.i(TAG, "getDataFromServerToWeatherList(): weatherList: " + weatherList.toString());
        } catch (MalformedURLException e) {
            Log.w(TAG, "URL is incorrect", e);
        }
    }*/

    /*private String connectToServiceAndGetJsonString(URL webServiceUrl) {
        Log.i(TAG, "in connectToServiceAndGetJsonString(URL webServiceUrl)");

        String jsonString = null;
        BufferedReader inStrmReader = null;
        HttpURLConnection connection = null;
        StringBuilder jsnStrngBldr = null;
        try {
            connection = (HttpURLConnection) webServiceUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            Boolean isConnectionNull = (connection == null);
            Log.i(TAG, "isConnectionNull: " + isConnectionNull);

            inStrmReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            jsnStrngBldr = new StringBuilder();
            String line;

            while ((line = inStrmReader.readLine()) != null) {
                jsnStrngBldr.append(line + '\n');
            }

            jsonString = jsnStrngBldr.toString();

            Log.i(TAG, " before catch: jsonString: " + jsonString);

        } catch (IOException e) {
            Log.w(TAG, "Error while receiving data from server", e);
        } finally {
            try {
                if (inStrmReader != null) {
                    inStrmReader.close();
                }
                connection.disconnect();
            } catch (IOException e) {
                Log.w(TAG, "Error while closing reader or connection", e);
            }
        }
        Log.i(TAG, " jsonString: " + jsonString);

        return jsonString;
    }

    private ArrayList<Weather> jsonStringDeserialize(String jsonString) {
        Log.i(TAG, "in jsonStringDeserialize(String jsonString)");
        GsonBuilder gsnBldr = new GsonBuilder();
        gsnBldr.registerTypeAdapter(Weather[].class, new WeatherDeserializer());
        Gson gson = gsnBldr.create();
        Weather[] array = gson.fromJson(jsonString, Weather[].class);
        weatherList = new ArrayList<Weather>(Arrays.asList(array));
        return weatherList;
    }

*/


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
        long lastRowId = database.insert(WeatherDbHelper.TABLE_NAME, WeatherDbHelper.CITY_COLUMN, values);
        Uri lastAddedItem = ContentUris.withAppendedId(CONTENT_URI, lastRowId);
        return lastAddedItem;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Log.i(TAG, "in query(Uri uri, String[] projection, String selection,\n" +
                "String[] selectionArgs, String sortOrder)");
//        sortOrder = (sortOrder == null) ? WeatherDbHelper.DAY_COLUMN : sortOrder;
        Cursor cursor = database.query(WeatherDbHelper.TABLE_NAME, projection, selection,
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

            /*//TEST
            ContentValues values = new ContentValues();
            //Preparing the row for inserting into the table
            values.put(CITY_COLUMN, "London");
            values.put(DAY_COLUMN, "Monday");
            values.put(WEATHER_CONDITION_COLUMN, "sunny");
            values.put(TEMPERATURE_COLUMN, "+22");
            //inserting the row into the table
            db.insert(TABLE_NAME, CITY_COLUMN, values);*/
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exists " + TABLE_NAME);
            onCreate(db);
        }
    }
}
