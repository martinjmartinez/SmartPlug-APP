package com.example.martinjmartinez.proyectofinal.Utils;


import android.support.v4.util.ArrayMap;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public final class DateUtils {

    public static final String DATE_FORMAT_SHORT = "dd/MM/yyyy";
    public static final String DATE_FORMAT_WITH_TIME = "dd/MM/yyyy hh:mm a";
    public static final String DATE_FORMAT_CND_INVOICE = "yyyyMMdd";
    public static final String DATE_FORMAT_DEFAULT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_DEFAULT_WITH_SEPARATOR = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String TIME_FORMAT_TWENTY_FOUR_HOURS = "HH:mm:ss";
    public static final String TIME_FORMAT_TWELVE_HOURS = "hh:mm a";

    private static ArrayMap<String, SimpleDateFormat> mDateFormatter;

    static {
        mDateFormatter = new ArrayMap<>();
    }

    private DateUtils() {
    }


    public static String format(String format, Date date) {
        setUpDateFormatter(format);

        return mDateFormatter.get(format).format(date);
    }

    public static Date parse(String format, String date) throws ParseException {
        setUpDateFormatter(format);

        return mDateFormatter.get(format).parse(date);
    }

    private static void setUpDateFormatter(String format) {
        if (mDateFormatter.get(format) == null) {
            mDateFormatter.put(format, new SimpleDateFormat(format, Locale.US));
        }
    }

    public static Date getCurrentDate() {
        return Calendar.getInstance().getTime();
    }

    public static Date getStartOfDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY,00);
        calendar.set(Calendar.MINUTE,00);
        calendar.set(Calendar.SECOND,00);

        return calendar.getTime();
    }

    public static String multiLineMediumFormat(String dateString){
        DateFormat outputFormat = new SimpleDateFormat("MMM d,\n yyyy");
        DateFormat inputFormat = new SimpleDateFormat("MMM d, yyyy");
        Date date = new Date();
        try {
            date = inputFormat.parse(dateString);
        }catch (ParseException ex) {
            Log.e("ParseEx", ex.getMessage());
        }

        return outputFormat.format(date);
    }

    public static String timeFormatter(long seconds){
        String elapsedTime = android.text.format.DateUtils.formatElapsedTime(seconds);

        if (elapsedTime.length() == 5) {
            if (elapsedTime.startsWith("00:")) {
                elapsedTime = elapsedTime + " S";
            } else {
                elapsedTime = elapsedTime + " M";
            }
        } else {
            elapsedTime = elapsedTime + " H";
        }

        return  elapsedTime;
    }
}