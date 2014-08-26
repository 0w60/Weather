package com.evgsoft.weather;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public class Forecast extends Activity implements View.OnClickListener {

    public static Cursor showAllCursor;
    public static ListView showTableListView;
    public static SimpleCursorAdapter adapter;
    public static final String[] FROM_COLUMNS = {
            WeatherDbProvider.WeatherDbHelper.CITY_COLUMN,
            WeatherDbProvider.WeatherDbHelper.DAY_COLUMN,
            WeatherDbProvider.WeatherDbHelper.WEATHER_CONDITION_COLUMN,
            WeatherDbProvider.WeatherDbHelper.TEMPERATURE_COLUMN};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        showForecast();
    }

    protected void showForecast() {
        ContentResolver contentResolver = getContentResolver();
        showAllCursor = contentResolver.query(WeatherDbProvider.CONTENT_URI,
                null, null, null, null, null);

        while (showAllCursor.getCount() == 0) {
            showAllCursor = getContentResolver().query(WeatherDbProvider.CONTENT_URI,
                    null, null, null, null, null);
        }


        /*if (showAllCursor.getCount() <= 0) {
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), Forecast.class);
            startActivity(intent);
        } */
//        showAllCursor.setNotificationUri(contentResolver, WeatherDbProvider.CONTENT_URI);

        int[] toViews = {R.id.cityTxtView, R.id.dayTxtView, R.id.condtnTxtView, R.id.tempTxtView};
        adapter = new SimpleCursorAdapter(this, R.layout.activity_views_for_lst_view,
                showAllCursor, FROM_COLUMNS, toViews, 0);
        showTableListView = (ListView) findViewById(R.id.forecastLstView);
        showTableListView.setAdapter(adapter);

    }

    public void refreshView(View v) {
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), Forecast.class);
        startActivity(intent);
       /* showAllCursor = getContentResolver().query(WeatherDbProvider.CONTENT_URI,
                null, null, null, null, null);
        adapter.notifyDataSetChanged();
        showTableListView.invalidateViews();
        showTableListView.refreshDrawableState();*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.forecast, menu);
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

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}
