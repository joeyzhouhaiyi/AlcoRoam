package com.elec390coen.alcoroam.Models;

public class TestResult {

    //parameter of test result
    String time;
    String reading;
    String type; //either alcohol or heart rate

    public TestResult(String time, String reading, String type) {
        this.time = time;
        this.reading = reading;
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getReading() {
        return reading;
    }

    public void setReading(String reading) {
        this.reading = reading;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
