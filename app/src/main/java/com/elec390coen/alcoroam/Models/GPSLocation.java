package com.elec390coen.alcoroam.Models;

public class GPSLocation {
    String lon;
    String lat;
    String lastSeen;
    private static GPSLocation instance = null;

    public static GPSLocation getInstance()
    {
        if (instance == null)
            instance = new GPSLocation();

        return instance;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLastSeen(String lastSeen)
    {
        this.lastSeen = lastSeen;
    }

    public String getLastSeen()
    {
        return this.lastSeen;
    }

}
