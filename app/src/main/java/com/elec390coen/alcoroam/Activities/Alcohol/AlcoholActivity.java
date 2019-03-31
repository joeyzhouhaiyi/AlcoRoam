package com.elec390coen.alcoroam.Activities.Alcohol;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.elec390coen.alcoroam.Activities.Setting.GPStracker;
import com.elec390coen.alcoroam.Activities.bluetooth.BluetoothActivity;
import com.elec390coen.alcoroam.Activities.bluetooth.ConnectedThread;
import com.elec390coen.alcoroam.Controllers.DeviceManager;
import com.elec390coen.alcoroam.Controllers.FireBaseAuthHelper;
import com.elec390coen.alcoroam.Controllers.FireBaseDBHelper;
import com.elec390coen.alcoroam.Models.GPSLocation;
import com.elec390coen.alcoroam.Models.TestResult;
import com.elec390coen.alcoroam.Models.User;
import com.elec390coen.alcoroam.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LabelFormatter;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;



public class AlcoholActivity extends AppCompatActivity {
    TextView viewData;
    TextView tv_risk;
    ProgressBar pb_alcohol_level;
    Button record;
    GraphView readingPlot;
    LineGraphSeries series;
    Button btn_refresh_connection;

    private StringBuilder recDataString = new StringBuilder();
    private static final int PERMISSION_REQUEST_CODE = 1;

    List<TestResult> results = new ArrayList<>();
    FireBaseDBHelper fireBaseDBHelper;
    FireBaseAuthHelper fireBaseAuthHelper;
    GPSLocation myLocation;

