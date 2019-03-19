package com.elec390coen.alcoroam.Controllers;

import com.elec390coen.alcoroam.Models.CurrentAlcoholSensor;
import com.elec390coen.alcoroam.Models.CurrentHeartRateSensor;

public class DeviceManager {

    private static CurrentAlcoholSensor currentAlcoholSensor;
    private static CurrentHeartRateSensor currentHeartRateSensor;

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
}
