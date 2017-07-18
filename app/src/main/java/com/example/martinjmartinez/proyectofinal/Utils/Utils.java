package com.example.martinjmartinez.proyectofinal.Utils;

import android.app.Activity;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.EditText;

import com.example.martinjmartinez.proyectofinal.R;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by MartinJMartinez on 6/27/2017.
 */
public class Utils {

    public static Handler mHandler;

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
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

    static public boolean isEditTextEmpty(EditText editText) {
        return editText.getText().toString().trim().length() == 0;
    }

    static public AlertDialog.Builder createDialog(Activity activity, String dialog_title, String dialog_message) {
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(dialog_message)
                .setTitle(dialog_title);

        return builder;
    }
}
