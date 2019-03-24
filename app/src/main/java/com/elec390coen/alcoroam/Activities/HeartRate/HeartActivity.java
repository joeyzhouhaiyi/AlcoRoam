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

public class HeartActivity<GraphView, LineGraphView> extends AppCompatActivity {
    private int x = 0;
    private Button getHeartRate;
    private Button refreshButton;
    private TextView viewData;
    private TextView connectionStsView;
    Handler btin;
    final int handlerState = 0;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();
    private static final UUID BTUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    BluetoothAdapter adapter = null;
    BTClient _bt;
    ZephyrProtocol _protocol;
    NewConnectedListener _NConnListener;
    private final int HEART_RATE = 0x100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.style_activity_heart);
        /*Sending a message to android that we are going to initiate a pairing request*/
        IntentFilter filter = new IntentFilter("android.bluetooth.device.action.PAIRING_REQUEST");
        /*Registering a new BTBroadcast receiver from the Main Activity context with pairing request event*/
        this.getApplicationContext().registerReceiver(new BTBroadcastReceiver(), filter);
        // Registering the BTBondReceiver in the application that the status of the receiver has changed to Paired
        IntentFilter filter2 = new IntentFilter("android.bluetooth.device.action.BOND_STATE_CHANGED");
        this.getApplicationContext().registerReceiver(new BTBondReceiver(), filter2);

        //Obtaining the handle to act on the CONNECT button
        TextView tv = (TextView) findViewById(R.id.connectionStsView);
        String ErrorText  = "Not Connected to HxM ! !";
        tv.setText(ErrorText);
        //Connect U.I Elements
        getHeartRate = (Button) findViewById(R.id.heartRateBtn);
        if (getHeartRate != null)
        {
            getHeartRate.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    String BhMacID = "00:07:80:9D:8A:E8";
                    //String BhMacID = "00:07:80:88:F6:BF";
                    adapter = BluetoothAdapter.getDefaultAdapter();

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
                    String DeviceName = Device.getName();
                    _bt = new BTClient(adapter, BhMacID);
                    _NConnListener = new NewConnectedListener(Newhandler,Newhandler);
                    _bt.addConnectedEventListener(_NConnListener);

                    TextView tv1 = (TextView) findViewById(R.id.hrValueView);
                    tv1.setText("000");


                    ((TextView) findViewById(R.id.connectionStsView)).setText(BhMacID);

                    if(_bt.IsConnected())
                    {
                        _bt.start();
                        TextView tv = (TextView) findViewById(R.id.connectionStsView);
                        String ErrorText  = "Connected to HxM "+DeviceName;
                        tv.setText(ErrorText);

                        //Reset all the values to 0s

                    }
                    else
                    {
                        TextView tv = (TextView) findViewById(R.id.connectionStsView);
                        String ErrorText  = "Unable to Connect !";
                        tv.setText(ErrorText);
                    }
                }
            });
        }
        Button btnDisconnect = (Button) findViewById(R.id.disconnect);
        if (btnDisconnect != null)
        {
            btnDisconnect.setOnClickListener(new View.OnClickListener() {
                @Override
                /*Functionality to act if the button DISCONNECT is touched*/
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    /*Reset the global variables*/
                    TextView tv = (TextView) findViewById(R.id.connectionStsView);
                    String ErrorText  = "Disconnected from HxM!";
                    tv.setText(ErrorText);

                    /*This disconnects listener from acting on received messages*/
                    _bt.removeConnectedEventListener(_NConnListener);
                    /*Close the communication with the device & throw an exception if failure*/
                    _bt.Close();

                }
            });
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
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (NoSuchMethodException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
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
                    if (tv != null)tv.setText(HeartRatetext);

            }
        }

    };
}
/*
    connectionStsView = (TextView) findViewById(R.id.connectionStsView);
        refreshButton = (Button) findViewById(R.id.refreshBtn);
        viewData = findViewById(R.id.hrValueView);
        /*final GraphView graphView = new LineGraphView(
                this, // context
                "Heart Rate Sensor" // heading
        );

        graphView.setVerticalLabels(new String[]{"high", "normal", "low"});

        graphView.setCustomLabelFormatter(new CustomLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    return null;
                } else {
                    if (value < 60) {
                        return "low";
                    } else if (value < 100) {
                        return "normal";
                    } else {
                        return "high";
                    }
                }
            }
        });*/


/*
    @Override
    public void onResume() {
        super.onResume();

        CurrentHeartRateSensor currentHeartRateSensor = DeviceManager.getCurrentHeartRateSensor();

        if (currentHeartRateSensor != null) {
            BluetoothDevice device = currentHeartRateSensor.getDevice();
            connectionStsView.setText("Connected to:\n" + device.getName() + " - " + device.getAddress());
            try {
                btSocket = createBluetoothSocket(device);
            } catch (IOException e) {
                Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
            }
            // Establish the Bluetooth socket connection.
            try {
                btSocket = (BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(device, 1);
                btSocket.connect();
            } catch (Exception e) {

            }
            mConnectedThread = new ConnectedThread(btSocket);
            mConnectedThread.start();

            //I send a character when resuming.beginning transmission to check device is connected
            //If it is not an exception will be thrown in the write method and finish() will be called
            //mConnectedThread.write("x");
        } else {
            connectionStsView.setText("Device not connected");
        }

        btin = new Handler() {
            public void handleMessage(Message msg)
            {
                TextView tv;
                if (msg.what == HEART_RATE)
                {
                        String HeartRatetext = msg.getData().getString("HeartRate");
                        tv = (EditText)findViewById(R.id.hrValueView);
                        System.out.println("Heart Rate Info is "+ HeartRatetext);
                        if (tv != null)tv.setText(HeartRatetext);
                }
            }

        };
    }


    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return device.createRfcommSocketToServiceRecord(BTUUID);
    }


    public class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }


        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);            //read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler
                    btin.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

    }
}
*/