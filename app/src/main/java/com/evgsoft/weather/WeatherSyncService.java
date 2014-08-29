package com.evgsoft.weather;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

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
        synchronized (dbSyncAdapterLock) {
            if (dbSyncAdapter == null) {
                dbSyncAdapter = new DbSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return dbSyncAdapter.getSyncAdapterBinder();
    }
}
