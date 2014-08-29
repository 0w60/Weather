package com.evgsoft.weather;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class WeatherSyncService extends Service {

    private static final String TAG = "WeatherSyncServic";
    private static DbSyncAdapter dbSyncAdapter;

    // Object to use as a thread-safe lock
    private static final Object dbSyncAdapterLock = new Object();

    public WeatherSyncService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "-----In the onCreate()");

        synchronized (dbSyncAdapterLock) {
            if (dbSyncAdapter == null) {
                dbSyncAdapter = new DbSyncAdapter(getApplicationContext(), true);

                boolean isDbSyncAdapterNull = (dbSyncAdapter == null);
                Log.i(TAG, "-----isDbSyncAdapterNull: " + isDbSyncAdapterNull);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return dbSyncAdapter.getSyncAdapterBinder();
    }
}
