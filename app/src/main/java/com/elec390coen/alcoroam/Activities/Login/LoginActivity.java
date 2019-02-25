package com.elec390coen.alcoroam.Activities.Login;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.elec390coen.alcoroam.R;

public class LoginActivity extends AppCompatActivity {

    private EditText et_name;
    private EditText et_pass;
    private Button btn_login;
    private Button btn_signup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.style_activity_login);
    }

    private void initView()
    {
        /*
        et_name = findViewById(R.id.et_name);
        et_pass = findViewById(R.id.et_pass);
        btn_login = findViewById(R.id.btn_login);
        btn_signup = findViewById(R.id.btn_signup);
        */
    }
}
