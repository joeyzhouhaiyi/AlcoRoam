package com.elec390coen.alcoroam.Activities.Main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.elec390coen.alcoroam.Activities.Setting.SettingActivity;
import com.elec390coen.alcoroam.R;

public class MainActivity extends AppCompatActivity {

    Button btn_setting;
    Button btn_alcohol;
    Button btn_hr;
    Button btn_bluetooth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.style_activity_main);

        btn_setting = findViewById(R.id.btn_setting);
        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
            }
        });
    }
}
