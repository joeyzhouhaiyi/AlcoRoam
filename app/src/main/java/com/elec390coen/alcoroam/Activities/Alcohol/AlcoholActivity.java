package com.elec390coen.alcoroam.Activities.Alcohol;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
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
import com.elec390coen.alcoroam.Activities.Setting.SettingActivity;
import com.elec390coen.alcoroam.Controllers.FireBaseAuthHelper;
import com.elec390coen.alcoroam.Controllers.FireBaseDBHelper;
import com.elec390coen.alcoroam.Models.GPSLocation;
import com.elec390coen.alcoroam.Models.TestResult;
import com.elec390coen.alcoroam.R;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class AlcoholActivity extends AppCompatActivity {
    TextView viewData;
    TextView tv_risk;
    ProgressBar pb_alcohol_level;
    Button record;

    PointsGraphSeries series;
    Button btn_refresh_connection;
    ShowcaseView showcaseView;

    private StringBuilder recDataString = new StringBuilder();
    private static final int PERMISSION_REQUEST_CODE = 1;
    GraphView readingPlot;
    List<TestResult> results = new ArrayList<>();
    FireBaseDBHelper fireBaseDBHelper;
    FireBaseAuthHelper fireBaseAuthHelper;
    GPSLocation myLocation;
    StaticLabelsFormatter staticLabelsFormatter;

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
        saveLocation();
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("messageIntent"));
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_DENIED) {

                Log.d("permission", "permission denied to SEND_SMS - requesting it");
                String[] permissions = {Manifest.permission.CALL_PHONE};

                requestPermissions(permissions, PERMISSION_REQUEST_CODE);

            }
        }

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
                        pb_alcohol_level.setProgress((int) (maxReading*1000));
                        setRiskLevel(maxReading);
                        viewData.setText(String.valueOf(maxReading) + "mg/dL");    //update the textviews with sensor values
                        currtest = maxReading;
                        maxReading=0;
                        UpdateGraph();
                        testAlcoholLevel();
                    }
                }, 8000);
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

                readingPlot.removeAllSeries();
                List<Date> xlabel = new ArrayList();
                DataPoint[] dp = new DataPoint[(int)dataSnapshot.getChildrenCount()];
                results.clear();
                SimpleDateFormat formater = new SimpleDateFormat("dd/MM hh:mm:ss");
                for (DataSnapshot readingSnapshot: dataSnapshot.getChildren()) {
                    TestResult t = readingSnapshot.getValue(TestResult.class);
                    results.add(t);
                    String time = t.getTime();


                    try {
                        Date x = formater.parse(time);
                        xlabel.add(x);
                        double y = Double.parseDouble(t.getReading());
                        dp[index] = new DataPoint(x,y);
                        index++;
                    }catch (Exception ex)
                    {
                        Log.d("TAG",ex.getLocalizedMessage());
                    }
                }

                if(xlabel.size()!=0)
                {
                    readingPlot.getViewport().setXAxisBoundsManual(true);
                    readingPlot.getViewport().setMinX(xlabel.get(0).getTime());
                    readingPlot.getViewport().setMaxX(xlabel.get(xlabel.size()-1).getTime());
                }
                series = new PointsGraphSeries<>(dp);
                series.setOnDataPointTapListener(new OnDataPointTapListener() {
                    @Override
                    public void onTap(Series series, DataPointInterface dataPoint) {
                        SimpleDateFormat formater = new SimpleDateFormat("dd/MM hh:mm:ss");
                        try
                        {
                            Date d = new Date((long) dataPoint.getX());
                            String currentTimeStamp = formater.format(d.getTime());
                            Toast.makeText(AlcoholActivity.this,currentTimeStamp , Toast.LENGTH_SHORT).show();
                        }catch (Exception e){}


                    }
                });
                readingPlot.addSeries(series);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private String getCurrentTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM hh:mm:ss");
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
        readingPlot.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(AlcoholActivity.this,new SimpleDateFormat("dd/MM\nhh:mm:ss")));
        readingPlot.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        readingPlot.getGridLabelRenderer().setHumanRounding(false,true);
        readingPlot.getViewport().setYAxisBoundsManual(true);
        readingPlot.getViewport().setMinY(0);
        readingPlot.getViewport().setMaxY(0.1);
        readingPlot.getViewport().scrollToEnd();

    }

    public void setRiskLevel(double reading) {
        if (reading < 0.05) {
            tv_risk.setText("Risk: LOW");
        } else if (reading >= 0.05 && reading < 0.08) {
            tv_risk.setTextColor(Color.YELLOW);
            tv_risk.setText("Risk: MEDIUM");
        } else if (reading >= 0.08 && reading < 0.09) {
            tv_risk.setTextColor(Color.RED);
            tv_risk.setText("Risk: HIGH!");
        } else {
            tv_risk.setTextColor(Color.BLACK);
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
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(AlcoholActivity.this, Locale.getDefault());

            try{
                addresses = geocoder.getFromLocation(lat, lon, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                myLocation.setLastSeen(address);
            }catch (Exception ex){
                Toast.makeText(AlcoholActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
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

            SmsManager.getDefault().sendTextMessage(contactNumber, null
                    , "Hello "+contactName + ", my alcohol level is at: "+currtest+"mg/dL and I am not allowed to drive.(1/2)", null, null);
            SmsManager.getDefault().sendTextMessage(contactNumber, null
                    , "Can you please come pick me up at: "
                            + myLocation.getLastSeen()+"? (2/2)", null, null);
        }
    }


    //get the max result every 4s and save it to database
    private static int counter = 0;
    private double maxReading = 0;

    private void getMaxData(String reading) {
        if (counter < 4) {
            double thisReading = Double.parseDouble(reading);
            if (thisReading > maxReading) {
                maxReading = thisReading;
            }
        } else {
            counter = 0;
            if (results.size() == 4)
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
                    String alcoholReading="";
                    if(AlcoholReadingStartingPointIndex>=0)
                    alcoholReading = recDataString.substring(AlcoholReadingStartingPointIndex, endOfLineIndex);    // extract string
                    int dataLength = alcoholReading.length();
                    if(alcoholReading.length()!=0)
                    {
                        //get length of data received
                        if (alcoholReading.charAt(0) == '#')                             //if it starts with # we know it is what we are looking for
                        {
                            String alcoholReadingInString = alcoholReading.substring(1, dataLength);
                            double reading = Double.parseDouble(alcoholReadingInString);
                            reading/=10000;
                            getMaxData(String.valueOf(reading));
                            currtest = reading;
                        }
                    }

                    recDataString.delete(0, recDataString.length());                    //clear all string data
                }
            }
        }
    };

    private boolean playTutorial()
    {
        SharedPreferences preferences = getSharedPreferences("LoginInfo",0);
        boolean play = preferences.getBoolean("tut4",false);
        preferences.edit().putBoolean("tut4",false).apply();
        return play;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(playTutorial())
        {
            showcaseView = new ShowcaseView.Builder(this)
                    .setTarget(new ViewTarget(R.id.recordAlco,this))
                    .setContentTitle("Test Result")
                    .setContentText("Press here to start testing your alcohol concentration, and don't forget to hold down the button on AlcoBox while testing!")
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
                .setTarget(new ViewTarget(R.id.btn_refresh_connection,this))
                .setContentTitle("Reconnection")
                .setContentText("In case your bluetooth device is disconnected, press here to reconnect to it.")
                .withHoloShowcase()
                .setStyle(R.style.ShowcaseView_custom)
                .blockAllTouches()
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showcaseView.hide();
                    }
                })
                .build();
    }
}


