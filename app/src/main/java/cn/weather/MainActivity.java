package cn.weather;

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

    /*The authority for the sync adapter's content provider.
    Must be the same as in AndroidManifest.xml and in xml/syncadapter.xml. */
    public static final String AUTHORITY = "cn.weather.WeatherDbProvider";

    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "cn.weather.dbsyncadapter";

    // The account name
    public static final String ACCOUNT = "dummyaccount";

    Account account;
    // Sync interval
    public static final long SYNC_INTERVAL_IN_SECONDS = 30L;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the dummy account
        account = CreateSyncAccount(this);

        boolean isAccountNull = (account == null);
        Log.i(TAG, "isAccountNull: " + isAccountNull);

        ContentResolver.setIsSyncable(account, AUTHORITY, 1);
        ContentResolver.setSyncAutomatically(account, AUTHORITY, true);

        // Turn on periodic syncing
        ContentResolver.addPeriodicSync(
                account,
                AUTHORITY,
                new Bundle(),
                SYNC_INTERVAL_IN_SECONDS);
    }

    @Override
    public void onClick(View v) {
        EditText cityEdtTxt = (EditText) findViewById(R.id.cityEdtTxt);
        String city = cityEdtTxt.getText().toString();

        EditText daysNumberEdtTxt = (EditText) findViewById(R.id.daysNumberEdtTxt);
        int daysNumber = Integer.parseInt(daysNumberEdtTxt.getText().toString());

        URL webServiceUrl = null;
        try {
            webServiceUrl = new URL(
                    "http://api.openweathermap.org/data/2.5/forecast/daily?q=" + city + "&mode=json&units=metric&cnt=" + daysNumber);
        } catch (MalformedURLException e) {
            Log.e(TAG, "Bad URL", e);
        }
        new GetWeatherTask(this).execute(webServiceUrl);

        Intent intent = new Intent(this, Forecast.class);
        startActivity(intent);
    }

    /**
     * Create a new dummy account for the sync adapter
     */
    static Account CreateSyncAccount(Context context) {
        Account newAccount = null;
        try {
            newAccount = new Account(
                    ACCOUNT, ACCOUNT_TYPE);
            AccountManager accountManager =
                    (AccountManager) context.getSystemService(
                            ACCOUNT_SERVICE);
            accountManager.addAccountExplicitly(newAccount, null, null);
        } catch (RuntimeException e) {
            Log.e(TAG, "Dummy account creation failed", e);
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
