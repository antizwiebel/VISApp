package vis.ooe.fh.at.visapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int LOC_ENABLE_REQUEST = 1;
    private static final int BT_ENABLE_REQUEST = 2;
    private boolean mServiceStarted=false;

    public static final String TAG ="MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ListView mDeviceListView =(ListView) findViewById(R.id.listViewBLEDevices);
        final TextView mDeviceDataTextView= (TextView) findViewById(R.id.textViewData);

        String[] values = new String[] {"Device 1", "Device 2", "Device 3","Device 4","Device 5"};

        final ArrayList<String> deviceList= new ArrayList<String>();

        for (String value : values) {
            deviceList.add(value);
        }

        final StableArrayAdapter adapter = new StableArrayAdapter(this, android.R.layout.simple_list_item_1
                , deviceList);
        mDeviceListView.setAdapter(adapter);
        mDeviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String item = (String ) parent.getItemAtPosition(position);
                //TODO replace with real logic lul
                mDeviceDataTextView.setText(item + "'s data:" + Math.random()*100);
            }
        });

        //TODO move to appropiate location
        checkBLEEnabled();
        checkLocationEnabled();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //TODO logic comes here
        if (id == R.id.action_start_service && !mServiceStarted) {
            mServiceStarted = true;
            Toast.makeText(this, "Starting service...", Toast.LENGTH_SHORT).show();
            Log.i("SERVICE_STATUS", "Service started");
            checkBLEEnabled();
            checkLocationEnabled();

            Intent i = new Intent(this, BluetoothService.class);
            startService(i);
        } else if (id == R.id.action_stop_service && mServiceStarted) {
            mServiceStarted =false;
            Toast.makeText(this, "Stopping service...", Toast.LENGTH_SHORT).show();
            Log.i("SERVICE_STATUS", "Service stopped");
            checkBLEEnabled();
            checkLocationEnabled();

            Intent i = new Intent(this, BluetoothService.class);
            stopService(i);
        }

        return super.onOptionsItemSelected(item);
    }

    protected boolean checkLocationEnabled() {
        LocationManager locationManager= (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (! locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(i, LOC_ENABLE_REQUEST);
            return false;
        }
        return true;
    }

    protected boolean checkBLEEnabled () {
        final BluetoothManager bluetoothManager= (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter =bluetoothManager.getAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(i, BT_ENABLE_REQUEST);
            return false;
        }
        return true;
    }

}
