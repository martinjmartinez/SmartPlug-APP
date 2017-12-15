package com.example.martinjmartinez.proyectofinal.Utils;

import android.app.Activity;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.martinjmartinez.proyectofinal.Entities.Settings;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Services.SettingsService;
import com.example.martinjmartinez.proyectofinal.UI.MainActivity.MainActivity;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.realm.Realm;

/**
 * Created by MartinJMartinez on 6/27/2017.
 */
public class Utils {

    public static Handler mHandler;

    public static DecimalFormat decimalFormat = new DecimalFormat("#.#");

    public static ArrayList<String> updateDeviceData(final String url){

        final ArrayList<String> data = new ArrayList<>();

        new Thread(){
            public void run(){
                final JSONObject json = RemoteFetch.getJSON(url);
                if(json == null){
                    mHandler.post(new Runnable(){
                        public void run(){
                            Log.e("Utils.GetData", "No se puede obtener la data");
                        }
                    });
                } else {
                    mHandler.post(new Runnable(){
                        public void run(){
                            try {
                                data.add(json.getString("name").toUpperCase());
                                data.add(json.getJSONObject("variables").get("status").toString());
                                data.add(json.getJSONObject("variables").get("potencia").toString());

                            }catch(Exception e){
                                Log.e("RenderInfo", "One or more fields not found in the JSON data");
                            }
                        }
                    });
                }
            }
        }.start();

        return data;
    }

    static public void loadContentFragment(final Fragment fromFragment,  Fragment toFrament, String toFragmentKey, boolean addToStack) {
        FragmentTransaction fragmentTransaction = fromFragment.getFragmentManager().beginTransaction();

        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        fragmentTransaction.replace(R.id.frame_layout, toFrament, toFragmentKey);
        if (addToStack) {
            fragmentTransaction.addToBackStack(toFrament.getTag());
        }else{
            fromFragment.getFragmentManager().popBackStack();
            fragmentTransaction.addToBackStack(toFrament.getTag());
        }
        fragmentTransaction.commit();
    }

    static public boolean isEditTextEmpty(EditText editText) {
        return editText.getText().toString().trim().length() == 0;
    }

    static public String formatDefaultDate(Date date) {
        String stringDate = SimpleDateFormat.getDateInstance(SimpleDateFormat.DEFAULT).format(date);
        String stringTime = SimpleDateFormat.getTimeInstance(SimpleDateFormat.DEFAULT).format(date);
        return stringDate + " " + stringTime;
    }

    static public String formatSimpleDate(Date date) {
        String stringDate = SimpleDateFormat.getDateInstance(SimpleDateFormat.DEFAULT).format(date);
        return stringDate;
    }

    static public String formatRoutineDays(List<Integer> days) {
        String daysString = "";
        if (days.size() ==7) {
            daysString = "Everyday";
        } else {
            for(Integer integer : days) {
                if (integer == 1) {
                    daysString = daysString + "Sun ";
                } else if (integer == 2) {
                    daysString = daysString + "Mon ";
                }else if (integer == 3) {
                    daysString = daysString + "Tue ";
                }else if (integer == 4) {
                    daysString = daysString + "Wed ";
                }else if (integer == 5) {
                    daysString = daysString + "Thu ";
                }else if (integer == 6) {
                    daysString = daysString + "Fri ";
                }else if (integer == 7) {
                    daysString = daysString + "Sat ";
                }
            }
        }
        return daysString;
    }

    static public String monthStringToId(String monthString) {
       String monthId = monthString.replace(" ", "_");
        return monthId;
    }

    static public String monthIdToString(String monthString) {
        String monthId = monthString.replace("_", " ");
        return monthId;
    }

