package org.thunderatz.dnery.moainitor;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * BtAdapter.java
 * ArrayAdapter for Bluetooth Devices
 * Shows Device Name and MAC address
 *
 * Autor: Daniel Nery Silva de Oliveira
 *
 * Equipe ThundeRatz de Robotica
 * 02/10/2016
 */

public class BtAdapter extends ArrayAdapter<BluetoothDevice> {
    public BtAdapter(Context context, ArrayList<BluetoothDevice> bluetoothDevices) {
        super(context, 0, bluetoothDevices);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BluetoothDevice bluetoothDevice = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.bt_list_item, parent, false);
        }

        TextView btName = (TextView) convertView.findViewById(R.id.btName);
        TextView btAddr = (TextView) convertView.findViewById(R.id.btAddr);

        btName.setText(bluetoothDevice.getName());
        btAddr.setText(bluetoothDevice.getAddress());

        return convertView;
    }
}
