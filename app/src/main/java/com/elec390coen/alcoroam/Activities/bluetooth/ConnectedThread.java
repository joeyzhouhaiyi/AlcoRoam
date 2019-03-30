package com.elec390coen.alcoroam.Activities.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ConnectedThread extends Thread {
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private Handler btin;
    private int handlerState;
    //creation of the connect thread
    public ConnectedThread(BluetoothSocket socket, Handler btin, int handlerState) {
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        this.btin = btin;
        this.handlerState = handlerState;
        try {
            //Create I/O streams for connection
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (Exception e) {
            Log.d("WHAT",e.getMessage());
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
                Log.d("ERORRR","here");
                bytes = mmInStream.read(buffer,0,buffer.length);            //read bytes from input buffer
                String readMessage = new String(buffer, 0, bytes);
                // Send the obtained bytes to the UI Activity via handler
                btin.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
            } catch (Exception e) {
                Log.d("ERORRR",e.getMessage());
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
