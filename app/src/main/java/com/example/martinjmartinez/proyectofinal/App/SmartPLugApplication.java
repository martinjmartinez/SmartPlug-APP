package com.example.martinjmartinez.proyectofinal.App;

import android.app.Application;
import android.content.res.Configuration;

import com.google.firebase.database.DatabaseReference;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class SmartPLugApplication extends Application {

    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!
    @Override
    public void onCreate() {
        super.onCreate();
        // Required initialization logic here!

        initDataBase();
    }

    // Called by the system when the device configuration changes while your component is running.
    // Overriding this method is totally optional!
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    // This is called when the overall system is running low on memory,
    // and would like actively running processes to tighten their belts.
    // Overriding this method is totally optional!
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    public void initDataBase() {
       initLocalDataBase();
    }

    public void initLocalDataBase() {
        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name("SmartplugDB")
                .schemaVersion(1)
                .build();

        Realm.setDefaultConfiguration(realmConfiguration);

        //Delete Database
        Realm.deleteRealm(realmConfiguration);
    }
}
