package com.elec390coen.alcoroam.Activities.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
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

    CheckBox enable_bt, visible;
    ImageView search_bt;
    TextView name_bt;
    ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.style_activity_bluetooth);

        enable_bt = findViewById(R.id.enable_bt);
        visible = findViewById(R.id.visible);
        search_bt = findViewById(R.id.search_bt);
        name_bt = findViewById(R.id.name_bt);
        listview = findViewById(R.id.list_view);

        name_bt.setText(getBTName());

        BTAdapter = BluetoothAdapter.getDefaultAdapter();
        //Check the bluetooth is available
        if (BTAdapter == null){
            Toast.makeText(this,"Bluetooth is not supported", Toast.LENGTH_SHORT).show();
            finish();
        }
        if(BTAdapter.isEnabled()){
            enable_bt.setChecked(true);
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
        //implement visible checkbox
        visible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    startActivityForResult(intent,0);
                    Toast.makeText(BluetoothActivity.this,"The device is discoverable", Toast.LENGTH_SHORT).show();
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
        pairedDevices = BTAdapter.getBondedDevices();
        ArrayList list = new ArrayList();
        for(BluetoothDevice bt : pairedDevices) {
            list.add(bt.getName());
        }
        Toast.makeText(this,"Showing Devices", Toast.LENGTH_SHORT).show();
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list );
        listview.setAdapter(adapter);
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
}