    static public String getPreviousMonthId(String actualMonthId) {
        String[] parts = actualMonthId.split("_");
        String month = parts[0];
        String year = parts[1];

        String previusMonthId = "";

        switch(month) {
            case "Jan":
                Calendar prevYear = Calendar.getInstance();
                prevYear.set(Calendar.YEAR, Integer.valueOf(year));
                prevYear.add(Calendar.YEAR, -1);

                previusMonthId = "Dec" + "_" + prevYear.get(Calendar.YEAR);
                break;
            case "Feb":
                previusMonthId = "Jan" + "_" + year;
                break;
            case "Mar":
                previusMonthId = "Feb" + "_" + year;
                break;
            case "Apr":
                previusMonthId = "Mar" + "_" + year;
                break;
            case "May":
                previusMonthId = "Apr" + "_" + year;
                break;
            case "Jun":
                previusMonthId = "May" + "_" + year;
                break;
            case "Jul":
                previusMonthId = "Jun" + "_" + year;
                break;
            case "Aug":
                previusMonthId = "Jul" + "_" + year;
                break;
            case "Sep":
                previusMonthId = "Aug" + "_" + year;
                break;
            case "Oct":
                previusMonthId = "Sep" + "_" + year;
                break;
            case "Nov":
                previusMonthId = "Oct" + "_" + year;
                break;
            case "Dec":
                previusMonthId = "Nov" + "_" + year;
                break;
        }

        return previusMonthId;
    }

    static public AlertDialog.Builder createDialog(Activity activity, String dialog_title, String dialog_message) {
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(dialog_message)
                .setTitle(dialog_title);

        return builder;
    }

    static public Double price(double consumption, String userId) {
        SettingsService settingsService = new SettingsService(Realm.getDefaultInstance());
        Settings settings = settingsService.getSettingsById(userId);
        Double consumptionInKWH = consumption/1000;
        Double price = 0.0;
        if (settings != null) {
            if (consumptionInKWH > 700) {
                price = consumptionInKWH * settings.getCat4Price();
            } else if (consumptionInKWH <= 700 && consumptionInKWH >= 301) {
                price = consumptionInKWH * settings.getCat3Price();
            } else if (consumptionInKWH <= 300 && consumptionInKWH >= 201) {
                price = consumptionInKWH * settings.getCat2Price();
            } else if (consumptionInKWH <= 200 && consumptionInKWH >= 0) {
                price = consumptionInKWH * settings.getCat1Price();
            }
        }

        return price;
    }

    static public void setActionBarIcon(Activity activity, boolean isBack) {
        final MainActivity mainActivity = (MainActivity) activity;

        mainActivity.getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(!isBack);
        mainActivity.getActionBarDrawerToggle().setDrawerIndicatorEnabled(!isBack);
        mainActivity.getActionBarDrawerToggle().setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.onBackPressed();
            }
        });

    }


    public static Date getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        return calendar.getTime();
    }

    public static Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    public static Date firstDayOfCurrentWeek() {
        Calendar calendar = setCalendar(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        return Utils.getStartOfDay(calendar.getTime());
    }

    public static Date firstDayOfPreviousWeek() {
        Calendar calendar = setCalendar(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        return Utils.getStartOfDay(moveCalendarToDate(calendar, Calendar.WEEK_OF_YEAR, -1));
    }

    public static Date lastDayOfPreviousWeek() {
        Calendar calendar = setCalendar(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        return Utils.getEndOfDay(moveCalendarToDate(calendar, Calendar.DAY_OF_MONTH, -1));
    }


    public static Date firstDayOfCurrentMonth() {
        Calendar calendar = setCalendar(Calendar.DAY_OF_MONTH, 1);

        return Utils.getStartOfDay(calendar.getTime());
    }


    public static Date firstDayOfPreviousMonth() {
        Calendar calendar = setCalendar(Calendar.DAY_OF_MONTH, 1);

        return Utils.getStartOfDay(moveCalendarToDate(calendar, Calendar.MONTH, -1));
    }


    public static Date lastDayOfPreviousMonth() {
        Calendar calendar = setCalendar(Calendar.DAY_OF_MONTH, 1);

        return Utils.getEndOfDay(moveCalendarToDate(calendar, Calendar.DAY_OF_MONTH, -1));
    }

    private static Calendar setCalendar(int dateField, int setAmount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(dateField, setAmount);

        return calendar;
    }

    private static Date moveCalendarToDate(Calendar calendar, int fieldToMove, int moveAmount) {
        calendar.add(fieldToMove, moveAmount);

        return calendar.getTime();
    }

    public static String getText(TextView textField) {
        return textField.getText().toString();
    }

    public static void setText(TextView textView, String text) {
        if (!Utils.getText(textView).equals(text)) textView.setText(text);
    }

    public static String formatDateFromString(String stringDate, String inputFormat, String outputFormat) {
        String formattedDate = stringDate;

        try {
            Date date = DateUtils.parse(inputFormat, stringDate);
            formattedDate = DateUtils.format(outputFormat, date);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        return formattedDate;
    }
}
