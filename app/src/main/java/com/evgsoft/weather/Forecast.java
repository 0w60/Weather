package com.evgsoft.weather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;


public class Forecast extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        /*
        String tableName = ContactsDBhelper.TABLE_NAME;
        SQLiteCursor showAllCursor = (SQLiteCursor) DBcreateActivity.db.query(
                tableName, null, null, null, null, null, null);

        String[] fromColumns = {"_id", ContactsDBhelper.NAME, ContactsDBhelper.PHONE};
        int[] toViews = {R.id._idTextView, R.id.nameTextView, R.id.phoneTextView};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.activity_views_for_list,
                showAllCursor, fromColumns, toViews, 0);
*/
        ListView showTableListView = (ListView) findViewById(R.id.forecastLstView);
//        showTableListView.setAdapter(adapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.forecast, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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
