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
    private Set<BluetoothDevice> pairedDevices;
    public static String EXTRA_ADDRESS = "device_address";
    CheckBox enable_bt;
    ImageView search_bt;
    TextView name_bt;
    ListView listview;
    ArrayList list;
    Button btn_cancel;
    ArrayAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.style_activity_bluetooth);

        enable_bt = findViewById(R.id.enable_bt);
        search_bt = findViewById(R.id.search_bt);
        name_bt = findViewById(R.id.name_bt);
        listview = findViewById(R.id.list_view);
        list = new ArrayList();
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BTAdapter.cancelDiscovery();
            }
        });

        adapter = new ArrayAdapter(BluetoothActivity.this, android.R.layout.simple_list_item_1, list );
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(myListClickListener);


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

        search_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                list();
            }
        });
    }

    //paired method and show the device name and address
    private void list(){
        /*
        pairedDevices = BTAdapter.getBondedDevices();
        ArrayList list = new ArrayList();
        for(BluetoothDevice bt : pairedDevices) {
            list.add("name:"+bt.getName() +" \naddress:"+ bt.getAddress() +" \nbound state:"+ bt.getBondState());
        }
        */

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(bReciever, filter);
        int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
        BTAdapter.startDiscovery();
        Toast.makeText(this,"Searching for Devices", Toast.LENGTH_SHORT).show();

    }
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

            new ConnectBT(BluetoothActivity.this,"00:07:80:0E:B1:0C").execute();
        }
    };


    private final BroadcastReceiver bReciever = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                //discovery starts, we can show progress dialog or perform other tasks
                Toast.makeText(BluetoothActivity.this,"dicovery started",Toast.LENGTH_SHORT).show();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //discovery finishes, dismis progress dialog
                Toast.makeText(BluetoothActivity.this,"dicovery finished",Toast.LENGTH_SHORT).show();
            }else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Create a new device item
                list.add("name:"+device.getName()+"\naddress:"+device.getAddress());
                // Add it to our adapter
                adapter.notifyDataSetChanged();
                Toast.makeText(BluetoothActivity.this,"name:"+device.getName()+"\naddress:"+device.getAddress(),Toast.LENGTH_SHORT).show();
            }
        }
    };
}
