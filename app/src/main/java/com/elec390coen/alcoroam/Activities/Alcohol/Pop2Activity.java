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

public class Pop2Activity extends Activity {

    Button textHome;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.style_activity_pop2);

        textHome = findViewById(R.id.textHome_btn);

        textHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call home
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
