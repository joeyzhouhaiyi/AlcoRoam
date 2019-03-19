package com.elec390coen.alcoroam.Activities.bluetooth;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.elec390coen.alcoroam.Controllers.DeviceManager;
import com.elec390coen.alcoroam.Models.CurrentAlcoholSensor;
import com.elec390coen.alcoroam.Models.CurrentHeartRateSensor;
import com.elec390coen.alcoroam.R;

public class ChooseDeviceDialog extends Dialog {

    Context context;
    TextView tv_choose_device;
    Button btn_alcohol;
    Button btn_heart;
    TextView tv_choice_warning;
    BluetoothDevice selectedDevice;


    public ChooseDeviceDialog(Context context, BluetoothDevice device)
    {
        super(context,R.style.MyDialog);
        this.context = context;
        selectedDevice = device;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.style_dialog_choose_your_device);
        setCanceledOnTouchOutside(false);
        initView();
        btn_alcohol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CurrentAlcoholSensor a = new CurrentAlcoholSensor(selectedDevice);
                DeviceManager.setCurrentAlcoholSensor(a);
                dismiss();
            }
        });
        btn_heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CurrentHeartRateSensor h = new CurrentHeartRateSensor(selectedDevice);
                DeviceManager.setCurrentHeartRateSensor(h);
                dismiss();
            }
        });
    }

    public void initView()
    {
        tv_choose_device = findViewById(R.id.tv_choose_device);
        btn_alcohol = findViewById(R.id.btn_alcohol);
        btn_heart = findViewById(R.id.btn_heart);
        tv_choice_warning = findViewById(R.id.tv_choice_warning);

    }
}
