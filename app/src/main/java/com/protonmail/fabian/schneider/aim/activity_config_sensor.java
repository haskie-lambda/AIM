package com.protonmail.fabian.schneider.aim;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.makeText;

public class activity_config_sensor extends AppCompatActivity {
    private ArrayList<String> sensors;
    private SensorManager sensorManager;
    private Button saveConfig;
    //private sSetting actualConf;
    private Context sensorThis;
    private String actualSourcePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_sensor);
        sensorThis = this;
        System.out.println("sensors started");
        //actualConf = (sSetting) getIntent().getSerializableExtra("actualConfig");
        actualSourcePath = getIntent().getStringExtra(constants.INTENT_EXTRA_ACTUAL_SOURCE_PATH);
        getSensors();
        addSensors();
        saveConfig = (Button) findViewById(R.id.btn_config_sensor_saveConfig);

        saveConfig.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                RadioGroup vg = (RadioGroup) findViewById(R.id.activity_config_sensor_sublayout_radio);
                int radioButtonID = vg.getCheckedRadioButtonId();
                View radioButton = vg.findViewById(radioButtonID);
                int idx = vg.indexOfChild(radioButton);
                RadioButton r = (RadioButton) vg.getChildAt(idx);
                //actualConf.sourcePath = r.getText().toString();
                Intent resultData = new Intent();
                resultData.putExtra(constants.INTENT_EXTRA_RETURN_SENSOR, r.getText().toString());
                setResult(Activity.RESULT_OK, resultData);
                Toast toast = makeText(sensorThis, "Configuration successfully saved", Toast.LENGTH_SHORT);
                toast.show();
                finish();
            }
        });

    }

    private void initSensors(){
        RadioGroup rg = ((RadioGroup) findViewById(R.id.activity_config_sensor_sublayout_radio));
        int count = rg.getChildCount();
        for(int i = 0;i<count;i++){
            RadioButton rb = (RadioButton) rg.getChildAt(i);
            if(rb.getText().toString().equals(actualSourcePath)){
                rb.setChecked(true);
            }
        }
    }

    private void addSensors(){
        //setContentView(R.layout.activity_config_sensor_sublayout);
        LinearLayout layout = (LinearLayout) findViewById(R.id.activity_config_sensor_sublayout);


        try {
            System.out.println("layout Oritentation (NPE-Test: " + layout.getOrientation());
            int counter = 0;
            for (String s : sensors) {
                //System.out.println("actualSensor to add: " + s);
                RadioButton rb = new RadioButton(this);
                rb.setText(s);
                rb.setTextSize(24);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                params.topMargin = 10000;
                rb.setId(counter);
                ((ViewGroup) findViewById(R.id.activity_config_sensor_sublayout_radio)).addView(rb);
                counter++;

            }
            initSensors();
        } catch (NullPointerException npe){
            System.out.println("NPE in addSensors()");
        }

    }

    private void getSensors() {
        System.out.println("getting Sensors...");
        sensors = new ArrayList<String>();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
        System.out.println("Count of Sensors: " + sensorList.size());
        Sensor tmp;
        int x, i;
        for (i = 0; i < sensorList.size(); i++) {
            tmp = sensorList.get(i);
            sensors.add(tmp.getName());
            //System.out.println("Sensor Nr. " + i + ": " + tmp.getName());
        }

    }
}


