package com.elec390coen.alcoroam.Activities.HeartRate;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.elec390coen.alcoroam.Activities.Alcohol.AlcoholActivity;
import com.elec390coen.alcoroam.Controllers.BluetoothHelper;
import com.elec390coen.alcoroam.Controllers.DeviceManager;
import com.elec390coen.alcoroam.Models.CurrentAlcoholSensor;
import com.elec390coen.alcoroam.Models.CurrentHeartRateSensor;
import com.elec390coen.alcoroam.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

import zephyr.android.HxMBT.BTClient;
import zephyr.android.HxMBT.ConnectListenerImpl;
import zephyr.android.HxMBT.ZephyrProtocol;

public class HeartActivity extends AppCompatActivity {

    GraphView gv_heart;
    private LineGraphSeries<DataPoint> series;
    static int second=0;
    boolean gettingHR=false;
    private Button getHeartRate;
    BluetoothAdapter adapter = null;
    BTClient _bt;
    NewConnectedListener _NConnListener;
    private final int HEART_RATE = 0x100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.style_activity_heart);
        gv_heart = findViewById(R.id.gv_heart);
        series = new LineGraphSeries<>();
        gv_heart.addSeries(series);
        gv_heart.getViewport().setXAxisBoundsManual(true);
        gv_heart.getViewport().setMinX(0);
        gv_heart.getViewport().setMaxX(60);
        gv_heart.getViewport().setYAxisBoundsManual(true);
        gv_heart.getViewport().setMinY(0);
        gv_heart.getViewport().setMaxY(160);

        /*Sending a message to android that we are going to initiate a pairing request*/
        IntentFilter filter = new IntentFilter("android.bluetooth.device.action.PAIRING_REQUEST");
        /*Registering a new BTBroadcast receiver from the Main Activity context with pairing request event*/
        this.getApplicationContext().registerReceiver(new BTBroadcastReceiver(), filter);
        // Registering the BTBondReceiver in the application that the status of the receiver has changed to Paired
        IntentFilter filter2 = new IntentFilter("android.bluetooth.device.action.BOND_STATE_CHANGED");
        this.getApplicationContext().registerReceiver(new BTBondReceiver(), filter2);

        //Obtaining the handle to act on the CONNECT button
        TextView tv = findViewById(R.id.connectionStsView);
        if(_bt == null)
        {
            String ErrorText  = "Not connected to HxM";
            tv.setText(ErrorText);
        }else
        {
            if(_bt.IsConnected())
            {
                String ErrorText  = "Connected to HxM "+_bt.getDevice().getName();
                tv.setText(ErrorText);
            }
        }
        //Connect U.I Elements
        getHeartRate = (Button) findViewById(R.id.heartRateBtn);
        if (getHeartRate != null)
        {
            getHeartRate.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    String DeviceName="";
                    if(_bt == null) {
                        String BhMacID = "00:07:80:9D:8A:E8";
                        Toast.makeText(HeartActivity.this, "Connecting...", Toast.LENGTH_LONG).show();
                        adapter = BluetoothAdapter.getDefaultAdapter();
                        second = 0;
                        Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();

                        if (pairedDevices.size() > 0)
                        {
                            for (BluetoothDevice device : pairedDevices)
                            {
                                if (device.getName().startsWith("HXM"))
                                {
                                    BluetoothDevice btDevice = device;
                                    BhMacID = btDevice.getAddress();
                                    break;

                                }
                            }
                        }

                        //BhMacID = btDevice.getAddress();
                        BluetoothDevice Device = adapter.getRemoteDevice(BhMacID);
                        DeviceName = Device.getName();
                        _bt = new BTClient(adapter, BhMacID);
                        _NConnListener = new NewConnectedListener(Newhandler, Newhandler);
                        _bt.addConnectedEventListener(_NConnListener);
                    }
                    TextView tv1 = findViewById(R.id.hrValueView);
                    tv1.setText("");

                    if(_bt.IsConnected())
                    {
                        if(!gettingHR)
                        _bt.start();
                        gettingHR = true;
                        TextView tv = findViewById(R.id.connectionStsView);
                        String ErrorText  = "Connected to HxM "+DeviceName;
                        tv.setText(ErrorText);
                    }
                    else
                    {
                        TextView tv = findViewById(R.id.connectionStsView);
                        String ErrorText  = "Unable to Connect!";
                        tv.setText(ErrorText);
                    }
                }
            });
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(_bt != null)
        {
            if(_bt.IsConnected()) {
                /*This disconnects listener from acting on received messages*/
                _bt.removeConnectedEventListener(_NConnListener);
                /*Close the communication with the device & throw an exception if failure*/
                _bt.Close();
            }
        }

    }

    private class BTBondReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();
            BluetoothDevice device = adapter.getRemoteDevice(b.get("android.bluetooth.device.extra.DEVICE").toString());
            Log.d("Bond state", "BOND_STATED = " + device.getBondState());
        }
    }
    private class BTBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("BTIntent", intent.getAction());
            Bundle b = intent.getExtras();
            Log.d("BTIntent", b.get("android.bluetooth.device.extra.DEVICE").toString());
            Log.d("BTIntent", b.get("android.bluetooth.device.extra.PAIRING_VARIANT").toString());
            try {
                BluetoothDevice device = adapter.getRemoteDevice(b.get("android.bluetooth.device.extra.DEVICE").toString());
                Method m = BluetoothDevice.class.getMethod("convertPinToBytes", new Class[] {String.class} );
                byte[] pin = (byte[])m.invoke(device, "1234");
                m = device.getClass().getMethod("setPin", new Class [] {pin.getClass()});
                Object result = m.invoke(device, pin);
                Log.d("BTTest", result.toString());
            } catch (SecurityException e1) {
                e1.printStackTrace();
            } catch (NoSuchMethodException e1) {
                e1.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    final  Handler Newhandler = new Handler(){
        public void handleMessage(Message msg)
        {
            TextView tv;
            if(msg.what == HEART_RATE)
            {

                    String HeartRatetext = msg.getData().getString("HeartRate");
                    tv = findViewById(R.id.hrValueView);
                    System.out.println("Heart Rate Info is "+ HeartRatetext);
                    if (tv != null)
                    {
                        second++;
                        tv.setText(HeartRatetext+"BPM");
                        series.appendData(new DataPoint(second,Integer.parseInt(HeartRatetext)),true,60);
                    }


            }
        }

    };
}