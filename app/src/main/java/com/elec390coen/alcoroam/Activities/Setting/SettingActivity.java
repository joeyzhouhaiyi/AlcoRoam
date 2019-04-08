package com.elec390coen.alcoroam.Activities.Setting;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
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

import com.elec390coen.alcoroam.Activities.Main.MainActivity;
import com.elec390coen.alcoroam.Activities.bluetooth.BluetoothActivity;
import com.elec390coen.alcoroam.Controllers.FireBaseAuthHelper;
import com.elec390coen.alcoroam.Controllers.FireBaseDBHelper;
import com.elec390coen.alcoroam.Models.GPSLocation;
import com.elec390coen.alcoroam.Models.User;
import com.elec390coen.alcoroam.R;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Locale;

public class SettingActivity extends AppCompatActivity {


    TextView textViewPhone;
    TextView textViewName;
    TextView textViewEmail;
    Switch GPS_switch;
    TextView tv_location;
    Button btn_bluetooth;
    ShowcaseView showcaseView;

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
        tv_location = findViewById(R.id.tv_location);
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
                        Geocoder geocoder;
                        List<Address> addresses;
                        geocoder = new Geocoder(SettingActivity.this, Locale.getDefault());

                        try{
                            addresses = geocoder.getFromLocation(lat, lon, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                            myLocation.setLastSeen(address);
                            tv_location.setText(address);
                        }catch (Exception ex){
                            Toast.makeText(SettingActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }else
                    {
                        Toast.makeText(SettingActivity.this, "Location is null", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    Toast.makeText(SettingActivity.this, "GPS Disabled", Toast.LENGTH_SHORT).show();
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


    @Override
    protected void onResume() {
        super.onResume();
        if(playTutorial())
        {
            showcaseView = new ShowcaseView.Builder(this)
                    .setTarget(new ViewTarget(R.id.contactInfo_btn,this))
                    .setContentTitle("Emergency Contact")
                    .setContentText("Here you can save your emergency contact's information. This is very important!")
                    .withHoloShowcase()
                    .setStyle(R.style.ShowcaseView_custom)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showcaseView.hide();
                            NextTutorial1();
                        }
                    })
                    .build();

        }
    }
    private void NextTutorial1() {
        showcaseView = new ShowcaseView.Builder(this)
                .setTarget(new ViewTarget(R.id.GPSswitch, this))
                .setContentTitle("GPS")
                .setContentText("Switch your GPS on so your friend can find you!")
                .withHoloShowcase()
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showcaseView.hide();
                        NextTutorial2();
                    }
                })
                .setStyle(R.style.ShowcaseView_custom)
                .build();
    }

    private void NextTutorial2() {
        showcaseView = new ShowcaseView.Builder(this)
                .setTarget(new ViewTarget(R.id.btn_bt, this))
                .setContentTitle("Setup Bluetooth")
                .setContentText("Click here to setup your bluetooth connection.")
                .withHoloShowcase()
                .setStyle(R.style.ShowcaseView_custom)
                .build();
        showcaseView.forceTextPosition(ShowcaseView.ABOVE_SHOWCASE);
        showcaseView.hideButton();
    }

    public void openDialog() {
        InfoDialog info = new InfoDialog(this);
        info.setCanceledOnTouchOutside(false);
        info.setCancelable(false);
        info.show();

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
                    textViewName.setText("  "+contactName);
                    textViewEmail.setText("  "+contactEmail);
                    textViewPhone.setText("  "+contactPhone);
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
        //bluetooth
        btn_bluetooth = findViewById(R.id.btn_bt);
        btn_bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showcaseView!=null)
                showcaseView.hide();
                startActivity(new Intent(SettingActivity.this, BluetoothActivity.class));
            }
        });

    }

    private boolean playTutorial()
    {
        SharedPreferences preferences = getSharedPreferences("LoginInfo",0);
        boolean play = preferences.getBoolean("tut2",false);
        preferences.edit().putBoolean("tut2",false).apply();
        return play;
    }

}