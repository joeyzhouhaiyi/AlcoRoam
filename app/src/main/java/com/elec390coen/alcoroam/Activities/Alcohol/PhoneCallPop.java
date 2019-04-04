package com.elec390coen.alcoroam.Activities.Alcohol;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ListView;
import android.content.Intent;

import com.elec390coen.alcoroam.Controllers.FireBaseAuthHelper;
import com.elec390coen.alcoroam.Controllers.FireBaseDBHelper;
import com.elec390coen.alcoroam.Models.User;
import com.elec390coen.alcoroam.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PhoneCallPop extends Activity {

    Button homeBTN;
    String phoneNumber;
    FireBaseDBHelper fireBaseDBHelper;
    FireBaseAuthHelper fireBaseAuthHelper;
    ListView lv_taxi;
    List<String> taxinumberList;
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.style_activity_pop);
        fireBaseDBHelper = new FireBaseDBHelper();
        fireBaseAuthHelper = new FireBaseAuthHelper();
        fireBaseDBHelper.getUserRefWithId(fireBaseAuthHelper.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User thisUser = dataSnapshot.getValue(User.class);
                phoneNumber = thisUser.getEmergencyContactNumber();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        homeBTN = findViewById(R.id.btn_home);

        homeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String number = phoneNumber;
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" +number));
                startActivity(intent);
            }
        });




        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        //getWindow().setLayout((int)(width*.8), (int)(height*.8));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        getWindow().setAttributes(params);

        lv_taxi = findViewById (R.id.lv_taxi);
        taxinumberList = new ArrayList<> ();

        taxinumberList.add ("OnCads Montreal\n5144001005");
        taxinumberList.add ("Taxi Boonjour Montreal\n5143708777");
        taxinumberList.add("Taxi Dianmond\n5142736331");
        taxinumberList.add("Atlas Taxi\n5144858585");
        taxinumberList.add("Taxi Champlian\n5142711111");
        taxinumberList.add("Taxi Coop\n5147259885");
        taxinumberList.add("Taxi Classe Affaires Montreal\n5145750220");
        taxinumberList.add("Hypra Taxi\n5143123003");
        taxinumberList.add("Angrignon Taxi Inc\n5147621000");
        taxinumberList.add("Westmount Taxi Ltee\n5144842604");
        taxinumberList.add("Amical Taxi\n514271252");
        taxinumberList.add("Taxi Pontiac Hemlock\n5147668294");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<> (PhoneCallPop.this,R.layout.item_listview_taxi, taxinumberList);
        lv_taxi.setAdapter (arrayAdapter);
        lv_taxi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                String[] strings = selectedItem.split("\n");
                String number = strings[1];
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

                    if (checkSelfPermission(Manifest.permission.CALL_PHONE)
                            == PackageManager.PERMISSION_DENIED) {

                        Log.d("permission", "permission denied to SEND_SMS - requesting it");
                        String[] permissions = {Manifest.permission.CALL_PHONE};

                        requestPermissions(permissions, PERMISSION_REQUEST_CODE);

                    }
                }
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" +number));
                startActivity(intent);
            }
        });
    }
}






