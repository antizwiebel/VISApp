package vis.ooe.fh.at.visapp;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Mark on 18.01.2017.
 */
public class BluetoothService extends Service {
    private static final String BLESERVICE_TAG = "BLEService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i (BLESERVICE_TAG, "creating service...");

        Notification notification = new Notification.Builder(this).
                setContentTitle("Bluetooth Service").
                setSmallIcon(android.R.drawable.stat_sys_data_bluetooth).build();
        startForeground(1337, notification);
    }
}
