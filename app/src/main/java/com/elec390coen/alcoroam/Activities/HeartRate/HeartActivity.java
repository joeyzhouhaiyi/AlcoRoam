package com.elec390coen.alcoroam.Activities.HeartRate;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.elec390coen.alcoroam.R;

public class HeartActivity<GraphView, LineGraphView> extends AppCompatActivity {
    private int x=0;
    private Button getHeartRate;
    private Button refreshButton;
    private TextView hrRateView;
    private TextView connectionStsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.style_activity_heart);
        //Connect U.I Elements
        getHeartRate = (Button) findViewById(R.id.heartRateBtn);
        hrRateView = (TextView) findViewById(R.id.hrValueView);
        connectionStsView = (TextView) findViewById(R.id.connectionStsView);
        refreshButton = (Button) findViewById(R.id.refreshBtn);

        /*final GraphView graphView = new LineGraphView(
                this, // context
                "Heart Rate Sensor" // heading
        );

        graphView.setVerticalLabels(new String[]{"high", "normal", "low"});

        graphView.setCustomLabelFormatter(new CustomLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    return null;
                } else {
                    if (value < 60) {
                        return "low";
                    } else if (value < 100) {
                        return "normal";
                    } else {
                        return "high";
                    }
                }
            }
        });
    }*/
