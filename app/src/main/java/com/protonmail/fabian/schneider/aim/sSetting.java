package com.protonmail.fabian.schneider.aim;

/**
 * Created by faebl on 05.12.16.
 *
 *
 *
 *
 */
import java.io.Serializable;

public class sSetting implements Serializable {
    public String name;
    public boolean type = false;

    public boolean disableOnMedia = true;
    public boolean readMessages = false;
    public boolean disableOnDisconnect = true;

    public static sSettingsSource sourceConfig;

    static {
        sourceConfig = new sSettingsSource(constants.PST_ONLINE, constants.ST_SATELLITES,"http://services.swpc.noaa.gov/text/goes-magnetometer-primary.txt","-1.00e+05");
    }
    //public String source = "Satellite";
    //public String sourcePath = "http://services.swpc.noaa.gov/text/goes-magnetometer-primary.txt";
    //public String errorPattern = "-1.00e+05";

    public int reps = -1;
    public int repTime = 5000;

    public int restFrom = 72;
    public int restTo = 80;
    public int lineOfFile = -1;
    public String specialLine = constants.DATA_ANALYSIS_SPECIALLINE_LAST_LINE;

    public boolean validRequired = true;
    public long validDateTimeFrom = -1;
    public long validDateRange = 5*60;
    public static int[] dateLocInFile = new int[2];
    static{
        dateLocInFile[0] = 35;
        dateLocInFile[1] = 51;
    }
    public static int[] timeLocInFile = new int[2];
    static {
        timeLocInFile[0] = 12;
        timeLocInFile[1] = 16;
    }

    public static String[] strengthArray;
    static{
        strengthArray = new String[7];
        strengthArray[0] = "90,110,0";
        strengthArray[1] = "50,89,1";
        strengthArray[2] = "111,150,1";
        strengthArray[3] = "0,49,2";
        strengthArray[4] = "151,200,2";
        strengthArray[5] = "-50,-1,3";
        strengthArray[6] = "201,-250,3";

    }


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
        from = Integer.parseInt(strengthArray[numberOfElement].split(",")[0]);
        to = Integer.parseInt(strengthArray[numberOfElement].split(",")[1]);
        num = Integer.parseInt(strengthArray[numberOfElement].split(",")[2]);
        return new int[]{from,to,num};
    }
}
