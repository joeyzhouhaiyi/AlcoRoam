package com.elec390coen.alcoroam.Models;

import android.bluetooth.BluetoothDevice;

public class CurrentAlcoholSensor {

    String name;
    String address;
    BluetoothDevice device=null;

    public CurrentAlcoholSensor(BluetoothDevice device)
    {
        this.device = device;
        this.name = device.getName();
        this.address = device.getAddress();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }
}
