package com.evgsoft.weather;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;


public class GetWeatherTask extends AsyncTask<URL, Void, Cursor> {

    private static final String TAG = "GetWeatherTask";
    static final String[] FROM_COLUMNS = {
            WeatherDbProvider.WeatherDbHelper._ID,
            WeatherDbProvider.WeatherDbHelper.CITY_COLUMN,
            WeatherDbProvider.WeatherDbHelper.DAY_COLUMN,
            WeatherDbProvider.WeatherDbHelper.WEATHER_CONDITION_COLUMN,
            WeatherDbProvider.WeatherDbHelper.TEMPERATURE_COLUMN};

    URL webServiceUrl;
    String jsonString;
    ArrayList<Weather> weatherList;

    @Override
    protected Cursor doInBackground(URL... url) {
        webServiceUrl = url[0];
        Log.i(TAG, "doInBackground, webServiceUrl: " + webServiceUrl);
        jsonString = connectToServiceAndGetJsonString(webServiceUrl);
        weatherList = jsonStringDeserialize();
        Log.i(TAG, "weatherList: " + weatherList);
        setContentValuesAndPopulateDB(weatherList);
        Cursor cursor = WeatherDbProvider.database.query(
                WeatherDbProvider.WeatherDbHelper.TABLE_NAME, FROM_COLUMNS,
                null, null, null, null, null);
        return cursor;
    }

    private String connectToServiceAndGetJsonString(URL webServiceUrl) {
        BufferedReader inStrmReader = null;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) webServiceUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            Boolean isConnectionNull = (connection == null);
            Log.i(TAG, "isConnectionNull: " + isConnectionNull);

            inStrmReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder fromJsnStrngBldr = new StringBuilder();
            String line;
            while ((line = inStrmReader.readLine()) != null) {
                fromJsnStrngBldr.append(line);
            }
            jsonString = fromJsnStrngBldr.toString();
            Log.i(TAG, "jsonString: " + jsonString);

        } catch (IOException e) {
            Log.w(TAG, "Error while receiving data from server", e);
        } finally {
            try {
                if (inStrmReader != null) {
                    inStrmReader.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (IOException e) {
                Log.w(TAG, "Error while closing reader or connection", e);
            }
        }
        Log.i(TAG, " jsonString: " + jsonString);
        return jsonString;
    }

    private ArrayList<Weather> jsonStringDeserialize() {
        GsonBuilder gsnBldr = new GsonBuilder();
        gsnBldr.registerTypeAdapter(Weather[].class, new WeatherDeserializer());
        Gson gson = gsnBldr.create();
        Weather[] array = gson.fromJson(jsonString, Weather[].class);
        weatherList = new ArrayList<>(Arrays.asList(array));
        return weatherList;
    }

    private static void setContentValuesAndPopulateDB(ArrayList<Weather> list) {
        ContentValues values = new ContentValues();
        for (Weather w : list) {
            values.put(WeatherDbProvider.WeatherDbHelper.CITY_COLUMN, w.city);
            values.put(WeatherDbProvider.WeatherDbHelper.DAY_COLUMN, w.day);
            values.put(WeatherDbProvider.WeatherDbHelper.WEATHER_CONDITION_COLUMN, w.weathrCondtns);
            values.put(WeatherDbProvider.WeatherDbHelper.TEMPERATURE_COLUMN, w.temperature);

            long rowId = WeatherDbProvider.database.insert(
                    WeatherDbProvider.WeatherDbHelper.TABLE_NAME,
                    WeatherDbProvider.WeatherDbHelper.CITY_COLUMN,
                    values);
        }
    }

    @Override
    protected void onPostExecute(Cursor cursor) {
        Forecast.refreshView(cursor);
    }
}