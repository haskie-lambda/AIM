package com.protonmail.fabian.schneider.aim;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Map;
import java.util.Set;

public class Settings extends AppCompatActivity {
    private Spinner sourceSpinner;
    private Spinner repSpinner;
    private Spinner repTimeSpinner;
    private Spinner configSpinner;
    private SeekBar sb_dataType;
    private SeekBar sb_disMedia;
    private SeekBar sb_MessFunc;
    private SeekBar sb_disconnect;
    private Button saveActualConfig;
    private Button addNewConfig;
    private Button removeActualConfig;

    private Context settingsContext = this;
    private String[] arraySpinner;

    private String newConfigName;
    private sSetting[] configObjects;
    private sSetting actualConf;
    private String actualConfigName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // initialize the elements of the activity
        configSpinner = (Spinner) findViewById(R.id.cbox_config);
        repTimeSpinner = (Spinner) findViewById(R.id.cbox_repTime);
        repSpinner = (Spinner) findViewById(R.id.cbox_reps);
        sourceSpinner = (Spinner) findViewById(R.id.cbox_source);
        sb_dataType = (SeekBar) findViewById(R.id.sb_dataType);
        sb_disMedia = (SeekBar) findViewById(R.id.sb_disAudio);
        sb_MessFunc = (SeekBar) findViewById(R.id.sb_MesFunc);
        sb_disconnect = (SeekBar) findViewById(R.id.sb_disDisconnect);
        saveActualConfig = (Button) findViewById(R.id.btn_saveConfig);
        addNewConfig = (Button) findViewById(R.id.btn_addConfig);


        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        if(!prefs.getBoolean("installer", false)){
            bootSequence();
            prefsEditor.putBoolean("installer", true).commit();
            System.out.println("Boot sequence successful");
        }

        setActualConfig();
        // initialize settings view for a configuration object

        //Listeners
        configSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String currentSelection = configSpinner.getSelectedItem().toString();
                //load the object with name in currentSelection
                setNewConfig(currentSelection);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });

        saveActualConfig.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v){
                    saveActualConfig();

                    Context context = getApplicationContext();
                    CharSequence text = "Configuration successfully saved";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                }
        });

        addNewConfig.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                inputBox inputBox = new inputBox("Ok", "Cancel", settingsContext, "sourcePath", 1,1);
                newConfigName = inputBox.show();
            }
        });

    }


    public void bootSequence(){
        sSetting boot;
        boot = new sSetting("Default",false,0,0,"Satellite","http://services.swpc.noaa.gov/text/goes-magnetometer-primary.txt",1,1,true,false,true);
        setNewConfig(boot);
    }

    public sSetting makeNewConfig(String name, String sourcePath, int dataFrom, int dataTo){
        sSetting newConfig;
        newConfig = new sSetting(name,
                                sb_dataType.getProgress()>0 ?  true:false,
                                Integer.parseInt(repSpinner.getSelectedItem().toString()),
                                Integer.parseInt(repTimeSpinner.getSelectedItem().toString()),
                                sourceSpinner.getSelectedItem().toString(),
                                sourcePath,
                                dataFrom,
                                dataTo,
                                sb_disMedia.getProgress()>0?true:false,
                                sb_MessFunc.getProgress()>0?true:false,
                                sb_disconnect.getProgress()>0?true:false);
        return newConfig;
    }

    protected void setActualConfig(){
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        actualConfigName = prefs.getString("actualConfig","");
        System.out.println("new actualConfigName: " + actualConfigName);
        setNewConfig(actualConfigName);
        initConfig();
    }

    protected void setNewConfig(String configName){
        if(!configName.contains("config_")){
            configName = "config_" + configName;
        }
        System.out.println("edited new actualConfigName: " + configName);
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = prefs.edit();
        Gson gson = new Gson();
        String json;
        json = prefs.getString(configName, "");
        actualConf = gson.fromJson(json, sSetting.class);
        prefEditor.putString("actualConfig", configName).commit();
    }

    public void setNewConfig(sSetting config){
        SharedPreferences pref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = pref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(config);
        prefEditor.putString("config_" + config.name, json).commit(); //give at least one object
        prefEditor.putString("actualConfig", "config_" + config.name).commit(); //set the name of the actual object
        actualConfigName = "config_" + config.name;
        setActualConfig();
    }

    protected void saveActualConfig(){
        actualConf.type = (sb_dataType.getProgress()>0);
        actualConf.disableOnMedia = (sb_disMedia.getProgress()>0);
        actualConf.readMessages = (sb_MessFunc.getProgress()>0);
        actualConf.disableOnDisconnect = (sb_disconnect.getProgress()>0);

        actualConf.reps = Integer.parseInt(repSpinner.getSelectedItem().toString());
        actualConf.repTime = Integer.parseInt(repTimeSpinner.getSelectedItem().toString());
        actualConf.source = sourceSpinner.getSelectedItem().toString();
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(actualConf);
        prefEditor.putString("config_"+ actualConf.name, json).commit();
        prefEditor.putString("actualConfig", actualConf.name).commit();
        setNewConfig(actualConfigName);
    }


    protected void initConfig(){
        //seekBars
        sb_dataType.setProgress(actualConf.type ? 1:0);
        sb_disMedia.setProgress(actualConf.disableOnMedia ? 1:0);
        sb_disconnect.setProgress(actualConf.disableOnDisconnect ? 1:0);
        sb_MessFunc.setProgress(actualConf.readMessages ? 1:0);

        initSpinners();
        //cboxes
        selectSpinnerValue(configSpinner, actualConfigName.replace("config_",""));
        selectSpinnerValue(repSpinner, String.valueOf(actualConf.reps));
        selectSpinnerValue(repTimeSpinner, String.valueOf(actualConf.repTime));

    }

    private void selectSpinnerValue(Spinner spinner, String value){
        int i = 0;
        for(i = 0; i<spinner.getCount(); i++){
            if(spinner.getItemAtPosition(i).toString().equals(value)){
                spinner.setSelection(i);
                return;
            }
        }
    }


    protected void initSpinners(){
        arraySpinner = new String[1];
        SharedPreferences pref = getPreferences(MODE_PRIVATE);
        Map<String, ?> allEntries = pref.getAll();
        for(Map.Entry<String, ?> entry : allEntries.entrySet()){
            if(entry.getKey().contains("config_")){
                arraySpinner[0] = entry.getKey().replace("config_", "");
                arraySpinner = java.util.Arrays.copyOf(arraySpinner, arraySpinner.length +1);
            }
        }
        fillSpinner(configSpinner, arraySpinner);

        this.arraySpinner = new String[10];
        int i;
        for (i=0;i<=9;i++){
            arraySpinner[i] = String.valueOf(i);
        }
        fillSpinner(repTimeSpinner,arraySpinner);
        fillSpinner(repSpinner,arraySpinner);

        //TODO: check the live-Data/file Data (settings object) for right initialization

        this.arraySpinner = new String[]{
                "Satellite","Online-Source", "File-Source"
        };
        fillSpinner(sourceSpinner, arraySpinner);
    }

    protected void fillSpinner(Spinner spinner, String[] contents){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, contents);
        spinner.setAdapter(adapter);
        return;
    }
}