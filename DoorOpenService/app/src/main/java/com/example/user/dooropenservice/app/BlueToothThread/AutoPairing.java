package com.example.user.dooropenservice.app.BluetoothThread;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class AutoPairing extends BroadcastReceiver {
    BluetoothDevice selectedDevice=null;
    private Context context;
    byte[] pin = {0x0001, 0x0002, 0x0003, 0x0004};

    public AutoPairing(Context context) {
        this.context = context;
        setIntentFilter();
    }

    public void setIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        filter.addAction(BluetoothAdapter.EXTRA_SCAN_MODE);

//        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
//        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        context.registerReceiver(this, filter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        BluetoothDevice device = null;


        Log.e("BroadCastTest", action);
        switch (action) {
            case BluetoothDevice.ACTION_PAIRING_REQUEST:
                if (selectedDevice!= null) {
                    if (selectedDevice.getName().equals("HC-06")) {
                        selectedDevice.setPin(pin);
                    }
                }
                break;
            case BluetoothDevice.ACTION_FOUND: //디바이스 발견
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getName() != null) {
                    if (device.getName().equals("HC-06")) {
                        selectedDevice = device;
                        selectedDevice.createBond();
                    }
                }
                break;
            case BluetoothAdapter.ACTION_DISCOVERY_STARTED:

                break;

        }

    }


}

