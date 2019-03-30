package com.elec390coen.alcoroam.Controllers;

import android.bluetooth.BluetoothSocket;

import com.elec390coen.alcoroam.Models.CurrentAlcoholSensor;
import com.elec390coen.alcoroam.Models.CurrentHeartRateSensor;

public class DeviceManager {

    private static CurrentAlcoholSensor currentAlcoholSensor;
    private static CurrentHeartRateSensor currentHeartRateSensor;
    private static BluetoothSocket btSocket;

    public static CurrentAlcoholSensor getCurrentAlcoholSensor() {
        return currentAlcoholSensor;
    }

    public static void setCurrentAlcoholSensor(CurrentAlcoholSensor currentAlcoholSensor) {
        DeviceManager.currentAlcoholSensor = currentAlcoholSensor;
    }

    public static CurrentHeartRateSensor getCurrentHeartRateSensor() {
        return currentHeartRateSensor;
    }

    public static void setCurrentHeartRateSensor(CurrentHeartRateSensor currentHeartRateSensor) {
        DeviceManager.currentHeartRateSensor = currentHeartRateSensor;
    }

    public static BluetoothSocket getBtSocket() {
        return btSocket;
    }

    public static void setBtSocket(BluetoothSocket btSocket) {
        DeviceManager.btSocket = btSocket;
    }
}
