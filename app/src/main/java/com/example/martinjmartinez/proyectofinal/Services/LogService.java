package com.example.martinjmartinez.proyectofinal.Services;

import com.example.martinjmartinez.proyectofinal.App.SmartPLugApplication;
import com.example.martinjmartinez.proyectofinal.Entities.Historial;
import com.example.martinjmartinez.proyectofinal.Entities.Log;
import com.example.martinjmartinez.proyectofinal.Models.LogFB;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.realm.Realm;

public class LogService extends SmartPLugApplication {

    private Realm realm;
    private HistorialService historialService;
    private DatabaseReference databaseReference;

    public LogService (Realm realm) {
        this.realm = realm;
        historialService = new HistorialService(realm);
        databaseReference = FirebaseDatabase.getInstance().getReference("powerLogs");
    }

    public void createLog(LogFB logFB) {
        String _id = databaseReference.push().getKey();
        String historialId = logFB.getHistorialId();
        double power = logFB.getPower();

        databaseReference.child(_id).setValue(logFB);
        Historial historial = historialService.getHistorialById(historialId);

        realm.beginTransaction();

        Log log = realm.createObject(Log.class, _id);

        log.setHistorial(historial);
        log.setPower(power);

        realm.commitTransaction();
    }

    public Log getLogById(String _id) {
        Log log = realm.where(Log.class).equalTo("_id", _id).findFirst();

        return log;
    }
}
