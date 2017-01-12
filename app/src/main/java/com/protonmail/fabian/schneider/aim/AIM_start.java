package com.protonmail.fabian.schneider.aim;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.TextView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import static android.widget.Toast.LENGTH_SHORT;
import static com.protonmail.fabian.schneider.aim.R.id.activity_settings;
import static com.protonmail.fabian.schneider.aim.R.id.lbl_actualConfig;

public class AIM_start extends AppCompatActivity {
    public Context tThis;

    private Button startButton;
    private Button stopButton;

    public TextView status;
    public TextView lbl_actualConf;
    public TextView output;

    private Button settings;
    protected void onCreate(Bundle savedInstanceState) {
        //app bar
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aim_start);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.showOverflowMenu();
        ///app bar

        lbl_actualConf = (TextView) findViewById(R.id.lbl_actualConfig);
        output =  (TextView) findViewById(R.id.lbl_output);
        setContentView(R.layout.activity_aim_start);
        tThis = this;
        status = (TextView) findViewById(R.id.statusView);
        lbl_actualConf.setText(getActualConfigName().replace("config_", ""));



        startButton = (Button) findViewById(R.id.start_AIM);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                status.setText("AIM-Started");
                //newintent = new Intent(tThis, AIMServiceMain.class);

                SharedPreferences prefs = getSharedPreferences("configuration", MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor = prefs.edit();
                if(!prefs.getString("actualConfig", "").equals("")) {
                    prefsEditor.putString("serviceStatus", "run").commit();
                    startService(new Intent(tThis, AIMServiceMain.class));
                    output.setText("AIM starting...");
                    status.setText("Booting...");
                } else{
                    Toast toast = Toast.makeText(tThis,"No Configuration set...", LENGTH_SHORT);
                    toast.show();
                }
            }
        });


        stopButton = (Button) findViewById(R.id.stop_AIM);
        stopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                status.setText("AIM-Stopped");
                //stopService(new Intent(tThis, AIMServiceMain.class)); //stopping service not working without binding
                //Stopping service with
                SharedPreferences prefs = getSharedPreferences("configuration", MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor = prefs.edit();
                prefsEditor.putString("serviceStatus", "stop").commit();
                output.setText("Shutdown...");
                status.setText("");
            }
        });

//UI-Strength update

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        double strength = intent.getDoubleExtra(constants.INTENT_FILTER_STRENGTH, -1);
                        output.setText("Actual Strength: " + strength);
                    }
                }, new IntentFilter(constants.INTENT_FILTER_STRENGTH)
        );

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        status.setText(intent.getStringExtra(constants.INTENT_FILTER_ADINFO));
                    }
                }, new IntentFilter(constants.INTENT_FILTER_ADINFO)
        );

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        lbl_actualConf.setText(intent.getStringExtra(constants.INTENT_FILTER_ACTUALCONF));
                    }
                }, new IntentFilter(constants.INTENT_FILTER_ACTUALCONF)
        );
    }




    public void sendMessage(View view){
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    private String getActualConfigName (){
        String ret;
        SharedPreferences prefs = getSharedPreferences(constants.SHAREDPREF_CONFIG, MODE_PRIVATE);
        if(!prefs.getBoolean(constants.SET_INSTALLER, false)) {
            ret = prefs.getString(constants.SHAREDPREF_ACTUAL_CONFIG, "");
            System.out.println(ret);
        } else{
            ret = "";
        }
        return ret;
    }

}