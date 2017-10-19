package com.example.martinjmartinez.proyectofinal.Utils;

/**
 * Created by MartinJMartinez on 7/17/2017.
 */

public class Constants {

    static public String BUILDING_QUERY = "https://smartplug-api.herokuapp.com/buildings";
    static public String SPACE_QUERY = "https://smartplug-api.herokuapp.com/spaces";
    static public String DEVICE_QUERY = "https://smartplug-api.herokuapp.com/devices";
    static public String HISTORY_QUERY = "https://smartplug-api.herokuapp.com/histories";

    static public String QUERY = "QUERY";
    static public String BUILDING_ID = "BUILDING_ID";
    static public String DEVICE_ID = "DEVICE_ID";
    static public String SPACE_ID = "SPACE_ID";

    static public String ON_QUERY = "/digital/2/0";
    static public String OFF_QUERY = "/digital/2/1";

    /**
     * Holds date formats.
     */
    public static final String DATE_FORMAT_SHORT = "dd/MM/yyyy";
    public static final String DATE_FORMAT_WITH_TIME = "dd/MM/yyyy hh:mm a";
    public static final String DATE_FORMAT_CND_INVOICE = "yyyyMMdd";
    public static final String DATE_FORMAT_DEFAULT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_DEFAULT_WITH_SEPARATOR = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String TIME_FORMAT_TWENTY_FOUR_HOURS = "HH:mm";
    public static final String TIME_FORMAT_TWELVE_HOURS = "hh:mm a";
}
