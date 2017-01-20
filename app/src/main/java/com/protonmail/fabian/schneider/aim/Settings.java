package com.protonmail.fabian.schneider.aim;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

import static android.widget.Toast.makeText;

public class Settings extends AppCompatActivity {
    private Spinner sourceSpinner;
    //private Spinner repSpinner;
    //private Spinner repTimeSpinner;
    private Spinner configSpinner;
    private SeekBar sb_dataType;
    private SeekBar sb_disMedia;
    private SeekBar sb_MessFunc;
    private SeekBar sb_disconnect;
    private EditText userDefinedConfigName;


    private Context settingsContext = this;
    private ArrayList<String> arraySpinner;

    private sSetting actualConf;
    private String actualConfigName;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {


        //TODO: read messages & Static data
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //local variables
        Button btn_configureData;
        Button btn_removeConfig;
        TextView lbl_test;
        Button btn_configureCategorization;
        Button saveActualConfig;
        Button addNewConfig;

        // initialize the elements of the activity
        configSpinner = (Spinner) findViewById(R.id.cbox_config);
        sourceSpinner = (Spinner) findViewById(R.id.cbox_source);
        sb_dataType = (SeekBar) findViewById(R.id.sb_dataType);
        sb_disMedia = (SeekBar) findViewById(R.id.sb_disAudio);
        sb_MessFunc = (SeekBar) findViewById(R.id.sb_MesFunc);
        sb_disconnect = (SeekBar) findViewById(R.id.sb_disDisconnect);
        saveActualConfig = (Button) findViewById(R.id.btn_saveConfig);
        addNewConfig = (Button) findViewById(R.id.btn_addConfig);
        userDefinedConfigName = (EditText) findViewById(R.id.tbox_newConfigName);
        btn_configureData = (Button) findViewById(R.id.btn_confData);
        btn_removeConfig = (Button) findViewById(R.id.btn_delConfig);
        btn_configureCategorization = (Button) findViewById(R.id.btn_confCategorization);

        lbl_test = (TextView) findViewById(R.id.lbl_test);


        configSpinner.setFocusable(true);
        //Styles
        sb_dataType.getProgressDrawable().setColorFilter(Color.parseColor("#3644D9"), PorterDuff.Mode.SRC_IN);
        sb_dataType.getThumb().setColorFilter(Color.parseColor("#3644D9"), PorterDuff.Mode.SRC_IN);

        //boot
        //Initialize a default configuration
        final SharedPreferences prefs = getSharedPreferences(constants.SHAREDPREF_CONFIG, MODE_PRIVATE);
        final SharedPreferences.Editor prefsEditor = prefs.edit();
        if(!prefs.getBoolean(constants.SET_INSTALLER, false)){
            bootSequence();
            prefsEditor.putBoolean(constants.SET_INSTALLER, true).apply();
        }

        setActualConfig();

        //Listeners

        configSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!configSpinner.getSelectedItem().toString().equals(actualConfigName.replace(constants.CONF_PREFIX,""))) {
                    try {
                        prefsEditor.putString(constants.SHAREDPREF_ACTUAL_CONFIG, configSpinner.getSelectedItem().toString()).commit();
                        setActualConfig();
                    } catch (NullPointerException e) {
                        System.out.println("NPE in config-Spinner selection");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        saveActualConfig.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v){
                    saveActualConfig();
                    Toast toast = makeText(settingsContext, "Configuration successfully saved", Toast.LENGTH_SHORT);
                    toast.show();
                }
        });

        addNewConfig.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                String name = userDefinedConfigName.getText().toString();
                if (name.length() < 1){
                    Toast toast = makeText(settingsContext, "Please name your configuration", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                sSetting newConfig = makeNewConfig(name);
                setNewConfig(newConfig);
                Toast toast = makeText(settingsContext, "Configuration successfully added", Toast.LENGTH_SHORT);
                toast.show();
            }
        });


        btn_removeConfig.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                String name = actualConfigName.contains(constants.CONF_PREFIX)?actualConfigName:constants.CONF_PREFIX+actualConfigName;
                SharedPreferences prefs = getSharedPreferences(constants.SHAREDPREF_CONFIG,MODE_PRIVATE);
                SharedPreferences.Editor prefEdit = prefs.edit();
                prefEdit.remove(name);
                prefEdit.apply();
                //deleted config
                int i;
                Object temp;
                for(i = 0; i<configSpinner.getCount()-1; i++){
                    temp = configSpinner.getItemAtPosition(i);
                    try {
                        if(!temp.toString().equals(name)){
                            prefsEditor.putString(constants.SHAREDPREF_ACTUAL_CONFIG, temp.toString()).commit();
                            setActualConfig();
                            //TODO: reInit settings view
                            return;
                        }
                    } catch (NullPointerException e) {
                        System.out.println("Element " + Integer.toString(i) + ": NULL");
                    }
                }
            }
        });

        btn_configureData.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                String act;
                Intent intent;
                switch(sourceSpinner.getSelectedItem().toString()){
                    case constants.ST_SATELLITES:
                        act = constants.ST_SATELLITES;
                        intent = new Intent(settingsContext, activity_config_satellites.class);
                        break;
                    case constants.ST_SENSOR_DATA:
                        act = constants.ST_SENSOR_DATA;
                        startSensorConfig(findViewById(android.R.id.content));
                        intent = new Intent(settingsContext, activity_config_sensor.class);
                        intent.putExtra(constants.INTENT_EXTRA_ACTUAL_SOURCE_PATH, actualConf.sourceConfig.source);
                        break;
                    case constants.ST_ONLINE_SOURCE:
                        act = constants.ST_ONLINE_SOURCE;
                        intent = new Intent(settingsContext, activity_config_onlineFiles.class);
                        intent.putExtra(constants.INTENT_EXTRA_DATA_RESTRICTION, actualConf.restFrom + "," + actualConf.restTo);
                        intent.putExtra(constants.INTENT_EXTRA_ONLINE_LOCAL, constants.PST_ONLINE);
                        break;
                    case constants.ST_FILE_SOURCE:
                        act = constants.ST_FILE_SOURCE;
                        intent = new Intent(settingsContext, activity_config_onlineFiles.class); //TODO: change eventually
                        intent.putExtra(constants.INTENT_EXTRA_DATA_RESTRICTION, actualConf.restFrom + "," + actualConf.restTo +"," + actualConf.restBy);
                        intent.putExtra(constants.INTENT_EXTRA_ONLINE_LOCAL, constants.PST_OFFLINE);
                        break;
                    /*case "Bluetooth-Source":
                        act = "Bluetooth-Source";
                        intent = new Intent(settingsContext, activity_config_sensor.class);
                        break;
                    case "Position":
                        act = "Position";
                        intent = new Intent(settingsContext, activity_config_sensor.class);
                        break;
                        */
                    default:
                        act = constants.ST_ERROR;
                        return;
                }
                startConfig(intent);
                Toast toast = makeText(settingsContext,"Configuration for " + act + " started", Toast.LENGTH_SHORT);
                toast.show();
            }
        });


        btn_configureCategorization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCatConfig();
            }
        });

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            System.out.println("backOverride");
            setResult(0);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy(){
        System.out.println("onDestroy");
        setResult(0);
        super.onDestroy();
    }


    public void startConfig(Intent intent){
        startActivityForResult(intent, 0);
    }

    public void startSensorConfig(View view){
        Intent intent = new Intent(this, activity_config_sensor.class);
        intent.putExtra(constants.INTENT_EXTRA_ACTUAL_SOURCE_PATH, actualConf.sourceConfig.sourceType);
        startActivityForResult(intent, 0);
    }


    public void startCatConfig(){
        Intent intent;
        intent = new Intent(this,activity_config_cat_sensor.class);
        intent.putExtra(constants.INTENT_EXTRA_ACTUAL_SENSOR, actualConf.sourceConfig.source);
        intent.putExtra(constants.INTENT_EXTRA_ACTUAL_STRENGTH_ARRAY, actualConf.strengthArray);
        startActivityForResult(intent, 1);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            switch (data.getStringExtra(constants.INTENT_EXTRA_RETURN)) {
                case (constants.INTENT_EXTRA_RETURN_SATELLITE): // INTENT CASES HERE
                    Gson converter = new Gson();
                    sSetting temp = converter.fromJson(data.getStringExtra(constants.INTENT_EXTRA_SATELLITE_CONFIG), sSetting.class);
                    setNewConfig(temp);
                    break;
                case (constants.INTENT_EXTRA_RETURN_SENSOR):
                    actualConf.sourceConfig.source = data.getStringExtra(constants.INTENT_EXTRA_ACTUAL_SENSOR);
                    break;
                case (constants.INTENT_EXTRA_RETURN_SARRAY):
                    actualConf.strengthArray = new ArrayList<String>();
                    actualConf.strengthArray = data.getStringArrayListExtra(constants.INTENT_EXTRA_ACTUAL_STRENGTH_ARRAY);
                    setNewConfig(actualConf);
                    try {
                        System.out.println("new conf with new StrengthArray set:" + actualConf.strengthArray.get(0));
                    }catch (NullPointerException e){
                        System.out.println("no array");
                        e.printStackTrace();
                    }
                    break;
                case (constants.INTENT_EXTRA_RETURN_ONLINEFILE):
                    actualConf.sourceConfig.errorPattern = data.getStringExtra(constants.INTENT_EXTRA_ERROR_PATTERN);
                    actualConf.restFrom = data.getIntArrayExtra(constants.INTENT_EXTRA_DATA_RESTRICTION)[0];
                    actualConf.restTo = data.getIntArrayExtra(constants.INTENT_EXTRA_DATA_RESTRICTION)[1];
                    actualConf.sourceConfig.source = data.getStringExtra(constants.INTENT_EXTRA_SOURCE_URL);
                    actualConf.restBy = data.getStringExtra(constants.INTENT_EXTRA_DATA_RESTRICTION_BY);
                    actualConf.lineOfFile = data.getIntExtra(constants.INTENT_EXTRA_DATA_LINE_IN_FILE, -1);

                    break;
            }


        } catch (NullPointerException e) {
            System.out.println("No data received from intent");
            e.printStackTrace();
        }

    }

    public void bootSequence(){
        sSetting boot;
        boot = new sSetting("Default");
        setNewConfig(boot);
    }

    public sSetting makeNewConfig(String name){
        sSetting newConfig;
        newConfig = new sSetting(constants.CONF_PREFIX + name);
        newConfig.type = sb_dataType.getProgress()>0;
        newConfig.sourceConfig.sourceType = sourceSpinner.getSelectedItem().toString();
        newConfig.disableOnMedia = sb_disMedia.getProgress()>0;
        newConfig.readMessages = sb_disMedia.getProgress()>0;
        newConfig.disableOnDisconnect = sb_disconnect.getProgress()>0;
        return newConfig;
    }



    protected void saveActualConfig(){
        actualConf.type = (sb_dataType.getProgress()>0);
        actualConf.disableOnMedia = (sb_disMedia.getProgress()>0);
        actualConf.readMessages = (sb_MessFunc.getProgress()>0);
        actualConf.disableOnDisconnect = (sb_disconnect.getProgress()>0);
        actualConf.sourceConfig.sourceType = sourceSpinner.getSelectedItem().toString();
        actualConf.name = userDefinedConfigName.getText().toString();
        actualConfigName = actualConf.name;

        SharedPreferences prefs = getSharedPreferences(constants.SHAREDPREF_CONFIG, MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(actualConf);
        prefEditor.putString(constants.CONF_PREFIX + actualConf.name, json).commit();
        prefEditor.putString(constants.SHAREDPREF_ACTUAL_CONFIG, actualConf.name).commit();
        setActualConfig();

        Intent pubIntent = new Intent(constants.INTENT_FILTER_ACTUALCONF);
        pubIntent.putExtra(constants.INTENT_FILTER_ACTUALCONF, actualConf.name.replace(constants.CONF_PREFIX, ""));
        sendBroadcast(pubIntent);
    }



    public void setNewConfig(sSetting config){
        if(!config.name.contains(constants.CONF_PREFIX)){
            config.name = constants.CONF_PREFIX + config.name;
        }
        SharedPreferences pref = getSharedPreferences(constants.SHAREDPREF_CONFIG, MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = pref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(config);
        prefEditor.putString(config.name, json).commit();
        prefEditor.putString(constants.SHAREDPREF_ACTUAL_CONFIG,config.name).commit();
        actualConfigName = config.name;
        setActualConfig();
    }
    protected void setActualConfig(){
        SharedPreferences prefs = getSharedPreferences("configuration", MODE_PRIVATE);
        actualConfigName = prefs.getString(constants.SHAREDPREF_ACTUAL_CONFIG,"");
        System.out.println("new actualConfigName: " + actualConfigName);
        setNewConfig(actualConfigName);     //METHOD BENEATH NOT ABOVE!!
        initConfig();
    }

    protected void setNewConfig(String configName){
        if(!configName.contains(constants.CONF_PREFIX)){
            configName = constants.CONF_PREFIX + configName;
        }
        System.out.println("edited new actualConfigName: " + configName);
        SharedPreferences prefs = getSharedPreferences(constants.SHAREDPREF_CONFIG, MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = prefs.edit();
        Gson gson = new Gson();
        String json;
        json = prefs.getString(configName, "");
        actualConf = gson.fromJson(json, sSetting.class);
        prefEditor.putString(constants.SHAREDPREF_ACTUAL_CONFIG, configName).commit();
    }



    protected void initConfig(){
        //seekBars
        sb_dataType.setProgress(actualConf.type ? 1:0);
        sb_disMedia.setProgress(actualConf.disableOnMedia ? 1:0);
        sb_disconnect.setProgress(actualConf.disableOnDisconnect ? 1:0);
        sb_MessFunc.setProgress(actualConf.readMessages ? 1:0);

        userDefinedConfigName.setText(actualConf.name.replace(constants.CONF_PREFIX,""));
        //AIM_start.lbl_actualConf.setText(actualConf.name.replace(constants.CONF_PREFIX,"")); TODO: set new actualConf on homescreen
        System.out.println("sending Broadcast");
        Intent pubIntent = new Intent(constants.INTENT_FILTER_ACTUALCONF);
        pubIntent.putExtra(constants.INTENT_FILTER_ACTUALCONF, actualConf.name.replace(constants.CONF_PREFIX, ""));
        sendBroadcast(pubIntent);

        initSpinners();

        selectSpinnerValue(configSpinner, actualConfigName.replace(constants.CONF_PREFIX,""));
        selectSpinnerValue(sourceSpinner, actualConf.sourceConfig.sourceType);
    }

    private void selectSpinnerValue(Spinner spinner, String value){
        int i;
        for(i = 0; i<spinner.getCount(); i++){
            if(spinner.getItemAtPosition(i).toString().equals(value)){
                spinner.setSelection(i);
                return;
            }
        }
    }


    protected void initSpinners(){
        SharedPreferences pref = getSharedPreferences(constants.SHAREDPREF_CONFIG, MODE_PRIVATE);
        Map<String, ?> allEntries = pref.getAll();
        arraySpinner = new ArrayList <String>();
        int i = 0;
        for(Map.Entry<String, ?> entry : allEntries.entrySet()){
            System.out.println("searching Keys: " + entry.getKey());
            if(entry.getKey().toString().contains(constants.CONF_PREFIX)){
                System.out.println(entry.getKey().toString());
                arraySpinner.add(i, entry.getKey().toString().replace(constants.CONF_PREFIX,""));
                i++;
            }
        }

        fillSpinner(configSpinner, arraySpinner);

        arraySpinner = new ArrayList<String>();

        if(actualConf.getTypeName().equals(constants.ST_CONF_TYPE_RT_DATA)){
            this.arraySpinner.add(constants.ST_SATELLITES);
            this.arraySpinner.add(constants.ST_ONLINE_SOURCE);
            this.arraySpinner.add(constants.ST_SENSOR_DATA);
            //this.arraySpinner.add(constants.ST_FILE_SOURCE); TODO: in future update
            //this.arraySpinner.add("Bluetooth-Source");
            //this.arraySpinner.add("Position");
        } else{
            //this.arraySpinner.add(constants.ST_FILE_SOURCE); TODO: in future update
            this.arraySpinner.add(constants.ST_JUST_MESSAGES);
        }
        fillSpinner(sourceSpinner, arraySpinner);
    }

    protected void fillSpinner(Spinner spinner, ArrayList <String> contents){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(settingsContext, R.layout.support_simple_spinner_dropdown_item, contents);
        spinner.setAdapter(adapter);
    }
}
