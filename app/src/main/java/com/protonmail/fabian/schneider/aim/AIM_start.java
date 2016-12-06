package com.protonmail.fabian.schneider.aim;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.TextView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import static com.protonmail.fabian.schneider.aim.R.id.activity_settings;

public class AIM_start extends AppCompatActivity {
    public Context tThis;
    public TextView status;
    private Button startButton;
    private Button stopButton;
    public TextView output;
    private Button settings;
    protected void onCreate(Bundle savedInstanceState) {
        //app bar
        super .onCreate(savedInstanceState);
        setContentView(R.layout.activity_aim_start);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.showOverflowMenu();
        ///app bar
        setContentView(R.layout.activity_aim_start);
        tThis = this;
        status = (TextView) findViewById(R.id.statusView);
        output = (TextView) findViewById(R.id.lbl_output);


        startButton = (Button) findViewById(R.id.start_AIM);
        startButton.setOnClickListener(new View.OnClickListener (){
            public void onClick(View v) {
                    status.setText("AIM-Started");
                    //newintent = new Intent(tThis, AIMServiceMain.class);
                    startService(new Intent(tThis, AIMServiceMain.class));
            }
        });


        stopButton = (Button) findViewById(R.id.stop_AIM);
        stopButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                status.setText("AIM-Stopped");
                stopService(new Intent(tThis, AIMServiceMain.class));
            }
        });
    }
    public void sendMessage(View view){
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }
}