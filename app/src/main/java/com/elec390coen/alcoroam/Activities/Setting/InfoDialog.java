package com.elec390coen.alcoroam.Activities.Setting;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.elec390coen.alcoroam.Controllers.FireBaseAuthHelper;
import com.elec390coen.alcoroam.Controllers.FireBaseDBHelper;
import com.elec390coen.alcoroam.Models.User;
import com.elec390coen.alcoroam.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import static android.content.Context.MODE_PRIVATE;

public class InfoDialog extends Dialog {
    EditText et_phoneNumber;
    EditText et_email;
    EditText et_name;
    Button btn_save;

    FirebaseUser currentUser;
    FireBaseAuthHelper authHelper;
    FireBaseDBHelper dbHelper;

    Context context;
  //  InfoDIalogListener listener;

    public InfoDialog(Context context)
    {
        super(context,R.style.MyDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.style_dialog_settings_contact_info);
        setCanceledOnTouchOutside(true);
        initView();
        getCurrentUserInfo();
        //retrieve the data from SharedPreferences

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameEntered = et_name.getText().toString();
                String emailEntered = et_email.getText().toString();
                String phoneEntered = et_phoneNumber.getText().toString();
                if(nameEntered.isEmpty())
                {
                    et_name.setError("Field Required");
                }
                else if(emailEntered.isEmpty()){
                    et_email.setError("Field Required");
                }
                else if(phoneEntered.isEmpty()){
                    et_phoneNumber.setError("Field Required");
                }
                else{
                    String id = currentUser.getUid();
                    dbHelper.getUserRefWithId(id).child("emergencyContactName").setValue(nameEntered);
                    dbHelper.getUserRefWithId(id).child("emergencyContactEmail").setValue(emailEntered);
                    dbHelper.getUserRefWithId(id).child("emergencyContactNumber").setValue(phoneEntered);
                    dismiss();

                }

            }
        });

    }

    private void getCurrentUserInfo()
    {
        String id = currentUser.getUid();

        dbHelper.getUserRefWithId(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User thisUser = dataSnapshot.getValue(User.class);
                if(thisUser!=null)
                {
                    //set the contact global varriables
                    String contactName = thisUser.getEmergencyContactName();
                    //String contactname = thisUser.getEmergencyContactName();
                    String contactEmail = thisUser.getEmergencyContactEmail();
                    //String contactemail = thisUser.getEmergencyContactEmail();
                    String contactPhone = thisUser.getEmergencyContactNumber();
                    //String contactnumber = thisUser.getEmergencyContactNumber();
                    et_name.setText(contactName);
                    et_email.setText(contactEmail);
                    et_phoneNumber.setText(contactPhone);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void initView()
    {
        et_phoneNumber = findViewById(R.id.contactPhone);
        et_email = findViewById(R.id.contactEmail);
        et_name = findViewById(R.id.contactName);
        btn_save = findViewById(R.id.btn_save);
        dbHelper = new FireBaseDBHelper();
        authHelper = new FireBaseAuthHelper();
        currentUser = authHelper.getCurrentUser();
    }

}
