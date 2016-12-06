package com.protonmail.fabian.schneider.aim;

/**
 * Created by faebl on 05.12.16.
 *
 *
 *
 *
 */

public class sSetting {
    public String name;
    public boolean type;
    public int reps;
    public int repTime;
    public String source;
    public String sourcePath;
    public int restFrom;
    public int restTo;
    public boolean disableOnMedia;
    public boolean readMessages;
    public boolean disableOnDisconnect;

    sSetting(String name, boolean type, int reps, int repTime, String source, String sourcePath, int restFrom, int restTo, boolean disableOnMedia, boolean readMessages, boolean disableOnDisconnect){
        this.name = name;
        this.type = type;
        this.reps = reps;
        this.repTime = repTime;
        this.source = source;
        this.sourcePath = sourcePath;
        this.restFrom = restFrom;
        this.restTo = restTo;
        this.disableOnMedia = disableOnMedia;
        this.readMessages = readMessages;
        this.disableOnDisconnect = disableOnDisconnect;
    }

    public void setRestiction(int from, int to){
        this.restFrom = from;
        this.restTo = to;
    }

    public String getTypeName(){
        if(type) {
            return "RTData";
        }else{
            return "FData";
        }
    }
}
