package com.protonmail.fabian.schneider.aim;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.widget.GridLayout.HORIZONTAL;
import static android.widget.GridLayout.LEFT;
import static android.widget.Toast.makeText;

public class activity_config_cat_sensor extends AppCompatActivity {
    private Context configCatSensorThis;

    private TextView sensorOutput;


    private Button btn_saveConfig;
    private Button btn_addConfigCat;

    private SensorManager mSensorManager;
    private Sensor sensor;

    private String actualSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_cat_sensor);
        configCatSensorThis = this;
        btn_addConfigCat = (Button) findViewById(R.id.config_cat_sensor_addRow);

        initStrengthArray(getIntent().getStringArrayListExtra(constants.INTENT_EXTRA_ACTUAL_STRENGTH_ARRAY));

        //sensorOutput = (TextView) findViewById(R.id.lbl_sensorOutput); TODO: in next update make new lable for sensor output and other means
/*
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        actualSensor = getIntent().getStringExtra(constants.INTENT_EXTRA_ACTUAL_SENSOR);
        if(actualSensor!=null) {
            if (actualSensor.contains(constants.SENSOR_CHECK_TEMP)) {
                sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
            } else {
                Toast toast = makeText(configCatSensorThis, "Sensor configuration not correct: " + actualSensor, Toast.LENGTH_SHORT);
                toast.show();
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        }*/

        btn_saveConfig = (Button) findViewById(R.id.btn_config_cat_sensor_saveConfig);

        btn_saveConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> strengths = new ArrayList<String>();
                strengths.clear();

                LinearLayout parentLayout = (LinearLayout) findViewById(R.id.config_cat_sensor_linScrollLayout);
                LinearLayout actualLayout;
                String from;
                String to;
                String strength;

                for(int i = 0;i<parentLayout.getChildCount(); i++){
                    actualLayout = (LinearLayout) parentLayout.getChildAt(i);
                    from = ((TextView) actualLayout.getChildAt(0)).getText().toString();
                    to = ((TextView) actualLayout.getChildAt(1)).getText().toString();
                    strength = "0"; //((Spinner) actualLayout.getChildAt(2).get)
                    strengths.add(from + "," + to + "," + strength);
                }

                Intent resultData = new Intent();
                resultData.putExtra(constants.INTENT_EXTRA_RETURN, constants.INTENT_EXTRA_RETURN_SARRAY);
                resultData.putExtra(constants.INTENT_EXTRA_ACTUAL_STRENGTH_ARRAY, strengths);
                setResult(Activity.RESULT_OK, resultData);

                Toast toast = makeText(configCatSensorThis, "Configuration successfully saved", Toast.LENGTH_SHORT);
                toast.show();
                finish();
            }
        });


        btn_addConfigCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout scrollLayout = (LinearLayout) findViewById(R.id.config_cat_sensor_linScrollLayout);
                scrollLayout.addView(makeRow("0","0","0"));
            }
        });

    }

    private void initStrengthArray(ArrayList<String> actualStrength){
        String[] temp;
            for (String s : actualStrength) {
                temp = s.split(",");
                //make new row and fill with values...
                LinearLayout scrollLayout = (LinearLayout) findViewById(R.id.config_cat_sensor_linScrollLayout);
                scrollLayout.addView(makeRow(temp[0], temp[1], temp[2]));
            }
    }

    private LinearLayout makeRow(String from, String to, String strength){
        int actualCount = (int) System.currentTimeMillis();
        LinearLayout tempLinLay = new LinearLayout(configCatSensorThis);
        tempLinLay.setOrientation(HORIZONTAL);
        tempLinLay.setId(actualCount);
        EditText etFrom;
        EditText etTo;
        Spinner spinStrength;
        Button rem;


        etFrom = new EditText(configCatSensorThis);
        etFrom.setText(from);
        etFrom.setId(actualCount);
        tempLinLay.addView(etFrom);

        etTo = new EditText(configCatSensorThis);
        etTo.setText(to);
        etTo.setId(actualCount);
        tempLinLay.addView(etTo);

        spinStrength = makeSpinner();
        spinStrength.setId(actualCount);
        tempLinLay.addView(spinStrength);

        rem = new Button(configCatSensorThis);
        rem.setText("-");
        rem.setId(actualCount);
        rem.setLayoutParams(new ActionBar.LayoutParams(android.app.ActionBar.LayoutParams.WRAP_CONTENT, android.app.ActionBar.LayoutParams.WRAP_CONTENT));
        rem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout parentLinLay = (LinearLayout) findViewById(R.id.config_cat_sensor_linScrollLayout);
                LinearLayout currentLay = (LinearLayout) findViewById(v.getId());
                parentLinLay.removeView(currentLay);
            }
        });
        tempLinLay.addView(rem);

        return tempLinLay;
    }

    protected Spinner makeSpinner(){
        Spinner spin = new Spinner(configCatSensorThis);
        ArrayList arr = new ArrayList<String>();

        int i;
        for(i=0;i<=3;i++){
            arr.add(i,String.valueOf(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(configCatSensorThis, R.layout.support_simple_spinner_dropdown_item, arr);
        spin.setAdapter(adapter);
        return spin;
    }

}
