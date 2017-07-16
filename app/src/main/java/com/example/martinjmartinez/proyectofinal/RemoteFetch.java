package com.example.martinjmartinez.proyectofinal;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by MartinJMartinez on 6/19/2017.
 */

public class RemoteFetch {

    public static JSONObject getJSON(String urlString){
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp="";
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());


            return data;
        }catch(Exception e){
            return null;
        }
    }
}
