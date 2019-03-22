package com.elec390coen.alcoroam.Activities.HeartRate;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.elec390coen.alcoroam.Activities.Alcohol.AlcoholActivity;
import com.elec390coen.alcoroam.Controllers.DeviceManager;
import com.elec390coen.alcoroam.Models.CurrentAlcoholSensor;
import com.elec390coen.alcoroam.Models.CurrentHeartRateSensor;
import com.elec390coen.alcoroam.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

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
    private ConnectedThread mConnectedThread;
    private static final UUID BTUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.style_activity_heart);
        //Connect U.I Elements
        getHeartRate = (Button) findViewById(R.id.heartRateBtn);
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

    }

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
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {
                    String readMessage = (String) msg.obj;
                    recDataString.append(readMessage);
                                   // determine the end-of-line
                                                             // make sure there data before ~
                          // extract string
                                                  //get length of data received
                        if (recDataString.charAt(0) != 0)                             //if it starts with # we know it is what we are looking for
                        {

                            String sensor0 = recDataString.substring(1);
                            viewData.setText(". Sensor reading = " + sensor0 );    //update the textviews with sensor values

                        }
                        recDataString.delete(0, recDataString.length());                    //clear all string data


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
