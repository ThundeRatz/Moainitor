package org.thunderatz.dnery.thundermonitor;

import android.os.Bundle;
import android.app.Activity;
import android.app.FragmentManager;
import android.bluetooth.*;

import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    private BluetoothAdapter btAdapter = null;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private boolean selected = false;
    private ListView lv;

    public String MAC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();

        lv = (ListView)findViewById(R.id.listView_bt);
        if (btAdapter.isEnabled())
            listBluetooth();
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
        }
    }

    private void listBluetooth() {
//        showToast("Dispositivos Pareados");
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

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
                selected = true;

                setContentView(R.layout.activity_main);
                FragmentManager fragmentManager = getFragmentManager();
                Sketch fragment = new Sketch();
                fragment.MAC = MAC;
                fragmentManager.beginTransaction()
                        .replace(R.id.container, fragment)
                        .commit();
            }
        });
    }

    public void showToast(String text) {
        Toast msg = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        msg.setGravity(Gravity.CENTER, 0, 0);
        msg.show();
    }

    private void checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on
        // Emulator doesn't support Bluetooth and will return null
        if (btAdapter == null) {
            errorExit("Fatal Error", "Bluetooth not supported");
        } else {
            if (btAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth ON...");
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    private void errorExit(String title, String message){
        showToast(title + " - " + message);
        finish();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                showToast("Bluetooth Ativado");
            } else {
                errorExit("Error", "Bluetooth must be enabled");
            }
        }
    }
}
