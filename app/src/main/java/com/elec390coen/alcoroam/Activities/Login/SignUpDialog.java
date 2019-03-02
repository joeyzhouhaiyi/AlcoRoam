package com.elec390coen.alcoroam.Activities.Login;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.elec390coen.alcoroam.Activities.Main.MainActivity;
import com.elec390coen.alcoroam.Controllers.FireBaseHelper;
import com.elec390coen.alcoroam.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import org.w3c.dom.Text;

public class SignUpDialog extends Dialog {

    private EditText et_dialog_email;
    private EditText et_dialog_password;
    private EditText et_dialog_password_re;
    private Button btn_dialog_signup;
    private FireBaseHelper fireBaseHelper;
    private TextView tv_error;

    public SignUpDialog(Context context)
    {
        super(context,R.style.MyDialog);
        if(context instanceof LoginActivity)
        {
            setOwnerActivity((LoginActivity)context );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.style_dialog_signup);
        setCanceledOnTouchOutside(true);
        initView();
        btn_dialog_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = et_dialog_email.getText().toString();
                String password = et_dialog_password.getText().toString();
                if(email.isEmpty() || password.isEmpty())
                {
                    tv_error.setText("*field cannot be empty");
                }else
                {
                    if(password.equals(et_dialog_password_re.getText().toString()))
                    {
                        fireBaseHelper.getAuth().createUserWithEmailAndPassword(email,password)
                                .addOnCompleteListener(getOwnerActivity(), new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        findViewById(R.id.dialog_loading);
                                        if(task.isSuccessful())
                                        {
                                            fireBaseHelper.setCurrentUser(fireBaseHelper.getAuth().getCurrentUser());
                                            dismiss();
                                            getContext().startActivity(new Intent(getOwnerActivity(),MainActivity.class));
                                        }else
                                        {
                                            tv_error.setText(task.getException().getMessage());
                                        }
                                    }
                                });
                    }else
                    {
                        tv_error.setText("Password does not match");
                    }
                }
                findViewById(R.id.dialog_loading).setVisibility(View.VISIBLE);
            }
        });
    }

    private void initView()
    {
        et_dialog_email = findViewById(R.id.et_dialog_email);
        et_dialog_password = findViewById(R.id.et_dialog_password);
        et_dialog_password_re = findViewById(R.id.et_dialog_password_re);
        btn_dialog_signup = findViewById(R.id.btn_dialog_signup);
        tv_error = findViewById(R.id.tv_signup_error);
        tv_error.setText("");
        fireBaseHelper = new FireBaseHelper();
    }
}
