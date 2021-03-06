package com.elec390coen.alcoroam.Activities.Login;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CheckBox;
import android.content.SharedPreferences;


import com.elec390coen.alcoroam.Activities.Main.MainActivity;
import com.elec390coen.alcoroam.Controllers.FireBaseAuthHelper;
import com.elec390coen.alcoroam.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class LoginActivity extends AppCompatActivity {

    private EditText et_email;
    private EditText et_pass;
    private Button btn_login;
    private Button btn_signup;
    private TextView tv_error;
    private FireBaseAuthHelper fireBaseAuthHelper;
    private CheckBox cb_savelogin;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.style_activity_login);
        initView();
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = et_email.getText().toString();
                String password = et_pass.getText().toString();
                if(cb_savelogin.isChecked()){
                    loginPrefsEditor.putBoolean("saveLogin",true);
                    loginPrefsEditor.putString("email",email);
                    loginPrefsEditor.putString("password",password);
                    loginPrefsEditor.commit();
                }else {
                    loginPrefsEditor.clear();
                    loginPrefsEditor.commit();
                }
                if(email.isEmpty() || password.isEmpty())
                {
                    tv_error.setText("*field cannot be empty");
                }else
                {
                    findViewById(R.id.loading).setVisibility(View.VISIBLE);
                    fireBaseAuthHelper.getAuth().signInWithEmailAndPassword(email,password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    findViewById(R.id.loading).setVisibility(View.GONE);
                                    if(task.isSuccessful())
                                    {
                                        //fireBaseAuthHelper.setCurrentUser(fireBaseAuthHelper.getAuth().getCurrentUser());
                                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                        finish();
                                    }else
                                    {
                                        tv_error.setText(task.getException().getMessage());
                                    }
                                }
                            });
                }
            }
        });

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SignUpDialog(LoginActivity.this).show();
            }
        });
    }

    private void initView()
    {
        fireBaseAuthHelper = new FireBaseAuthHelper();
        et_email = findViewById(R.id.et_email);
        et_pass = findViewById(R.id.et_password);
        btn_login = findViewById(R.id.btn_signin);
        btn_signup = findViewById(R.id.btn_signup);
        tv_error = findViewById(R.id.tv_login_error);
        cb_savelogin=findViewById(R.id.cb_savelogin);
        loginPreferences= getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor= loginPreferences.edit();

        saveLogin= loginPreferences.getBoolean("saveLogin",false);
        if(saveLogin == true){
            et_email.setText(loginPreferences.getString("email",""));
            et_pass.setText(loginPreferences.getString("password",""));
            cb_savelogin.setChecked(true);
        }
    }


}
