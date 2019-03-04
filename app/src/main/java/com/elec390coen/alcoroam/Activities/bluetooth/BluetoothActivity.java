package com.elec390coen.alcoroam.Activities.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.elec390coen.alcoroam.R;

import java.util.ArrayList;
import java.util.Set;

public class BluetoothActivity extends AppCompatActivity {
    private BluetoothAdapter BTAdapter;
    CheckBox enable_bt;
    TextView name_bt;
    ListView listview;
    ArrayList list;
    Button btn_search;
    ArrayAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.style_activity_bluetooth);

        enable_bt = findViewById(R.id.enable_bt);
        name_bt = findViewById(R.id.name_bt);
        listview = findViewById(R.id.list_view);
        list = new ArrayList();
        btn_search = findViewById(R.id.btn_search);
        adapter = new ArrayAdapter(BluetoothActivity.this, android.R.layout.simple_list_item_1, list);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(myListClickListener);

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btn_search.getText().toString().equals(getString(R.string.search)))
                {
                    btn_search.setText(R.string.stop);
                    searchForDevice();

                }else {
                    unregisterReceiver(bReciever);
                    BTAdapter.cancelDiscovery();
                    btn_search.setText(R.string.search);
                }
            }
        });


        BTAdapter = BluetoothAdapter.getDefaultAdapter();
        //Check the bluetooth is available
        if (BTAdapter == null){
            Toast.makeText(this,"Bluetooth is not supported", Toast.LENGTH_SHORT).show();
            finish();
        }else
        {
            name_bt.setText(getBTName());
            if(BTAdapter.isEnabled()){
                enable_bt.setChecked(true);
            }
        }

        //implement enable checkbox
        enable_bt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    BTAdapter.disable();
                    Toast.makeText(BluetoothActivity.this,"Turn off bluetooth", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, 0);
                    Toast.makeText(BluetoothActivity.this,"Turn on bluetooth", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //search for device and show the device name and address
    private void searchForDevice(){

        //setup intent filter and register receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(bReciever, filter);

        //request location permission
        int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_AND_FINE_LOCATION = 1;
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_AND_FINE_LOCATION);

        BTAdapter.startDiscovery();
        Toast.makeText(this,"Searching for Devices", Toast.LENGTH_SHORT).show();

    }

    //get current device name, if name is null get its address
    public String getBTName(){
        if (BTAdapter == null){
            BTAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        String name = BTAdapter.getName();
        if (name == null){
            name = BTAdapter.getAddress();
        }
        return name;
    }

    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick (AdapterView<?> av, View v, int arg2, long arg3)
        {
            // Get the device MAC address, the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            new ConnectBT(BluetoothActivity.this,address).execute();
        }
    };

    //display device name and address when device found
    private final BroadcastReceiver bReciever = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                findViewById(R.id.loading_progress).setVisibility(View.VISIBLE);
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                findViewById(R.id.loading_progress).setVisibility(View.GONE);
            }
            else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Create a new device item
                list.add("Device Name: "+device.getName()+"\nMAC Address: "+device.getAddress());
                // Add it to our adapter
                adapter.notifyDataSetChanged();
            }
        }
    };
}
