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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.style_activity_login);
        initView();
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = et_email.getText().toString();
                String pass = et_pass.getText().toString();
                if(email.isEmpty() || pass.isEmpty())
                {
                    tv_error.setText("*field cannot be empty");
                }else
                {
                    findViewById(R.id.loading).setVisibility(View.VISIBLE);
                    fireBaseAuthHelper.getAuth().signInWithEmailAndPassword(email,pass)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    findViewById(R.id.loading).setVisibility(View.GONE);
                                    if(task.isSuccessful())
                                    {
                                        fireBaseAuthHelper.setCurrentUser(fireBaseAuthHelper.getAuth().getCurrentUser());
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
    }


}
