package com.elec390coen.alcoroam.Activities.Setting;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.elec390coen.alcoroam.Controllers.FireBaseAuthHelper;
import com.elec390coen.alcoroam.Controllers.FireBaseDBHelper;
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


    Button contactInfoBTN;

    FirebaseUser currentUser;
    FireBaseAuthHelper authHelper;
    FireBaseDBHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.style_activity_setting);
        initView();
        getCurrentUserInfo();
//        //initialize views
//        phoneNumber = findViewById(R.id.contactPhone);
//        email = findViewById(R.id.contactEmail);
//        name = findViewById(R.id.contactName);
//
//        saveBTN = findViewById(R.id.saveBtn);
//        fireBaseHelper = new FireBaseHelper();
//        //retrieve the data from SharedPreferences
//
//        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
//        String phoneString = pref.getString("phone", "");
//        phoneNumber.setText(phoneString);
//
//        String emailString = pref.getString("email", "");
//        email.setText(emailString);
//
//        String nameString = pref.getString("name", "");
//        name.setText(nameString);
//
//
//
//        //handle click
//        saveBTN.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                phoneTxt = phoneNumber.getText().toString().trim();
//                emailTxt = email.getText().toString().trim();
//                nameTxt = name.getText().toString();
//
//                if(phoneTxt.isEmpty() || emailTxt.isEmpty() || nameTxt.isEmpty()){
//                    Toast.makeText(SettingActivity.this, "Please enter complete information", Toast.LENGTH_SHORT).show();
//                }
//                else{

//                    SharedPreferences pref = getSharedPreferences(fireBaseHelper.getCurrentUser().getUid(),MODE_PRIVATE);
//                    SharedPreferences.Editor editor = pref.edit();
//
//                    editor.putString("phone", phoneTxt);
//                    editor.putString("email", emailTxt);
//                    editor.putString("name", nameTxt);
//                    editor.apply();
//                    Toast.makeText(SettingActivity.this, "Saved", Toast.LENGTH_SHORT).show();
//
//                }
//
//            }
//        });
//
//
//    }
//
//}




        /*
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SettingActivity.this);

        textViewPhone.setText("Phone Number: "+sharedPreferences.getString("phone",""));
        textViewEmail.setText("Email: "+sharedPreferences.getString("email",""));
        textViewName.setText("Name: "+sharedPreferences.getString("name",""));

        */
        contactInfoBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
          }
        });



        //sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

    }
    /*
    SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("phone")){
                textViewPhone.setText("Phone Number: "+sharedPreferences.getString(key,"defaultPhone"));
                // Write your code here
            }
            if(key.equals("email")){
                textViewEmail.setText("Email: "+sharedPreferences.getString(key,"defaultEmail"));
            }
            if(key.equals("name")){
                textViewName.setText("Name: "+sharedPreferences.getString(key,"defaultName"));
            }
        }
    };
    */
    public void openDialog() {
        new InfoDialog(this).show();
        //infoDialog.show(getSupportFragmentManager(), "info dialog");
    }
/*
    @Override
    public void applyTexts(String phoneTxt, String nameTxt, String emailTxt) {
            textViewPhone.setText(phoneTxt);
            textViewName.setText(nameTxt);
            textViewEmail.setText(emailTxt);
    }
    */
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
                /*
                for(DataSnapshot user : dataSnapshot.getChildren()){

                    User thisUser = user.getValue(User.class);
                    //add each contact to an arraylist and use it to populate a list of contacts
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
                */
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
/**
 * F
 * I
 * N
 * I
 * S
 * H
 * !
 *
 * 666666
 * 88
 * im going
 *
 * */
}