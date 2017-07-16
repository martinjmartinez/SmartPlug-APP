package com.example.martinjmartinez.proyectofinal.Utils;

import android.content.Context;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.RemoteFetch;

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

    static public void loadContentFragment(final Fragment fromFragment,  Fragment toFrament, String toFragmentKey) {
        FragmentTransaction fragmentTransaction = fromFragment.getFragmentManager().beginTransaction();

        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        fragmentTransaction.replace(R.id.frame_layout, toFrament, toFragmentKey);
        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commitAllowingStateLoss();
    }
}
