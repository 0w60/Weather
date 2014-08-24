package com.evgsoft.weather;

import android.content.ContentQueryMap;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by artem on 8/24/14.
 */
public class GetWeatherTask extends AsyncTask<URL, Void, String> {

    private static final String TAG = "GetWeatherTask";

    URL webServiceUrl;
    String jsonString;

    public GetWeatherTask(URL webServiceUrl) {
        this.webServiceUrl = webServiceUrl;
    }


    @Override
    protected String doInBackground(URL... url) {
        connectToServiceAndGetJsonString(webServiceUrl);

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
}
