package com.elec390coen.alcoroam.Activities.Setting;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;


public class GPStracker implements LocationListener {
    Context context;
    public GPStracker(Context c)
    {
        context = c;
    }
    Location myLocation;

    public Location getMyLocation() {
        return myLocation;
    }

    public void getLocation(){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)!=
        PackageManager.PERMISSION_GRANTED){
            Toast.makeText(context, "Permission not Granted", Toast.LENGTH_SHORT).show();
            myLocation= null;
        }
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGPSEnabled){
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,6000,10,this);
            Location l =lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            myLocation=  l;
        }else {
            Toast.makeText(context, "Please Enable GPS", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        myLocation = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
