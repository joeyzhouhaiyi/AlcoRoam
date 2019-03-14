package com.elec390coen.alcoroam.Activities.Setting;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.elec390coen.alcoroam.R;

import static android.content.Context.MODE_PRIVATE;

public class InfoDialog extends Dialog {
    EditText phoneNumber;
    EditText email;
    EditText name;
    Button btn_save;

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
        //retrieve the data from SharedPreferences

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String phoneString = pref.getString("phone", "");
        phoneNumber.setText(phoneString);

        String emailString = pref.getString("email", "");
        email.setText(emailString);

        String nameString = pref.getString("name", "");
        name.setText(nameString);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneEntered = phoneNumber.getText().toString();
                String emailEntered = email.getText().toString();
                String nameEntered = name.getText().toString();
                if(phoneEntered.isEmpty() || emailEntered.isEmpty() || nameEntered.isEmpty())
                {
                    Toast.makeText(context,"Cannot have empty field.",Toast.LENGTH_LONG).show();
                }
                else{
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = pref.edit();

                    editor.putString("phone", phoneEntered);
                    editor.putString("email", emailEntered);
                    editor.putString("name", nameEntered);
                    editor.apply();
                    Toast.makeText(context,"Saved",Toast.LENGTH_LONG).show();
                    dismiss();

                }

            }
        });
/*
        builder.setView(view)
                .setTitle("Emergency Contact")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        listener.applyTexts(phoneTxt, nameTxt, emailTxt);

                        SharedPreferences pref = context.getSharedPreferences("contact_info",MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();

                    editor.putString("phone", phoneTxt);
                    editor.putString("email", emailTxt);
                    editor.putString("name", nameTxt);
                    editor.apply();


                    }
                });

*/


 //       return builder.create();
    }

    private void initView()
    {
        phoneNumber = findViewById(R.id.contactPhone);
        email = findViewById(R.id.contactEmail);
        name = findViewById(R.id.contactName);
        btn_save = findViewById(R.id.btn_save);
    }
/*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (InfoDIalogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+ "must implement info");
        }
    }

    public interface InfoDIalogListener{
        void applyTexts (String phoneTxt, String nameTxt, String emailTxt);

    }
*/
//    public String getName(){
//        return nameTxt;
//    }
//
//
//    public String getNumber(){
//        return phoneTxt;
//    }
//
//    public String getEmail(){
//        return emailTxt;
//    }
}
