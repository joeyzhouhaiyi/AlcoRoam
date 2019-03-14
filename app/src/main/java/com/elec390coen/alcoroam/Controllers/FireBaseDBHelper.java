package com.elec390coen.alcoroam.Controllers;

import com.elec390coen.alcoroam.Models.TestResult;
import com.elec390coen.alcoroam.Models.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FireBaseDBHelper {

    FirebaseDatabase database;

    public FireBaseDBHelper()
    {
        database = FirebaseDatabase.getInstance();
    }

    public void addNewUser(String name,String email,String password)
    {
        DatabaseReference ref = database.getReference("Users");

        String id = ref.push().getKey();
        User newUSer = new User(id,name,email,password);
        ref.child(id).setValue(newUSer);
    }

    public void saveAlcoholTestResult(String reading)
    {
        DatabaseReference ref = database.getReference("AlcoholReading");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
        String currentTimeStamp = simpleDateFormat.format(new Date());
        String type = "alcohol";
        TestResult tr = new TestResult(currentTimeStamp,reading,type);
        ref.child(currentTimeStamp).setValue(tr);
    }

    public void saveHeartRateTestResult(String reading)
    {
        DatabaseReference ref = database.getReference("hrReading");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
        String currentTimeStamp = simpleDateFormat.format(new Date());
        String type = "heart";
        TestResult tr = new TestResult(currentTimeStamp,reading,type);
        ref.child(currentTimeStamp).setValue(tr);
    }
}
