package com.example.martinjmartinez.proyectofinal.Services;

import com.example.martinjmartinez.proyectofinal.App.SmartPLugApplication;
import com.example.martinjmartinez.proyectofinal.Entities.Building;
import com.example.martinjmartinez.proyectofinal.Entities.Device;
import com.example.martinjmartinez.proyectofinal.Entities.Log;
import com.example.martinjmartinez.proyectofinal.Entities.Space;

import io.realm.Realm;

public class LogService extends SmartPLugApplication {

    private Realm realm;

    public LogService (Realm realm) {
        this.realm = realm;
    }

    public void createLog(String _id, double power) {
        realm.beginTransaction();

        Log log = realm.createObject(Log.class, _id);

        log.setPower(power);

        realm.commitTransaction();
    }

    public Log getLogById(String _id) {
        Log log = realm.where(Log.class).equalTo("_id", _id).findFirst();

        return log;
    }

    public void updateOrCreateLog(String _id, double power) {
        if(getLogById(_id) != null) {
            updateLog(_id, power);
        } else {
            createLog(_id, power);
        }
    }

    public void updateLog(String _id, double power) {
        Log log = getLogById(_id);

        realm.beginTransaction();

        log.setPower(power);

        realm.commitTransaction();
    }
}
