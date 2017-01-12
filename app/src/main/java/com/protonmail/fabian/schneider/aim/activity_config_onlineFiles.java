package com.protonmail.fabian.schneider.aim;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import static android.widget.Toast.makeText;

public class activity_config_onlineFiles extends AppCompatActivity {
    WebView webView;
    Button webViewGo;
    Button saveConfig;
    EditText webViewUrl;
    EditText errorPattern;
    EditText restFrom;
    EditText restTo;
    EditText restBy;
    EditText lineOfFile;
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
        webViewGo = (Button) findViewById(R.id.onSource_webView_go);
        webViewUrl = (EditText) findViewById(R.id.et_webViewUrl);
        restFrom = (EditText) findViewById(R.id.onSource_restFrom);
        restTo = (EditText) findViewById(R.id.onSource_restTo);
        restBy = (EditText) findViewById(R.id.onSource_restBy);
        lineOfFile = (EditText) findViewById(R.id.onSource_lineOfFile);

        if(getIntent().getStringExtra(constants.INTENT_EXTRA_ONLINE_LOCAL).equals(constants.PST_ONLINE)) {
            webView.loadUrl("http://www.google.com");
        } else {
            webView.loadUrl("file:///"); //TODO: in future update filepicker?
        }


        webViewGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.loadUrl(webViewUrl.getText().toString());
            }
        });

        webView.setWebViewClient(new WebViewClient(){
            public void onPageFinished(WebView view, String url){
                webViewUrl.setText(url);
            }
        });

        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) { //TODO: in later update get selection
                if(event.getAction() == android.view.MotionEvent.ACTION_UP){

                }

                return false;
            }
        });

        saveConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataRest[0] = Integer.parseInt(restBy.getText().toString());
                dataRest[1] = Integer.parseInt(restTo.getText().toString());
                Intent resultData = new Intent();
                resultData.putExtra(constants.INTENT_EXTRA_ERROR_PATTERN, errorPattern.getText().toString());
                resultData.putExtra(constants.INTENT_EXTRA_DATA_RESTRICTION, dataRest);
                resultData.putExtra(constants.INTENT_EXTRA_SOURCE_URL, webView.getUrl());
                resultData.putExtra(constants.INTENT_EXTRA_DATA_RESTRICTION_BY, restBy.getText().toString());
                resultData.putExtra(constants.INTENT_EXTRA_DATA_LINE_IN_FILE, Integer.parseInt(lineOfFile.getText().toString()));
                resultData.putExtra(constants.INTENT_EXTRA_RETURN, constants.INTENT_EXTRA_RETURN_ONLINEFILE);

                setResult(Activity.RESULT_OK, resultData);
                Toast toast = makeText(onlineFilesThis, "Configuration successfully saved", Toast.LENGTH_SHORT);
                toast.show();
                finish();
            }
        });
    }
}
