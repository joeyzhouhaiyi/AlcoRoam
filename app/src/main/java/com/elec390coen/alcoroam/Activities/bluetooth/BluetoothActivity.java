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

import com.elec390coen.alcoroam.Activities.Alcohol.BluetoothServices;
import com.elec390coen.alcoroam.Controllers.BluetoothHelper;
import com.elec390coen.alcoroam.Controllers.DeviceManager;
import com.elec390coen.alcoroam.Models.CurrentAlcoholSensor;
import com.elec390coen.alcoroam.R;

import java.util.ArrayList;
import java.util.Set;

public class BluetoothActivity extends AppCompatActivity {

    CheckBox enable_bt;
    TextView name_bt;
    ListView listview;
    ArrayList<BluetoothDevice> deviceList;
    Button btn_search;
    ArrayAdapter adapter;
    BluetoothHelper bluetoothHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.style_activity_bluetooth);

        //initialize helper, close the screen if bluetooth is not supported
        bluetoothHelper = new BluetoothHelper(BluetoothActivity.this);
        initView();
        //search button listener
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btn_search.getText().toString().equals(getString(R.string.search)))
                {
                    bluetoothHelper.registerReceiverWithFilter(bReciever); //register receiver
                    deviceList.clear(); //clear the list before a new discovery
                    adapter.notifyDataSetChanged();
                    searchForDevice();
                }else {
                    bluetoothHelper.stopSearchingForDevice();
                }
            }
        });
        //implement enable checkbox
        enable_bt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    bluetoothHelper.turnOffBluetooth();
                }
                else{
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, 0);
                    Toast.makeText(BluetoothActivity.this,"Turn on bluetooth", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initView()
    {
        enable_bt = findViewById(R.id.enable_bt);
        name_bt = findViewById(R.id.name_bt);
        listview = findViewById(R.id.list_view);
        deviceList = new ArrayList<>();
        btn_search = findViewById(R.id.btn_search);
        adapter = new ArrayAdapterForBluetoothDevices(BluetoothActivity.this,deviceList);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice selectedDevice = (BluetoothDevice)parent.getItemAtPosition(position);
                Intent startServiceIntent = new Intent(BluetoothActivity.this,BluetoothServices.class);
                startServiceIntent.putExtra("deviceAddress",selectedDevice.getAddress());
                startService(startServiceIntent);
                //bluetoothHelper.stopSearchingForDevice();
                //DeviceManager.setCurrentAlcoholSensor(new CurrentAlcoholSensor(selectedDevice));
                //new ConnectBT(BluetoothActivity.this,selectedDevice).execute();
            }
        });
        //set current device name and initialize checkbox
        name_bt.setText(bluetoothHelper.getCurrentBluetoothDeviceName());
        if(bluetoothHelper.bluetoothIsAvailable()){
            enable_bt.setChecked(true);
        }
    }

    //search for device and show the device name and address
    private void searchForDevice(){
        bluetoothHelper.registerReceiverWithFilter(bReciever);
        //request location permission
        int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_AND_FINE_LOCATION = 1;
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_AND_FINE_LOCATION);

        bluetoothHelper.startSearchingForDevice();
    }


    //display device name and address when device found
    private final BroadcastReceiver bReciever = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                findViewById(R.id.loading_progress).setVisibility(View.VISIBLE);
                btn_search.setText(getString(R.string.stop));
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                findViewById(R.id.loading_progress).setVisibility(View.GONE);
                btn_search.setText(getString(R.string.search));
            }
            else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                deviceList.add(device);
                adapter.notifyDataSetChanged();
            }
        }
    };
}
