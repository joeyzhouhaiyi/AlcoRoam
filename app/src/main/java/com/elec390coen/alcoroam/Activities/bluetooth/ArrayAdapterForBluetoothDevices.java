package com.elec390coen.alcoroam.Activities.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.elec390coen.alcoroam.R;

import java.util.ArrayList;

public class ArrayAdapterForBluetoothDevices extends ArrayAdapter<BluetoothDevice> {

    public ArrayAdapterForBluetoothDevices(Context context, ArrayList<BluetoothDevice> devices) {
        super(context, 0, devices);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        BluetoothDevice device = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_bluetooth_devices, parent, false);
        }

        TextView tv_device_name = convertView.findViewById(R.id.tv_device_name);
        TextView tv_device_address = convertView.findViewById(R.id.tv_device_address);

        tv_device_name.setText(device.getName() == null ? "Unknown" : device.getName());
        tv_device_address.setText(device.getAddress());
        // Return the completed view to render on screen
        return convertView;
    }

}
