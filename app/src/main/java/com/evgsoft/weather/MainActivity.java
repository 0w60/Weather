package com.evgsoft.weather;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
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

    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = "com.evgsoft.weather";
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "com.evgsoft.weather.dbsyncadapter";
    // The account name
    public static final String ACCOUNT = "dummyaccount";

    Account account;
    // Sync interval constants
    /*public static final long MILLISECONDS_PER_SECOND = 1000L;
    public static final long SECONDS_PER_MINUTE = 60L;
    public static final long SYNC_INTERVAL_IN_MINUTES = 60L;*/
    public static final long SYNC_INTERVAL_IN_SECONDS = 30L;

    ContentResolver mResolver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the dummy account
        account = CreateSyncAccount(this);

        boolean isMaccountNull = (account == null);
        Log.i(TAG, "-----isMaccountNull:" + isMaccountNull);

        mResolver = getContentResolver();

        ContentResolver.setIsSyncable(account, AUTHORITY, 1);
        ContentResolver.setSyncAutomatically(account, AUTHORITY, true);
        /*
         * Turn on periodic syncing
         */
        ContentResolver.addPeriodicSync(
                account,
                AUTHORITY,
                new Bundle(),
                SYNC_INTERVAL_IN_SECONDS);
    }

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

    /**
     * Create a new dummy account for the sync adapter
     *
     * @param context The application context
     */
    public static Account CreateSyncAccount(Context context) {
        Account newAccount = null;
        try {
            newAccount = new Account(
                    ACCOUNT, ACCOUNT_TYPE);
            AccountManager accountManager =
                    (AccountManager) context.getSystemService(
                            ACCOUNT_SERVICE);
            accountManager.addAccountExplicitly(newAccount, null, null);
        } catch (Exception e) {
            Log.w(TAG, "Dummy account creation failed", e);
        }
        return newAccount;
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
