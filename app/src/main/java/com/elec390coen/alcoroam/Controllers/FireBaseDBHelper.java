package com.elec390coen.alcoroam.Controllers;

import android.support.annotation.NonNull;

import com.elec390coen.alcoroam.Models.TestResult;
import com.elec390coen.alcoroam.Models.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FireBaseDBHelper {

    FirebaseDatabase database;

    public FireBaseDBHelper()
    {
        database = FirebaseDatabase.getInstance();
    }

    public void addNewUser(String id, String name,String email,String password)
    {
        DatabaseReference ref = database.getReference("Users");
        User newUSer = new User(id,name,email,password);
        ref.child(id).setValue(newUSer);
    }

    public void saveAlcoholReadingToUser(String userId, List<TestResult> results)
    {
        DatabaseReference ref = database.getReference("Users");
        ref.child(userId).child("AlcoholReadings").setValue(results);
    }

    public void fetchUserAlcoholReading(FirebaseUser user, final List<TestResult> resultList)
    {
        database.getReference("Users").child(user.getUid()).child("AlcoholReadings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot readingSnapshot: dataSnapshot.getChildren()) {
                    TestResult t = readingSnapshot.getValue(TestResult.class);
                    resultList.add(t);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    //get database reference of user
    public DatabaseReference getUserRef()
    {
        return database.getReference("Users");
    }

    //get database reference with specific user id
    public DatabaseReference getUserRefWithId(String id)
    {
        return database.getReference("Users").child(id);
    }


    //save the alcohol reading to server
    public void saveAlcoholTestResult(String reading)
    {
        DatabaseReference ref = database.getReference("AlcoholReading");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM hh:mm:ss");
        String currentTimeStamp = simpleDateFormat.format(new Date());
        String type = "alcohol";
        TestResult tr = new TestResult(currentTimeStamp,reading,type);
        ref.child(currentTimeStamp).setValue(tr);
    }

    //save the hr reading to server
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
