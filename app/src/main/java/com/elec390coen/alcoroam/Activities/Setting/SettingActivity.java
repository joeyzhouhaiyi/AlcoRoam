package com.elec390coen.alcoroam.Activities.Setting;

import android.Manifest;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.elec390coen.alcoroam.Controllers.FireBaseAuthHelper;
import com.elec390coen.alcoroam.Controllers.FireBaseDBHelper;
import com.elec390coen.alcoroam.Models.GPSLocation;
import com.elec390coen.alcoroam.Models.User;
import com.elec390coen.alcoroam.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class SettingActivity extends AppCompatActivity {


    TextView textViewPhone;
    TextView textViewName;
    TextView textViewEmail;
    Switch GPS_switch;
    TextView textViewLat;
    TextView textViewLon;


    Button contactInfoBTN;

    FirebaseUser currentUser;
    FireBaseAuthHelper authHelper;
    FireBaseDBHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.style_activity_setting);

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},123);
        initView();
        getCurrentUserInfo();
        textViewLat = findViewById(R.id.GPS_latView);
        textViewLon = findViewById(R.id.GPS_lonView);
        GPS_switch = findViewById(R.id.GPSswitch);
        GPS_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true){
                    Toast.makeText(SettingActivity.this, "GPS Activated", Toast.LENGTH_SHORT).show();
                    GPStracker g = new GPStracker(SettingActivity.this);
                    g.getLocation();
                    Location l = g.getMyLocation();
                    if (l != null){
                        double lat = l.getLatitude();
                        double lon = l.getLongitude();
                        GPSLocation myLocation = GPSLocation.getInstance();
                        myLocation.setLat(String.valueOf(lat));
                        myLocation.setLon(String.valueOf(lon));
                        textViewLat.setText(Double.toString(lat));
                        textViewLon.setText(Double.toString(lon));
                        //Toast.makeText(getApplicationContext(), "LAT:" +lat+"\n LON:" +lon, Toast.LENGTH_SHORT).show();
                    }else
                    {
                        Toast.makeText(SettingActivity.this, "Location is null", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    Toast.makeText(SettingActivity.this, "GPS Deactivated", Toast.LENGTH_SHORT).show();
                }
            }
        });
        contactInfoBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
          }
        });


    }

    public void openDialog() {
        new InfoDialog(this).show();

    }

    private void getCurrentUserInfo()
    {
        String id = currentUser.getUid();

        dbHelper.getUserRefWithId(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User thisUser = dataSnapshot.getValue(User.class);
                if(thisUser!=null)
                {
                    String contactName = thisUser.getEmergencyContactName();
                    String contactEmail = thisUser.getEmergencyContactEmail();
                    String contactPhone = thisUser.getEmergencyContactNumber();
                    textViewName.setText(contactName);
                    textViewEmail.setText(contactEmail);
                    textViewPhone.setText(contactPhone);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SettingActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initView()
    {
        textViewEmail = findViewById(R.id.txtVEmail);
        textViewName = findViewById(R.id.txtVName);
        textViewPhone = findViewById(R.id.txtVPhone);
        contactInfoBTN = findViewById(R.id.contactInfo_btn);
        dbHelper = new FireBaseDBHelper();
        authHelper = new FireBaseAuthHelper();
        currentUser = authHelper.getCurrentUser();

    }

}