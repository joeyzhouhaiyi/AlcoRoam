package com.elec390coen.alcoroam.Activities.Alcohol;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.elec390coen.alcoroam.R;

public class PopActivity extends Activity {

    Button homeBTN;
    Button taxiBTN;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.style_activity_pop);

        homeBTN = findViewById(R.id.btn_home);
        taxiBTN = findViewById(R.id.btn_taxi);

        homeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call home
            }
        });

        taxiBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call taxi
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

    }
}






