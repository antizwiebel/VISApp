package vis.ooe.fh.at.visapp;

import android.app.IntentService;
import android.app.Notification;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Mark on 18.01.2017.
 */
public class BluetoothService extends Service {
    private static final String BLESERVICE_TAG = "BLEService";

    public BluetoothLeScanner mBLEScanner;
    public ScanCallback mScanCallback;
    public ArrayList<ScanResult> mBLE_DeviceList;

    private Handler mHandler;
    private Messenger mServiceMessenger;
    private double mData;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent _intent, int _flags, int _startId) {
        final BluetoothManager bluetoothManager =  (BluetoothManager) (getSystemService(BLUETOOTH_SERVICE));
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

        mBLEScanner = bluetoothAdapter.getBluetoothLeScanner();
        mScanCallback = new ScanCallback(){

            @Override
            public void onScanResult(int _callbackType, ScanResult _result){
                String device = _result.getDevice().getName();
                device = (device == null || device.length() == 0) ?
                        "device has no name" : device;
                Log.i(MainActivity.TAG, "BLE_Service ScannCallback::onScanResult() -->" +
                        device);
                for(int i = 0; i<mBLE_DeviceList.size(); i++){
                    ScanResult entry = mBLE_DeviceList.get(i);
                    if(entry.getDevice().getAddress().equals(_result.getDevice().getAddress()))
                        return;
                } //for i
                mBLE_DeviceList.add(_result);
            }
        };
        startScan();

        if (mServiceMessenger == null) {
            mServiceMessenger =
                    (Messenger) _intent.getParcelableExtra(MainActivity.MESSENGER_KEY);
        }

        //TODO just for testing
        mData = Math.random()*50;

        Log.i(BLESERVICE_TAG, "Data result = " + mData);
        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.DATA_KEY, String.valueOf(mData));

        Message msg= Message.obtain();
        msg.what = MainActivity.MESSAGE_KEY;
        msg.setData(bundle);
        try {
            mServiceMessenger.send(msg);
        } catch (RemoteException _e) {
            Log.e(BLESERVICE_TAG, "error sending reply message...", _e);
        }

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    protected void startScan() {
        mBLE_DeviceList = new ArrayList<>();
        //Stops scanning after a pre-defined scan period.
        //TODO: hander fertigstellen
        /*mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopScan();
            }
        }, 2000);*/

        // ScanFilter.Builder filterFac = new ScanFilter.Builder();
        // filterFac = filterFac.setDeviceAddress("E5:::blablabla") //heart rate
        // Scan Filter filter = filterFac.build();
        //List <ScanFilter> filters = new ArrayList<ScanFilter();
        //filters.add(filter)

        ScanSettings.Builder settingsFac = new ScanSettings.Builder();
        settingsFac = settingsFac.setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES);
        settingsFac = settingsFac.setScanMode(ScanSettings.SCAN_MODE_BALANCED);
        ScanSettings settings = settingsFac.build();

        mBLEScanner.startScan(null, settings, mScanCallback);
        //TODO: Message activity to start progress indicator
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(MainActivity.TAG, "creating service...");

        Notification notification = new Notification.Builder(this).
                setContentTitle("Bluetooth Service").
                setSmallIcon(android.R.drawable.stat_sys_data_bluetooth).build();
        startForeground(1337, notification);



        mHandler = new Handler() {
            @Override
            public void handleMessage(Message _msg) {

            }


        };
    }
}