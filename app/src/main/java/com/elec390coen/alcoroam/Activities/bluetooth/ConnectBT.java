package com.elec390coen.alcoroam.Activities.bluetooth;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.elec390coen.alcoroam.Activities.Alcohol.AlcoholActivity;

import java.io.IOException;
import java.util.UUID;

public class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
{
    private boolean ConnectSuccess = true; //if it's here, it's almost connected
    Context context;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    ConnectBT(Context context, String address){
        this.context = context;
        this.address = address;
    }

    @Override
    protected void onPreExecute()
    {
        progress = ProgressDialog.show(context, "Connecting...", "Please wait!!!");  //show a progress dialog
    }

    @Override
    protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
    {
        try
        {
            if (btSocket == null || !isBtConnected)
            {
                myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                btSocket.connect();//start connection
                /*
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = pref.edit();

                editor.putString("current_bluetooth_address",address);
                editor.apply();*/


            }
        }
        catch (IOException e)
        {
            ConnectSuccess = false;//if the try failed, you can check the exception here
        }
        return null;
    }
    @Override
    protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
    {
        super.onPostExecute(result);

        if (!ConnectSuccess)
        {
            msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
        }
        else
        {
            msg("Connected.");
            isBtConnected = true;
            /*
            Intent i = new Intent(context, AlcoholActivity.class);
            i.putExtra("bt_address", address);
            //context.startActivity(i);*/
            SharedPreferences p = context.getSharedPreferences("alc", Context.MODE_PRIVATE);
            SharedPreferences.Editor e = p.edit();
            e.putString("current_bluetooth_address",address);
            e.apply();
        }
        progress.dismiss();
    }

    private void msg(String s)
    {
        Toast.makeText(context,s,Toast.LENGTH_LONG).show();
    }
}