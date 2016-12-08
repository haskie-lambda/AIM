package com.protonmail.fabian.schneider.aim;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import static com.protonmail.fabian.schneider.aim.R.styleable.Spinner;

/**
 * Created by faebl on 06.12.16.
 */
// TODO: Export module and delete from AIM - solve everything without inputBox!
public final class inputBox extends Settings {
    private String buttonOk;
    private String buttonCancel;
    private String returnValue;
    private Context setContext;
    private String sourcePath = "sourcePath";
    private int dataFrom = 1;
    private int dataTo = 1;

    inputBox(String buttonOk, String buttonCancel, Context setContext){
        this.buttonOk = buttonOk;
        this.buttonCancel = buttonCancel;
        this.setContext = setContext;
    }

    inputBox(String buttonOk, String buttonCancel, Context setContext, String sourcePath, int dataFrom, int dataTo){
        this.buttonOk = buttonOk;
        this.buttonCancel = buttonCancel;
        this.setContext = setContext;
        this.sourcePath = sourcePath;
        this.dataFrom = dataFrom;
        this.dataTo = dataTo;
    }

    public String show(android.widget.Spinner configSpinner) {
        final Spinner spinner = configSpinner;
        AlertDialog.Builder builder = new AlertDialog.Builder(setContext);
        builder.setTitle("ConfigurationName");
        final EditText input = new EditText(setContext);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton(buttonOk, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                returnValue = input.getText().toString();
                updateObject(input.getText().toString(), spinner);
            }
        });
        builder.setNegativeButton(buttonCancel, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                returnValue = "_NONE_";
            }
        });
        builder.show();
        return returnValue;
    }

    private void updateObject(String newName, Spinner configSpinner){
        //sSetting newConfig = super.makeNewConfig(returnValue, sourcePath, dataFrom, dataTo);
        //super.setNewConfig(newConfig);
        String actualConf;
        String actualConfObjString;
        sSetting actualConfObj;
        SharedPreferences pref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = pref.edit();
        actualConf = pref.getString("acutalConfig", "");
        actualConfObjString = pref.getString(actualConf,"");
        Gson gson = new Gson();
        actualConfObj = gson.fromJson(actualConfObjString, sSetting.class);
        actualConfObj.name = newName;
        actualConfObjString = gson.toJson(actualConfObj);
        prefEditor.putString("config_" + newName, actualConfObjString).commit(); //give at least one object
        prefEditor.putString("actualConfig", "config_" + newName).commit(); //set the name of the actual object
        selectSpinnerValue(configSpinner, newName.replace("config_",""));
        Toast toast = Toast.makeText(setContext, "Configuration successfully added", Toast.LENGTH_SHORT);
        toast.show();
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

}
