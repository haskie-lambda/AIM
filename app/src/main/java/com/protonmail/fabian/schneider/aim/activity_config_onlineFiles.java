package com.protonmail.fabian.schneider.aim;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static android.widget.Toast.makeText;

public class activity_config_onlineFiles extends AppCompatActivity { //TODO: usable for local files??
    WebView webView;
    Button saveConfig;
    EditText errorPattern;
    Context onlineFilesThis;
    int[] dataRest = new int[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_online_files);
        onlineFilesThis = this;
        webView = (WebView) findViewById(R.id.onSource_webView);
        saveConfig = (Button) findViewById(R.id.onSource_saveConfig);
        errorPattern = (EditText) findViewById(R.id.onSource_errorPattern);

        webView.loadUrl("http://www.google.com");

        saveConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] temp = getIntent().getStringExtra(constants.INTENT_EXTRA_DATA_RESTRICTION).split(",");
                dataRest[0] = Integer.parseInt(temp[0]);
                dataRest[1] = Integer.parseInt(temp[1]);
                Intent resultData = new Intent();
                resultData.putExtra(constants.INTENT_EXTRA_SOURCE_URL, webView.getUrl());
                resultData.putExtra(constants.INTENT_EXTRA_ERROR_PATTERN, errorPattern.getText().toString());
                resultData.putExtra(constants.INTENT_EXTRA_DATA_RESTRICTION, dataRest);

                setResult(Activity.RESULT_OK, resultData);
                Toast toast = makeText(onlineFilesThis, "Configuration successfully saved", Toast.LENGTH_SHORT);
                toast.show();
                finish();
            }
        });
    }
}
