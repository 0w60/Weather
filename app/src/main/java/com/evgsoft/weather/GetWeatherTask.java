package com.evgsoft.weather;

import android.content.ContentValues;
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


public class GetWeatherTask extends AsyncTask<URL, Void, Void> {

    private static final String TAG = "GetWeatherTask";
    static String[] fromColumns = {
            WeatherDbProvider.WeatherDbHelper.CITY_COLUMN,
            WeatherDbProvider.WeatherDbHelper.DAY_COLUMN,
            WeatherDbProvider.WeatherDbHelper.WEATHER_CONDITION_COLUMN,
            WeatherDbProvider.WeatherDbHelper.TEMPERATURE_COLUMN};

    URL webServiceUrl;
    String jsonString;
    ArrayList<Weather> weatherList;

    @Override
    protected Void doInBackground(URL... url) {

        webServiceUrl = url[0];

        Log.i(TAG, "doInBackground, webServiceUrl: " + webServiceUrl);

        jsonString = connectToServiceAndGetJsonString(webServiceUrl);


        weatherList = jsonStringDeserialize();

        Log.i(TAG, "weatherList: " + weatherList.toString());

        setContentValuesAndPopulateDB(weatherList);
//        populateDatabase();


        return null;
    }

    private String connectToServiceAndGetJsonString(URL webServiceUrl) {
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
            Log.i(TAG, "jsonString: " + jsonString);

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

    private ArrayList<Weather> jsonStringDeserialize() {
        GsonBuilder gsnBldr = new GsonBuilder();
        gsnBldr.registerTypeAdapter(Weather[].class, new WeatherDeserializer());
        Gson gson = gsnBldr.create();
        Weather[] array = gson.fromJson(jsonString, Weather[].class);
        weatherList = new ArrayList<Weather>(Arrays.asList(array));
        return weatherList;
    }
/*
    protected void populateDatabase() {
        Log.i(TAG, "in populateDatabase()");
        long rowId = WeatherDbProvider.database.insert(
                WeatherDbProvider.WeatherDbHelper.TABLE_NAME,
                WeatherDbProvider.WeatherDbHelper.CITY_COLUMN,
                setContentValuesAndPopulateDB(weatherList));
        Log.i(TAG, "populateDatabase(), rowId=" + rowId);
    }*/

    private ContentValues setContentValuesAndPopulateDB(ArrayList<Weather> list) {
        Log.i(TAG, "in setContentValuesAndPopulateDB(ArrayList<Weather> list)");

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
            Log.i(TAG, "setContentValuesAndPopulateDB, rowId=" + rowId);
        }
        return values;
    }
/*
    @Override
    protected void onPostExecute(Void v) {
        Forecast.showAllCursor = WeatherDbProvider.database.query(
                WeatherDbProvider.WeatherDbHelper.TABLE_NAME, fromColumns,
                null, null, null, null, null);
        Forecast.adapter.notifyDataSetChanged();
        Forecast.showTableListView.invalidateViews();
        Forecast.showTableListView.refreshDrawableState();
    }*/
}