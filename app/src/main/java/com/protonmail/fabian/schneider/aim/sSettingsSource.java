package com.protonmail.fabian.schneider.aim;

/**
 * Created by faebl on 26.12.16.
 */

public class sSettingsSource {
    public String parentSourceType = constants.PST_ONLINE;
    public String sourceType = constants.ST_SATELLITES;
    public String source = "http://services.swpc.noaa.gov/text/goes-magnetometer-primary.txt";
    public String errorPattern = "-1.00e+05";
/*
    sSettingsSource(String parentSourceType, String sourceType, String source, String errorPattern){
        this.parentSourceType = parentSourceType;
        this.sourceType = sourceType;
        this.source = source;
        this.errorPattern = errorPattern;
    }*/
}
