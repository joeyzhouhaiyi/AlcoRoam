package com.elec390coen.alcoroam.Activities.Setting;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.elec390coen.alcoroam.Controllers.FireBaseHelper;
import com.elec390coen.alcoroam.R;

public class SettingActivity extends AppCompatActivity {

    EditText phoneNumber;
    EditText email;
    EditText name;
    Button saveBTN;
    String phoneTxt;
    String emailTxt;
    String nameTxt;

    FireBaseHelper fireBaseHelper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.style_activity_setting);

        //initialize views
        phoneNumber = findViewById(R.id.contactPhone);
        email = findViewById(R.id.contactEmail);
        name = findViewById(R.id.contactName);

        saveBTN = findViewById(R.id.saveBtn);
        fireBaseHelper = new FireBaseHelper();
        //retrieve the data from SharedPreferences

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String phoneString = pref.getString("phone", "");
        phoneNumber.setText(phoneString);

        String emailString = pref.getString("email", "");
        email.setText(emailString);

        String nameString = pref.getString("name", "");
        name.setText(nameString);



        //handle click
        saveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneTxt = phoneNumber.getText().toString().trim();
                emailTxt = email.getText().toString().trim();
                nameTxt = name.getText().toString();

                if(phoneTxt.isEmpty() || emailTxt.isEmpty() || nameTxt.isEmpty()){
                    Toast.makeText(SettingActivity.this, "Please enter complete information", Toast.LENGTH_SHORT).show();
                }
                else{
//                    saveToTxtFile(mText);
                    SharedPreferences pref = getSharedPreferences(fireBaseHelper.getCurrentUser().getUid(),MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();

                    editor.putString("phone", phoneTxt);
                    editor.putString("email", emailTxt);
                    editor.putString("name", nameTxt);
                    editor.apply();
                }

            }
        });


    }
//        private void saveToTxtFile(String mText){
//            String timeS
//        }
}
