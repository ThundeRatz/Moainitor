package org.thunderatz.dnery.thundermonitor;

/**
 * Created by dnery on 23/08/2016.
 */

import android.app.Activity;
import android.bluetooth.*;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import controlP5.Button;
import controlP5.ControlP5;
import processing.core.*;

public class Sketch extends PApplet {
    private static final String TAG = "Sketch";

    public String MAC = "20:16:05:19:17:79";

    private String mConnectedDeviceName = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothService mBtService = null;

    ControlP5 cp5;

    int backgnd = color(255, 0, 0);
    String status;
    byte[] readBuf;
    Button autoBtn, rcBtn, logBtn;
    MenuBar menu;

    public void settings() {
        size(displayWidth, displayHeight);
    }

    public void setup() {
        Log.d(TAG, "=== Entering setup() ===");
        orientation(PORTRAIT);

        cp5 = new ControlP5(this);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        mBtService = new BluetoothService(getActivity(), mHandler);
        readBuf = new byte[1024];
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(MAC);
        mBtService.connect(device, false);

        menu = new MenuBar();
    }

    public void draw() {
        backgnd = color(255, 255, 255);
        background(backgnd);
        fill(0);
        textSize(60);
        textAlign(CENTER, CENTER);

        menu.show();


    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "=== Entering onResume() ===");

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mBtService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mBtService.getState() == BluetoothService.STATE_NONE) {
                // Start the Bluetooth chat services
                mBtService.start();
            }
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(MAC);
            mBtService.connect(device, true);
        }
    }

    public void showToast(String text) {
        Toast msg = Toast.makeText(getActivity().getApplicationContext(), text, Toast.LENGTH_SHORT);
        msg.setGravity(Gravity.CENTER, 0, 0);
        msg.show();
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Activity activity = getActivity();
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            status = "Connected";

                            //mConversationArrayAdapter.clear();
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            status = "Connecting to Device";
                            break;
//                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            status = "Not Connected";
                            // setStatus(R.string.title_not_connected);
                            break;
                    }
                    showToast(status);
                    break;
                case Constants.MESSAGE_WRITE:
                    //byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    //String writeMessage = new String(writeBuf);
                    //mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
//                    String readMessage = new String(readBuf, 0, msg.arg1);
//                    mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
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

    private void sendMessage(byte[] pack) {
        // Check that we're actually connected before trying anything
        if (mBtService.getState() != BluetoothService.STATE_CONNECTED) {
            Log.d(TAG, "Device not connected");
//            Toast.makeText(getActivity(), "Device not connected", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (pack.length > 0) {
            // Get the message bytes and tell the BluetoothService to write
            mBtService.write(pack);
        }
    }

    public class MenuBar {
        int mHeight = height / 10;
        int xE = 0, xD = width, yC = height - mHeight, yB = height;
        int bckColor = color(83, 100, 130);
        int activeColor = color(0, 255, 0);
        int inactiveColor = color(255, 0, 0);

        public MenuBar() {
            autoBtn = cp5.addButton("autoBtn")
                    .setPosition(10 + 0*mHeight, 11*height / 12 - 10)
                    .setSize(height / 12, height / 12)
                    .setColorBackground(bckColor)
                    .setColorLabel(activeColor)
                    .activateBy(ControlP5.PRESSED)
                    .setLabel("AUTO");

            rcBtn = cp5.addButton("rcBtn")
                    .setPosition(20 + 1*mHeight, 11*height / 12 - 10)
                    .setSize(height / 12, height / 12)
                    .setColorBackground(bckColor)
                    .setColorLabel(inactiveColor)
                    .activateBy(ControlP5.PRESSED)
                    .setLabel("RC");

            logBtn = cp5.addButton("logBtn")
                    .setPosition(30 + 2*mHeight, 11*height / 12 - 10)
                    .setSize(height / 12, height / 12)
                    .setColorBackground(bckColor)
                    .setColorLabel(inactiveColor)
                    .activateBy(ControlP5.PRESSED)
                    .setLabel("LOG");

            autoBtn.getCaptionLabel().setSize(50);
            rcBtn.getCaptionLabel().setSize(50);
            logBtn.getCaptionLabel().setSize(50);
        }

        public void show() {
            fill(bckColor);
            rect(xE, yC, xD, yB);
        }
    }


    public void autoBtn(int val) {
        sendMessage(new byte[]{'1'});
    }

    public void rcBtn(int val) {
        sendMessage(new byte[]{'0'});
    }

    public void logBtn(int val) {
        sendMessage(new byte[]{'2'});
    }
}
