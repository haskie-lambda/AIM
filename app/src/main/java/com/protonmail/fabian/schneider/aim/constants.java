package com.protonmail.fabian.schneider.aim;

/**
 * Created by faebl on 26.12.16.
 */
import android.app.Application;

public class constants extends Application {
    public static final String ST_SATELLITES = "Satellite";
    public static final String ST_ONLINE_SOURCE = "Online-Source";
    public static final String ST_SENSOR_DATA = "Sensor-Data";
    public static final String ST_FILE_SOURCE = "File-Source";
    public static final String PST_ONLINE = "online";
    public static final String PST_OFFLINE = "offline";
    public static final String ST_ERROR = "ERROR";
    public static final String ST_JUST_MESSAGES = "Just Messages";

    public static final String ST_CONF_TYPE_RT_DATA = "RTData";
    public static final String ST_CONF_TYPE_FDATA = "FData";

    public static final String SET_INSTALLER = "installer";

    public static final String SET_INSTALLER_MAIN = "bootMain";
    public static final String SET_INSTALLER_SETTINGS = "bootSett";
    public static final String SET_INSTALLER_CONFIG = "bootConfig";
    public static final String SET_INSTALLER_CONFIG_CAT = "bootConfigCat";

    public static final String SHAREDPREF_CONFIG = "configuration";
    public static final String SHAREDPREF_ACTUAL_CONFIG = "actualConfig";

    public static final String CONF_PREFIX = "conf_";

    public static final String INTENT_EXTRA_ACTUAL_SOURCE_PATH = "com.protonmail.fabian.schneider.aim.actualSourcePath";
    public static final String INTENT_EXTRA_ACTUAL_SENSOR = "com.protonmail.fabian.schneider.aim.actualSensor";
    public static final String INTENT_EXTRA_ACTUAL_STRENGTH_ARRAY = "com.protonmail.fabian.schneider.aim.actualStrengthArray";
    public static final String INTENT_EXTRA_SOURCE_URL = "com.protonmail.fabian.schneider.aim.sourceURL";
    public static final String INTENT_EXTRA_ERROR_PATTERN = "com.protonmail.fabian.schneider.aim.errorPattern";
    public static final String INTENT_EXTRA_DATA_RESTRICTION = "com.protonmail.fabian.schneider.aim.dataRest";
    public static final String INTENT_EXTRA_SATELLITE_CONFIG = "com.protonmail.fabian.schneider.aim.satelliteConfig";
    public static final String INTENT_EXTRA_RETURN = "com.protonmail.fabian.schneider.aim.config_return";
    public static final String INTENT_EXTRA_RETURN_SENSOR = "com.protonmail.fabian.schneider.aim.sensor_return";
    public static final String INTENT_EXTRA_RETURN_SATELLITE = "com.protonmail.fabian.schneider.aim.satellite_return";
    public static final String INTENT_EXTRA_RETURN_SARRAY = "com.protonmail.fabian.schneider.aim.sArray_return";
    public static final String INTENT_EXTRA_ONLINE_LOCAL = "com.protonmail.fabian.schneider.aim.onloc";
    public static final String INTENT_EXTRA_DATA_RESTRICTION_BY = "com.protonmail.fabian.schneider.aim.dataRestBy";
    public static final String INTENT_EXTRA_DATA_LINE_IN_FILE = "com.protonmail.fabian.schneider.aim.dataLineRest";
    public static final String INTENT_EXTRA_RETURN_ONLINEFILE = "com.protonmail.fabian.schneider.aim.return_online";

    public static final String INTENT_FILTER_STRENGTH = "com.protonmail.fabian.schneider.aim.strength";
    public static final String INTENT_FILTER_ADINFO = "com.protonmail.fabian.schneider.aim.adinfo";
    public static final String INTENT_FILTER_ACTUALCONF = "com.protonmail.fabian.schneider.aim.intFilterActualConf";

    public static final String SENSOR_CHECK_TEMP = "Temp";

    public static final String DATA_ANALYSIS_SPECIALLINE_LAST_LINE = "ll";
    public static final String DATA_ANALYSIS_SPECIALLINE_FIRST_LINE = "fl";
    public static final String DATA_ANALYSIS_SPECIALLINE_NO = "no";

    public static final int NOTIFICATION_ID = 11;


}
