package com.elec390coen.alcoroam.Controllers;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.widget.Toast;

import com.elec390coen.alcoroam.R;

public class BluetoothHelper {

    private Context context;
    private BluetoothAdapter BTAdapter;


    public BluetoothHelper(Context context)
    {
        this.context = context;
        BTAdapter = BluetoothAdapter.getDefaultAdapter();
        BTAdapter.enable();
        if(BTAdapter == null)
        {
            Toast.makeText(context,"Bluetooth is not supported",Toast.LENGTH_LONG).show();
            ((Activity)context).finish();
        }
    }

    public boolean bluetoothIsAvailable()
    {
        return BTAdapter.isEnabled();
    }

    //turn off bluetooth
    public void turnOffBluetooth()
    {
        BTAdapter.disable();
        Toast.makeText(context,"Bluetooth Turned Off", Toast.LENGTH_SHORT).show();
    }

    //get current bluetooth adapter
    public BluetoothAdapter getBTAdapter()
    {
        return BTAdapter;
    }

    //get current device name
    public String getCurrentBluetoothDeviceName()
    {
        return BTAdapter.getName();
    }

    public void startSearchingForDevice()
    {
        BTAdapter.startDiscovery();
        Toast.makeText(context,"Searching for Devices", Toast.LENGTH_SHORT).show();
    }

    public void stopSearchingForDevice()
    {
        BTAdapter.cancelDiscovery();
        Toast.makeText(context,"Searching stopped", Toast.LENGTH_SHORT).show();
    }

    public void registerReceiverWithFilter(BroadcastReceiver broadcastReceiver)
    {
        //setup intent filter and register receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(broadcastReceiver, filter);
    }
}
