package cn.weather;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
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
    ContentResolver contentResolver;

    public GetWeatherTask(Context context) {
        contentResolver = context.getContentResolver();
    }

    @Override
    protected Cursor doInBackground(URL... url) {
        URL webServiceUrl = url[0];
        Log.i(TAG, "webServiceUrl: " + webServiceUrl);

        String jsonString = connectToServiceAndGetJsonString(webServiceUrl);
        ArrayList<Weather> weatherList = jsonStringDeserialize(jsonString);
        setContentValuesAndPopulateDB(weatherList);

        Cursor cursor = contentResolver.query(WeatherDbProvider.CONTENT_URI, FROM_COLUMNS, null, null, null);
        return cursor;
    }

    String connectToServiceAndGetJsonString(URL webServiceUrl) {
        String jsonString = null;
        BufferedReader inStrmReader = null;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) webServiceUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            inStrmReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder fromJsnStrngBldr = new StringBuilder();
            String line;
            while ((line = inStrmReader.readLine()) != null) {
                fromJsnStrngBldr.append(line);
            }

            jsonString = fromJsnStrngBldr.toString();

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

    ArrayList<Weather> jsonStringDeserialize(String jsonString) {
        GsonBuilder gsnBldr = new GsonBuilder();
        gsnBldr.registerTypeAdapter(Weather[].class, new WeatherDeserializer());
        Gson gson = gsnBldr.create();
        Weather[] array = gson.fromJson(jsonString, Weather[].class);
        return new ArrayList<>(Arrays.asList(array));
    }

    void setContentValuesAndPopulateDB(ArrayList<Weather> list) {
        ContentValues values = new ContentValues();
        for (Weather w : list) {
            values.put(WeatherDbProvider.WeatherDbHelper.CITY_COLUMN, w.city);
            values.put(WeatherDbProvider.WeatherDbHelper.DAY_COLUMN, w.day);
            values.put(WeatherDbProvider.WeatherDbHelper.WEATHER_CONDITION_COLUMN, w.weathrCondtns);
            values.put(WeatherDbProvider.WeatherDbHelper.TEMPERATURE_COLUMN, w.temperature);

            contentResolver.insert(WeatherDbProvider.CONTENT_URI, values);
        }
    }

    @Override
    protected void onPostExecute(Cursor cursor) {
        Forecast.refreshView(cursor);
    }
}