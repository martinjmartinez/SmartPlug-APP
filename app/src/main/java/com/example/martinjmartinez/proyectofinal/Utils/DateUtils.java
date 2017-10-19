package com.example.martinjmartinez.proyectofinal.Utils;


import android.support.v4.util.ArrayMap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public final class DateUtils {

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
}