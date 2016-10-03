package org.thunderatz.dnery.moainitor;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.Set;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import org.thunderatz.dnery.moainitor.processing.Auto;
import org.thunderatz.dnery.moainitor.processing.RC;
import org.thunderatz.dnery.moainitor.processing.Sensors;

/**
 * MainActivity.java
 * Moainitor Main Activity
 *
 * Autor: Daniel Nery Silva de Oliveira
 *
 * Equipe ThundeRatz de Robotica
 * 02/10/2016
 */

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    private String mConnectedDeviceName = null;
    private BluetoothAdapter mBtAdapter = null;

    public BluetoothService mBtService = null;

    private boolean selected = false;
    private ListView lv;

    public String btStatus;
    public String MAC;
    public int[] readBuf;

    private Fragment currentSketch;

    public int lSpeed, rSpeed;
    public int distances[], line[];
    public float battery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        readBuf = new int[32];
        distances = new int[7];
        line = new int[4];

        Toolbar tb = (Toolbar)findViewById(R.id.toolbar);
        setActionBar(tb);

        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();

        lv = (ListView)findViewById(R.id.listView_bt);
        if (mBtAdapter.isEnabled())
            listBluetooth();

        if (selected)
            connectBluetooth();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "... In onPause ...");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "... In onResume ...");
        if (!selected) {
            listBluetooth();
        } else if (mBtService.getState() != BluetoothService.STATE_CONNECTED){
            connectBluetooth();
        }
    }

    private void listBluetooth() {
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        final ArrayList<BluetoothDevice> btList = new ArrayList<>();
        final ArrayList<String> addrList = new ArrayList<>();
        if (pairedDevices.size() > 0) {
            for(BluetoothDevice bt : pairedDevices) {
                btList.add(bt);
                addrList.add(bt.getAddress());
            }
        }

        final BtAdapter adapter = new BtAdapter(this, btList);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MAC = addrList.get(position);
                setContentView(R.layout.activity_main);

                selected = true;
                connectBluetooth();
            }
        });
    }

    public void connectBluetooth() {
        final FragmentManager fragmentManager = getFragmentManager();

        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(int tabId) {
                switch (tabId) {
                    // The tab with id R.id.tab_favorites was selected,
                    // change your content accordingly.
                    case R.id.tab_auto:
                        currentSketch = new Auto();
                        break;

                    case R.id.tab_control:
                        currentSketch = new RC();
                        break;

                    case R.id.tab_sensors:
                        currentSketch = new Sensors();
                        break;
                }
                fragmentManager.beginTransaction()
                        .replace(R.id.container, currentSketch)
                        .commit();

                Toolbar tb = (Toolbar)findViewById(R.id.toolbar);
                setActionBar(tb);
            }
        });

        mBtService = new BluetoothService(this, mHandler);
        BluetoothDevice device = mBtAdapter.getRemoteDevice(MAC);
        mBtService.connect(device, false);
    }

    public final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Activity activity = MainActivity.this;
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            btStatus = "Connected";

                            //mConversationArrayAdapter.clear();
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            btStatus = "Connecting to Device";
                            break;
                        case BluetoothService.STATE_NONE:
                            btStatus = "Not Connected";
                            // setStatus(R.string.title_not_connected);
                            break;
                    }
                    Toast.makeText(MainActivity.this, btStatus, Toast.LENGTH_SHORT).show();
                    break;
                case Constants.MESSAGE_WRITE:
                    //byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    //String writeMessage = new String(writeBuf);
                    break;
                case Constants.MESSAGE_READ:
                    readBuf = (int[]) msg.obj;
                    parseBuf();
                    // construct a string from the valid bytes in the buffer
//                    String readMessage = new String(readBuf, 0, msg.arg1);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != activity) {
                        Toast.makeText(activity, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    public void sendMessage(int[] packet) {
        // Check that we're actually connected before trying anything
        if (mBtService.getState() != BluetoothService.STATE_CONNECTED) {
            Log.d(TAG, "Device not connected");
            return;
        }

        // Check that there's actually something to send
        if (packet.length > 0) {
            // Get the message bytes and tell the BluetoothService to write
            mBtService.write(packet);
        }
    }

    public void parseBuf() {
        int index, value;
        switch (readBuf[2]) {
            case Constants.CMD_SET_MOTORS:
                lSpeed = readBuf[4];
                rSpeed = readBuf[6];

                if (readBuf[3] == Constants.MOT_TRAS)
                    lSpeed = -lSpeed;
                if (readBuf[5] == Constants.MOT_FRENTE)
                    rSpeed = -rSpeed;
                break;

            case Constants.SENSOR_DISTANCE:
                index = readBuf[3];

                if (index >= distances.length)
                    return;

                value = readBuf[4];
                distances[index] = value;
                break;

            case Constants.SENSOR_LINE:
                index = readBuf[3];

                if (index >= line.length)
                    return;

                value = readBuf[4];
                line[index] = value;
                break;

            case Constants.BATTERY_LEVEL:
                value = readBuf[3];
                battery = (300 + value) / 100f;
                break;
        }
    }

    private void checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on
        // Emulator doesn't support Bluetooth and will return null
        if (mBtAdapter == null) {
            errorExit("Fatal Error", "Bluetooth not supported");
        } else {
            if (mBtAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth ON...");
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    private void errorExit(String title, String message){
        Toast.makeText(this, title + " - " + message, Toast.LENGTH_SHORT).show();
        finish();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Bluetooth Ativado", Toast.LENGTH_SHORT).show();
            } else {
                errorExit("Error", "Bluetooth must be enabled");
            }
        }
    }
}
