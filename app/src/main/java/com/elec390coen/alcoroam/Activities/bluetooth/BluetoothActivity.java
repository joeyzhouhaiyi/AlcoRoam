package com.elec390coen.alcoroam.Activities.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

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
    ShowcaseView showcaseView;

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
                if(showcaseView!=null)
                {
                    showcaseView.hide();
                }
                if(btn_search.getText().toString().equals(getString(R.string.search)))
                {
                    bluetoothHelper.registerReceiverWithFilter(bReciever); //register receiver
                    deviceList.clear(); //clear the list before a new discovery
                    adapter.notifyDataSetChanged();
                    searchForDevice();
                }else {
                    bluetoothHelper.stopSearchingForDevice();
                }
                SharedPreferences preferences = getSharedPreferences("LoginInfo",0);
                preferences.edit().putBoolean("playTutorial",false).apply();
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
        if(playTutorial())
        {
            showcaseView = new ShowcaseView.Builder(this)
                    .setTarget(new ViewTarget(R.id.enable_bt,this))
                    .setContentTitle("Enable Bluetooth")
                    .setContentText("Here you can turn on/off your bluetooth.")
                    .withHoloShowcase()
                    .setStyle(R.style.ShowcaseView_custom)
                    .blockAllTouches()
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showcaseView.hide();
                            NextTutorial();
                        }
                    })
                    .build();

        }

    }

    private void NextTutorial() {
        showcaseView = new ShowcaseView.Builder(this)
                .setTarget(new ViewTarget(R.id.btn_search, this))
                .setContentTitle("Select Device")
                .setContentText("Press SEARCH and select \"HC-05\" to establish connection.")
                .withHoloShowcase()
                .hideOnTouchOutside()
                .setStyle(R.style.ShowcaseView_custom)
                .build();
        showcaseView.forceTextPosition(ShowcaseView.ABOVE_SHOWCASE);
        showcaseView.hideButton();
    }

    private boolean playTutorial()
    {
        SharedPreferences preferences = getSharedPreferences("LoginInfo",0);
        boolean play = preferences.getBoolean("tut3",false);
        preferences.edit().putBoolean("tut3",false).apply();
        return play;
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
                if(device.getName()!=null)
                deviceList.add(device);
                adapter.notifyDataSetChanged();
            }else if(BluetoothDevice.ACTION_ACL_CONNECTED.equals(action))
            {
                Toast.makeText(BluetoothActivity.this,"Device Connected",Toast.LENGTH_SHORT).show();
            }
        }
    };
}
