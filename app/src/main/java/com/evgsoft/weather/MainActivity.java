package com.evgsoft.weather;

import android.app.Activity;
import android.content.ContentResolver;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


public class MainActivity extends Activity implements View.OnClickListener {

    protected static String city;
    protected static int daysNumber;

    @Override
    public void onClick(View v) {
        EditText cityEdtTxt = (EditText) findViewById(R.id.cityEdtTxt);
        city = cityEdtTxt.getText().toString();

        EditText daysNumberEdtTxt = (EditText) findViewById(R.id.daysNumberEdtTxt);
        daysNumber = Integer.parseInt(daysNumberEdtTxt.getText().toString());
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
