package com.evgsoft.weather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    protected static String city;
    protected static int daysNumber;
    URL webServiceUrl;

    @Override
    public void onClick(View v) {
        EditText cityEdtTxt = (EditText) findViewById(R.id.cityEdtTxt);
        city = cityEdtTxt.getText().toString();

        EditText daysNumberEdtTxt = (EditText) findViewById(R.id.daysNumberEdtTxt);
        daysNumber = Integer.parseInt(daysNumberEdtTxt.getText().toString());

        try {
            webServiceUrl = new URL(
                    "http://api.openweathermap.org/data/2.5/forecast/daily?q=" + city + "&mode=json&units=metric&cnt=" + daysNumber);
        } catch (MalformedURLException e) {
            Log.w(TAG, "Bad URL", e);
        }
        new GetWeatherTask().execute(webServiceUrl);

        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), Forecast.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