    private String contactName;
    private String contactNumber;
    private double currtest; // current alcohol percentage
    private double ableToDrive;
    private double veryDrunk;     // you are too drunk
    private boolean testButtonPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.style_activity_alcohol);
        initUI();
        UpdateGraph();
        setButtonListener();
        Intent i = getIntent();
        contactName = i.getStringExtra("name");
        contactNumber = i.getStringExtra("number");
        myLocation = GPSLocation.getInstance();
        ableToDrive = Double.parseDouble(getString(R.string.driveLimit)); //driving limits
        veryDrunk = Double.parseDouble(getString(R.string.tooDrunk));      // you are too drunk
        //saveLocation();
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("messageIntent"));
    }

    private void setButtonListener() {
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testButtonPressed = true;
                final LinearLayout ll = findViewById(R.id.ll_blow);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ll.setVisibility(View.GONE);
                        testButtonPressed = false;
                        counter=0;
                        pb_alcohol_level.setProgress((int) maxReading / 10);
                        setRiskLevel(maxReading);
                        viewData.setText("Sensor reading = " + String.valueOf(maxReading / 1000) + "g/L");    //update the textviews with sensor values
                        maxReading=0;
                        UpdateGraph();
                    }
                }, 5000);
                ll.setVisibility(View.VISIBLE);
                Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.mytransition);
                ll.startAnimation(animFadeIn);

            }
        });
        btn_refresh_connection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = getSharedPreferences("BT_NAME", 0);
                String bt_address = pref.getString("lastConnectedBTAddress","00:18:E4:34:DD:3F");
                Intent startServiceIntent = new Intent(AlcoholActivity.this,BluetoothServices.class);
                startServiceIntent.putExtra("deviceAddress",bt_address);
                startService(startServiceIntent);
            }
        });
    }

    protected void UpdateGraph()
    {
        fireBaseDBHelper.getUserRefWithId(fireBaseAuthHelper.getCurrentUser().getUid()).child("AlcoholReadings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int index = 0;
                DataPoint[] dp = new DataPoint[(int)dataSnapshot.getChildrenCount()];
                for (DataSnapshot readingSnapshot: dataSnapshot.getChildren()) {
                    TestResult t = readingSnapshot.getValue(TestResult.class);
                    //resultList.add(t);
                    String time = t.getTime();
                    SimpleDateFormat formater = new SimpleDateFormat("dd/MM hh:mm:ss");
                    try {
                        Date x = formater.parse(time);

                        double y = Double.parseDouble(t.getReading());
                        dp[index] = new DataPoint(x,y);
                        index++;
                    }catch (Exception ex)
                    {
                        Log.d("TAG",ex.getLocalizedMessage());
                    }
                }
                series.resetData(dp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private String getCurrentTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String currentTimeStamp = simpleDateFormat.format(new Date());
        return currentTimeStamp;
    }


    private void initUI() {
        viewData = findViewById(R.id.tv_response);
        tv_risk = findViewById(R.id.tv_riskLevel);
        pb_alcohol_level = findViewById(R.id.pb_alcohol_level);
        btn_refresh_connection = findViewById(R.id.btn_refresh_connection);
        fireBaseDBHelper = new FireBaseDBHelper();
        fireBaseAuthHelper = new FireBaseAuthHelper();
        record=findViewById(R.id.recordAlco);
        readingPlot = findViewById(R.id.alcoView);
        series = new LineGraphSeries();
        readingPlot.addSeries(series);
//        readingPlot.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
//            @Override
//            public String formatLabel(double value, boolean isValueX) {
//                if(isValueX)
//                {
//                    return
//                }else
//                {
//                    return super.formatLabel(value, isValueX);
//                }
//
//            }
//        });
        readingPlot.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this,new SimpleDateFormat("dd/MM\nhh:mm:ss")));
        readingPlot.getGridLabelRenderer().setNumHorizontalLabels(5);
        readingPlot.getGridLabelRenderer().setHumanRounding(false);
    }

    public void setRiskLevel(double reading) {
        if (reading < 500.00) {
            tv_risk.setText("Risk: LOW");
        } else if (reading >= 500.0 && reading < 800.0) {
            tv_risk.setText("Risk: MEDIUM");
        } else if (reading >= 800 && reading < 900) {
            tv_risk.setText("Risk: HIGH!");
        } else {
            tv_risk.setText("Risk: VERY HIGH!!!!!");
        }

    }

    public void saveLocation()
    {
        GPStracker g = new GPStracker(AlcoholActivity.this);
        g.getLocation();
        Location l = g.getMyLocation();
        if (l != null) {
            double lat = l.getLatitude();
            double lon = l.getLongitude();
            myLocation = GPSLocation.getInstance();
            myLocation.setLat(String.valueOf(lat));
            myLocation.setLon(String.valueOf(lon));
        }else{
            Toast.makeText(this, "GPS Location is null", Toast.LENGTH_SHORT).show();
        }
    }

    //Test the alcohol level and perform the corresponding operation
    public void testAlcoholLevel() {
        if (currtest > ableToDrive && currtest < veryDrunk) {
            Intent i = new Intent(getApplicationContext(), PhoneCallPop.class);
            startActivity(i);

        } else if (currtest > veryDrunk) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

                if (checkSelfPermission(Manifest.permission.SEND_SMS)
                        == PackageManager.PERMISSION_DENIED) {

                    Log.d("permission", "permission denied to SEND_SMS - requesting it");
                    String[] permissions = {Manifest.permission.SEND_SMS};

                    requestPermissions(permissions, PERMISSION_REQUEST_CODE);

                }
            }
            SmsManager.getDefault().sendTextMessage(contactNumber, null
                    , "Hello "+contactName + ", My alcohol concentration is at: "+currtest+". Please come pick me up at: Longitude:"
                            + myLocation.getLon() + ", Latitude: "
                            + myLocation.getLat() + "!", null, null);
        }
    }


    //get the max result every 8s and save it to database
    private static int counter = 0;
    private double maxReading = 0;

    private void getMaxData(String reading) {
        if (counter < 8) {
            double thisReading = Double.parseDouble(reading);
            if (thisReading > maxReading) {
                maxReading = thisReading;
            }
        } else {
            counter = 0;
            if (results.size() == 8)
                results.remove(0);

            results.add(new TestResult(getCurrentTime(), String.valueOf(maxReading), "Alcohol"));
            fireBaseDBHelper.saveAlcoholReadingToUser(fireBaseAuthHelper.getCurrentUser().getUid(), results);
        }
        counter++;
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(testButtonPressed) {
                String receivedMsg = intent.getStringExtra("receivedMessage");
                Log.d("AlcoholActivity", receivedMsg);
                recDataString.append(receivedMsg);
                int endOfLineIndex = recDataString.indexOf("~");                    // determine the end-of-line
                if (endOfLineIndex > 0) {                                           // make sure there data before ~
                    int AlcoholReadingStartingPointIndex = recDataString.indexOf("#");
                    String alcoholReading = recDataString.substring(AlcoholReadingStartingPointIndex, endOfLineIndex);    // extract string
                    int dataLength = alcoholReading.length();                          //get length of data received
                    if (alcoholReading.charAt(0) == '#')                             //if it starts with # we know it is what we are looking for
                    {
                        String alcoholReadingInString = alcoholReading.substring(1, dataLength);
                        double reading = Double.parseDouble(alcoholReadingInString);
                        getMaxData(alcoholReadingInString);
                        currtest = reading;
                        testAlcoholLevel();
                    }
                    recDataString.delete(0, recDataString.length());                    //clear all string data
                }
            }
        }
    };
}


