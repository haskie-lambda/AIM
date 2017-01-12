package com.protonmail.fabian.schneider.aim;

/**
 * Created by faebl on 05.12.16.
 *
 *
 *
 *
 */
import java.io.Serializable;
import java.util.ArrayList;

public class sSetting implements Serializable {
    public String name;
    public boolean type = false;

    public boolean disableOnMedia = true;
    public boolean readMessages = false;
    public boolean disableOnDisconnect = true;

    public sSettingsSource sourceConfig = new sSettingsSource();

    public int reps = -1;
    public int repTime = 5000;

    public int restFrom = 72;
    public int restTo = 80;
    public String restBy = "";
    public int lineOfFile = -1;
    public String specialLine = constants.DATA_ANALYSIS_SPECIALLINE_LAST_LINE;

    public boolean validRequired = false;
    public long validDateTimeFrom = -1;
    public long validDateRange = 5*60;
    public int validFrom = 0;
    public int validTo = 0;
    public int validLineOfFile = -1;
    public int[] dateLocInFile = new int[] {35, 51};

    public int[] timeLocInFile = new int[]{12, 16};

    public ArrayList<String> strengthArray = new ArrayList<String>() {{
        add("90,110,0");
        add("50,89,1");
        add("111,150,1");
        add("0,49,2");
        add("151,200,2");
        add("-50,-1,3");
        add("201,-250,3");

    }};


    sSetting(String name, boolean type, int reps, int repTime, String parentSourceType, String source, String sourcePath, int restFrom, int restTo, boolean disableOnMedia, boolean readMessages, boolean disableOnDisconnect){
        if(!name.contains(constants.CONF_PREFIX)){
            name = constants.CONF_PREFIX + name;
        }
        this.name = name;
        this.type = type;
        this.reps = reps;
        this.repTime = repTime;

        this.sourceConfig.source = source;
        this.sourceConfig.source = sourcePath;
        this.sourceConfig.parentSourceType = parentSourceType;

        this.restFrom = restFrom;
        this.restTo = restTo;
        this.disableOnMedia = disableOnMedia;
        this.readMessages = readMessages;
        this.disableOnDisconnect = disableOnDisconnect;
    }

    sSetting(String name){
        if(!name.contains(constants.CONF_PREFIX)){
            name = constants.CONF_PREFIX + name;
        }
        this.name = name;
    }

    public void setRestiction(int from, int to){
        this.restFrom = from;
        this.restTo = to;
    }

    public String getTypeName(){
        if(type) {
            return constants.ST_CONF_TYPE_FDATA;
        }else{
            return constants.ST_CONF_TYPE_RT_DATA;
        }
    }

    public int[] splitStrenthArray(int numberOfElement){
        int from;
        int to;
        int num;
        from = Integer.parseInt(strengthArray.get(numberOfElement).split(",")[0]);
        to = Integer.parseInt(strengthArray.get(numberOfElement).split(",")[1]);
        num = Integer.parseInt(strengthArray.get(numberOfElement).split(",")[2]);
        return new int[]{from,to,num};
    }
}
