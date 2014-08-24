package com.evgsoft.weather;

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
import java.util.List;


public class GetWeatherTask extends AsyncTask<URL, Void, ArrayList<Weather>> {

    private static final String TAG = "GetWeatherTask";

    URL webServiceUrl;
    String jsonString;
    ArrayList<Weather> weatherList;

    public GetWeatherTask(URL webServiceUrl) {
        this.webServiceUrl = webServiceUrl;
    }

    @Override
    protected ArrayList<Weather> doInBackground(URL... url) {
        connectToServiceAndGetJsonString(webServiceUrl);
        jsonStringDeserialize();

        Log.i(TAG, "weatherList: " + weatherList.toString());

        return weatherList;
    }

    private String connectToServiceAndGetJsonString(URL webServiceUrl) {
        BufferedReader inStrmReader = null;
        HttpURLConnection connection = null;
        StringBuilder jsnStrngBldr = null;
        try {
            connection = (HttpURLConnection) webServiceUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            inStrmReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            jsnStrngBldr = new StringBuilder();
            String line;

            while ((line = inStrmReader.readLine()) != null) {
                jsnStrngBldr.append(line + '\n');
            }

            jsonString = jsnStrngBldr.toString();

        } catch (IOException e) {
            Log.w(TAG, "Error while receiving data from server", e);
        } finally {
            try {
                inStrmReader.close();
                connection.disconnect();
            } catch (IOException e) {
                Log.w(TAG, "Error while closing reader or connection", e);
            }
        }
        Log.i(TAG, " jsonString: " + jsonString);

        return jsonString;
    }

    private List<Weather> jsonStringDeserialize() {
        GsonBuilder gsnBldr = new GsonBuilder();
        gsnBldr.registerTypeAdapter(Weather[].class, new WeatherDeserializer());
        Gson gson = gsnBldr.create();
        Weather[] array = gson.fromJson(jsonString, Weather[].class);
        weatherList = new ArrayList<Weather>(Arrays.asList(array));
        return weatherList;
    }
}
