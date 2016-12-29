package com.protonmail.fabian.schneider.aim;

/**
 * Created by faebl on 26.12.16.
 */

public class sSettingsSource {
    public String parentSourceType;
    public String sourceType;
    public String source;
    public String errorPattern;

    sSettingsSource(String parentSourceType, String sourceType, String source, String errorPattern){
        this.parentSourceType = parentSourceType;
        this.sourceType = sourceType;
        this.source = source;
        this.errorPattern = errorPattern;
    }
}
