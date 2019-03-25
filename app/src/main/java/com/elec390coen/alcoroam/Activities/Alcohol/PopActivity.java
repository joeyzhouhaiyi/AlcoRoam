package com.elec390coen.alcoroam.Activities.Alcohol;

import android.app.Activity;
import android.net.Uri;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.content.Intent;

import com.elec390coen.alcoroam.R;

import java.util.ArrayList;
import java.util.List;

public class PopActivity extends Activity {

    Button homeBTN;
    Button taxiBTN;


    ListView lv_taxi;
    List<String> taxinumberList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.style_activity_pop);

        homeBTN = findViewById(R.id.btn_home);
        taxiBTN = findViewById(R.id.btn_taxi);

        homeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = getString(R.string.contactnumber);
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" +number));
                startActivity(intent);
            }
        });

        taxiBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number2 = "5144001005";
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" +number2));
                startActivity(intent);
            }
        });



        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*.6), (int)(height*.4));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        getWindow().setAttributes(params);

        lv_taxi = findViewById (R.id.taxiListView);
        taxinumberList = new ArrayList<> ();

        taxinumberList.add ("OnCads Montreal\n514-400-1005");
        taxinumberList.add ("Taxi Boonjour Montreal\n514-370-8777");
        taxinumberList.add("Taxi Dianmond\n514-273-6331");
        taxinumberList.add("Atlas Taxi\n514-485-8585");
        taxinumberList.add("Taxi Champlian\n514-271-1111");
        taxinumberList.add("Taxi Coop\n514-725-9885");
        taxinumberList.add("Taxi Classe Affaires Montreal/n514-575-0220");
        taxinumberList.add("Hypra Taxi\n514-312-3003");
        taxinumberList.add("Angrignon Taxi Inc\n514-762-1000");
        taxinumberList.add("Westmount Taxi Ltee\n514-484-2604");
        taxinumberList.add("Amical Taxi\n514-271-252");
        taxinumberList.add("Taxi Pontiac Hemlock\n514-766-8294");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<> (PopActivity.this,android.R.layout.simple_list_item_1, taxinumberList);
        lv_taxi.setAdapter (arrayAdapter);
    }
}






