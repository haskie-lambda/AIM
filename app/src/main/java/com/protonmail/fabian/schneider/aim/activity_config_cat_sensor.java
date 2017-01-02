package com.protonmail.fabian.schneider.aim;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static android.widget.GridLayout.HORIZONTAL;
import static android.widget.GridLayout.LEFT;
import static android.widget.Toast.makeText;

public class activity_config_cat_sensor extends AppCompatActivity {
    private Context configCatSensorThis;

    private TextView sensorOutput;


    private Button btn_saveConfig;
    private Button btn_addConfigCat;
    private Button btn_remConfigCat;

    private SensorManager mSensorManager;
    private Sensor sensor;

    private String actualSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_cat_sensor);
        configCatSensorThis = this;


        initStrengthArray(getIntent().getStringArrayExtra(constants.INTENT_EXTRA_ACTUAL_STRENGTH_ARRAY));

        //sensorOutput = (TextView) findViewById(R.id.lbl_sensorOutput); TODO: make new lable for sensor output

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        actualSensor = getIntent().getStringExtra(constants.INTENT_EXTRA_ACTUAL_SENSOR);
        if(actualSensor!=null) {
            if (actualSensor.contains(constants.SENSOR_CHECK_TEMP)) {  //TODO: initialize various sensors
                sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
            } else {
                Toast toast = makeText(configCatSensorThis, "Sensor configuration not correct: " + actualSensor, Toast.LENGTH_SHORT);
                toast.show();
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        }

        btn_saveConfig = (Button) findViewById(R.id.btn_config_cat_sensor_saveConfig);


        //LISTENERS TODO: add functionality for add and remove buttons
        btn_saveConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> strengths = new ArrayList<String>();
                strengths.clear();

                /*double[] intStrengths = new double[3];
                for(EditText i:etEl){   //loop editText Boxes

                    String[] temp;
                    temp = i.getText().toString().split(";");   //split textBox to single Strenghts fromTo

                    for(String s:temp){                             //loop all single Strengths fromTo
                        intStrengths[3] = Double.parseDouble(i.getTag().toString().substring(i.getTag().toString().length()-1));
                        strengths.add(strengths.size(), s.replace("_",",") +"," +  String.valueOf(i.getTag().toString().substring(i.getTag().toString().length()-1)));
                    }

                }

                String[] tempString = new String[strengths.size()];
                for(int i=0;i<tempString.length;i++) {
                    tempString[i] = strengths.get(i);
                }

                Intent resultData = new Intent();
                resultData.putExtra("strengthArray", tempString);

                setResult(Activity.RESULT_OK, resultData);
                TODO: correct saving procedure!!

                for(LinearLayout ll:((LinearLayout) findViewById(R.id.config_cat_sensor_linScrollLayout))){                 FOREACH NOT WORKIGN WITH LINEAR LAYOUT

                }
                */
                Toast toast = makeText(configCatSensorThis, "Configuration successfully saved", Toast.LENGTH_SHORT);
                toast.show();
                finish();
            }
        });

    }

    private void initStrengthArray(String[] actualStrength){
        String[] temp;
            for (String s : actualStrength) {
                temp = s.split(",");
                //make new row and fill with values...
                LinearLayout scrollLayout = (LinearLayout) findViewById(R.id.config_cat_sensor_linScrollLayout);
                scrollLayout.addView(makeRow(temp[0], temp[1], temp[2]));
            }
    }

    private LinearLayout makeRow(String from, String to, String strength){
        LinearLayout tempLinLay = new LinearLayout(configCatSensorThis);
        tempLinLay.setOrientation(HORIZONTAL);
        EditText etFrom;
        EditText etTo;
        Spinner spinStrength;
        etFrom = new EditText(configCatSensorThis);
        etFrom.setText(from);
        tempLinLay.addView(etFrom);

        etTo = new EditText(configCatSensorThis);
        etTo.setText(to);
        tempLinLay.addView(etTo);

        //TODO: ADD SPINNER WITH INIT SPINNER PROCEDURE


        return tempLinLay;
    }

    /*
    //@Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy){
        //TODO: IN BETA - fix sensor screening
        sensorOutput.setText(String.valueOf(sensor.getPower()));
    }*/
}
