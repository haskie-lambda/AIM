package com.protonmail.fabian.schneider.aim;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.gson.Gson;

import static android.widget.Toast.makeText;

public class activity_config_satellites extends AppCompatActivity {
    //TODO: add Satellites to the scrolling activity with standard configs
    Button saveConfig;
    Context configSatellitesThis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_satellites);
        configSatellitesThis = this;
        saveConfig = (Button) findViewById(R.id.config_satellites_saveConfig);

        SharedPreferences prefs = getSharedPreferences(constants.SHAREDPREF_CONFIG, MODE_PRIVATE);
        if(prefs.getString(constants.SHAREDPREF_ACTUAL_CONFIG, "").equals("GEOS Magnetometer")){
            ((RadioButton) findViewById(R.id.config_satellites_rb_geos)).setSelected(true);
        }



        //LISTENERS
        saveConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultData = new Intent();
                Gson gson = new Gson();
                String json = gson.toJson(GEOS_MAGNETOMETER());
                resultData.putExtra(constants.INTENT_EXTRA_SATELLITE_CONFIG, json);
                setResult(Activity.RESULT_OK, resultData);
                Toast toast = makeText(configSatellitesThis, "Configuration successfully saved", Toast.LENGTH_SHORT);
                toast.show();
                finish();
            }
        });

    }

    private sSetting GEOS_MAGNETOMETER(){
        sSetting geos = new sSetting("GEOS Magnetometer");
        return geos;
    }

}
