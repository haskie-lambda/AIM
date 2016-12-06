package com.protonmail.fabian.schneider.aim;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by faebl on 06.12.16.
 */

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

    public String show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(setContext);
        builder.setTitle("ConfigurationName");
        final EditText input = new EditText(setContext);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton(buttonOk, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                returnValue = input.getText().toString();
                createObject();
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

    private void createObject(){
        sSetting newConfig = super.makeNewConfig(returnValue, sourcePath, dataFrom, dataTo);
        super.setNewConfig(newConfig);
        Toast toast = Toast.makeText(setContext, "Configuration successfully added", Toast.LENGTH_SHORT);
        toast.show();
    }

}
