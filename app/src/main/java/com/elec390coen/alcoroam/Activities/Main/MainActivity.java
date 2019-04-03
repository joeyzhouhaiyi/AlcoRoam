package com.elec390coen.alcoroam.Activities.Main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.elec390coen.alcoroam.Activities.Alcohol.AlcoholActivity;
import com.elec390coen.alcoroam.Activities.HeartRate.HeartActivity;
import com.elec390coen.alcoroam.Activities.Login.LoginActivity;
import com.elec390coen.alcoroam.Activities.Setting.SettingActivity;
import com.elec390coen.alcoroam.Activities.bluetooth.BluetoothActivity;
import com.elec390coen.alcoroam.Controllers.FireBaseDBHelper;
import com.elec390coen.alcoroam.Models.TestResult;
import com.elec390coen.alcoroam.Models.User;
import com.elec390coen.alcoroam.R;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button btn_setting;
    Button btn_alcohol;
    Button btn_hr;
    Button btn_bluetooth;
    Button btn_logout;
    Button btn_debug;
    Button btn_display;
    TextView tv_welcome;
    User thisUser;
    private FirebaseAuth firebaseAuth;
    private FireBaseDBHelper fireBaseDBHelper;
    ListView lv_readings;

    List<TestResult> results = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.style_activity_main);
        fireBaseDBHelper = new FireBaseDBHelper();


        /*lv_readings = findViewById(R.id.lv_reading);

        //debug
        btn_debug = findViewById(R.id.btn_debug);
        btn_debug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fireBaseDBHelper.fetchUserAlcoholReading(firebaseAuth.getCurrentUser(),results);
                Toast.makeText(MainActivity.this, "Fetch Complete", Toast.LENGTH_SHORT).show();
            }
        });
        /*
        btn_display = findViewById(R.id.btn_display);
        btn_display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processData(results);
            }
        });*/

        //settings
        btn_setting = findViewById(R.id.btn_setting);
        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
            }
        });



        //alcohol
        btn_alcohol = findViewById(R.id.btn_alco);
        btn_alcohol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, AlcoholActivity.class);
                i.putExtra("name",thisUser.getEmergencyContactName());
                i.putExtra("number",thisUser.getEmergencyContactNumber());
                startActivity(i);
            }
        });


        //heart rate
        btn_hr = findViewById(R.id.btn_hrate);
        btn_hr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, HeartActivity.class));
            }
        });



        //logout
        firebaseAuth = FirebaseAuth.getInstance();
        btn_logout= (Button)findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
            }
        });

        //welcome
        tv_welcome = findViewById(R.id.tv_welcome);
        fireBaseDBHelper.getUserRefWithId(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                thisUser = dataSnapshot.getValue(User.class);
                tv_welcome.setText("Welcome, "+thisUser.getName() +"!");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        if(playTutorial())
        {
            Button bt = new Button(this);
            bt.setBackgroundColor(Color.TRANSPARENT);
            bt.setText("");
            bt.setEnabled(false);
            new ShowcaseView.Builder(this)
                    .setTarget(new ViewTarget(R.id.btn_setting, this))
                    .setContentTitle("Welcome to AlcoRoam!")
                    .setContentText("Please click on the setting button as I will show you how to set up your devices.")
                    .withHoloShowcase()
                    .replaceEndButton(bt)
                    .setStyle(R.style.ShowcaseView_custom)
                    .build();
        }

    }

    private boolean playTutorial()
    {
        SharedPreferences preferences = getSharedPreferences("LoginInfo",0);
        boolean play = preferences.getBoolean("playTutorial",false);
        return play;
    }

}
