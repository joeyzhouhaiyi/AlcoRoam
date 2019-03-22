package com.elec390coen.alcoroam.Activities.Alcohol;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.elec390coen.alcoroam.Activities.bluetooth.BluetoothActivity;
import com.elec390coen.alcoroam.Activities.bluetooth.ConnectBT;
import com.elec390coen.alcoroam.Controllers.DeviceManager;
import com.elec390coen.alcoroam.Models.CurrentAlcoholSensor;
import com.elec390coen.alcoroam.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class AlcoholActivity extends AppCompatActivity {
    TextView viewData;
    TextView tv_connection_status;
    Handler btin;
    ProgressBar pb_alcohol_level;

    final int handlerState = 0;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();
    private ConnectedThread mConnectedThread;
    private static final UUID BTUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    //auto popup and sending call & message **********

    double currtest = 48.88; // current alcohol percentage
    double ableToDrive = Double.parseDouble(getString(R.string.driveLimit)); //driving limits
    double veryDrunk = Double.parseDouble(getString(R.string.tooDrunk));      // you are too drunk

    public void test()
    {
        if (currtest > ableToDrive && currtest < veryDrunk) {
            Intent i = new Intent(getApplicationContext(), PopActivity.class);
            startActivity(i);

        } else if (currtest > veryDrunk) {
            Intent i2 = new Intent(getApplicationContext(), Pop2Activity.class);
            startActivity(i2);
        }
    }


    //end **************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.style_activity_alcohol);
        initUI();

    }


    @Override
    public void onResume() {
        super.onResume();

        CurrentAlcoholSensor currentAlcoholSensor = DeviceManager.getCurrentAlcoholSensor();

        if(currentAlcoholSensor!=null)
        {
            BluetoothDevice device = currentAlcoholSensor.getDevice();
            tv_connection_status.setText("Connected to:\n"+device.getName()+ " - "+ device.getAddress());
            try {
                btSocket = createBluetoothSocket(device);
            } catch (IOException e) {
                Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
            }
            // Establish the Bluetooth socket connection.
            try
            {
                btSocket =(BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(device,1);
                btSocket.connect();
            } catch (Exception e) {

            }
            mConnectedThread = new ConnectedThread(btSocket);
            mConnectedThread.start();

            //I send a character when resuming.beginning transmission to check device is connected
            //If it is not an exception will be thrown in the write method and finish() will be called
            //mConnectedThread.write("x");
        }
        else
        {
            tv_connection_status.setText("Device not connected");
        }

        btin = new Handler(){
            public void handleMessage(android.os.Message msg){
                if(msg.what == handlerState){
                    String readMessage = (String)msg.obj;
                    recDataString.append(readMessage);
                    int endOfLineIndex = recDataString.indexOf("~");                    // determine the end-of-line
                    if (endOfLineIndex > 0) {                                           // make sure there data before ~
                        String dataInPrint = recDataString.substring(0, endOfLineIndex);    // extract string
                        int dataLength = dataInPrint.length();                          //get length of data received
                        if (recDataString.charAt(0) == '#')                             //if it starts with # we know it is what we are looking for
                        {

                            String sensor0 = recDataString.substring(1, dataLength);
                            viewData.setText("Data length = "+dataLength+". Sensor reading = " + sensor0 + "ml/g");    //update the textviews with sensor values

                        }
                        recDataString.delete(0, recDataString.length());                    //clear all string data
                        // strIncom =" ";
                        dataInPrint = " ";
                    }
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
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }


        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);        	//read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler
                    btin.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        /*
        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_LONG).show();
                finish();
            }
        }*/
    }


    private void initUI()
    {
        viewData = findViewById(R.id.tv_response);
        pb_alcohol_level = findViewById(R.id.pb_alcohol_level);
        tv_connection_status = findViewById(R.id.tv_connection_status);
    }

}


