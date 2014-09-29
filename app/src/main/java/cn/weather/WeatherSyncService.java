package cn.weather;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class WeatherSyncService extends Service {

    private static final String TAG = "WeatherSyncService";
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

                Log.i(TAG, "onCreate()");
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return dbSyncAdapter.getSyncAdapterBinder();
    }
}
