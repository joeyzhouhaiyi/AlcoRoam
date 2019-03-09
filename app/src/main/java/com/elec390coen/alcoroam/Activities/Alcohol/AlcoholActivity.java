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
import android.widget.TextView;
import android.widget.Toast;

import com.elec390coen.alcoroam.Activities.bluetooth.BluetoothActivity;
import com.elec390coen.alcoroam.Activities.bluetooth.ConnectBT;
import com.elec390coen.alcoroam.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class AlcoholActivity extends AppCompatActivity {
    TextView viewData;
    Handler btin;

    final int handlerState = 0;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();
    private ConnectedThread mConnectedThread;

    private static final UUID BTUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private String address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.style_activity_alcohol);
        viewData = (TextView) findViewById(R.id.tv_response);
        btin = new Handler(){
            public void handleMessage(android.os.Message msg){
                if(msg.what == handlerState){
                    String readMessage = (String)msg.obj;
                    recDataString.append(readMessage);
                    //String sensor0 = recDataString.substring(1,5);
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
                        /*
                    if(viewData!=null)
                    viewData.setText("bt reading: "+recDataString);
                    recDataString.delete(0,recDataString.length());
*/}
                    }
            }
        };
        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
    }
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return device.createRfcommSocketToServiceRecord(BTUUID);
    }

    @Override
    public void onResume() {
        super.onResume();

        //get MAC address
        Intent intent = getIntent();
        //BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

        address = getAddressFromSharedPref();//intent.getStringExtra("bt_address");//
        viewData.setText(address);
        BluetoothDevice device = btAdapter.getRemoteDevice(address);
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
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
            try
            {
                btSocket =(BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(device,1);
                btSocket.connect();
                //btSocket.close();
            } catch (Exception e2)
            {
                //insert code to deal with this
            }
        }
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();

        //I send a character when resuming.beginning transmission to check device is connected
        //If it is not an exception will be thrown in the write method and finish() will be called
        //mConnectedThread.write("x");
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
    public String getAddressFromSharedPref()
    {
        SharedPreferences p = this.getSharedPreferences("alc", MODE_PRIVATE);
        return p.getString("current_bluetooth_address","null");
    }

    private void initUI()
    {

    }
}

