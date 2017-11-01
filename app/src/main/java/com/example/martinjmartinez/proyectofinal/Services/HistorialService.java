package com.example.martinjmartinez.proyectofinal.Services;

import com.example.martinjmartinez.proyectofinal.App.SmartPLugApplication;
import com.example.martinjmartinez.proyectofinal.Entities.Device;
import com.example.martinjmartinez.proyectofinal.Entities.Historial;
import com.example.martinjmartinez.proyectofinal.Entities.Log;
import com.example.martinjmartinez.proyectofinal.Models.HistorialFB;
import com.example.martinjmartinez.proyectofinal.Models.LogFB;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class HistorialService extends SmartPLugApplication {
    private Realm realm;
    private DeviceService deviceService;
    private DatabaseReference historialDatabaseReference;
    private DatabaseReference deviceDatabaseReference;

    public HistorialService(Realm realm) {
        this.realm = realm;
        deviceService = new DeviceService(realm);
        historialDatabaseReference = FirebaseDatabase.getInstance().getReference("Histories");
        deviceDatabaseReference = FirebaseDatabase.getInstance().getReference("Devices");
    }

    public List<Historial> allHistorial() {
        RealmResults<Historial> results = realm.where(Historial.class).findAll();

        return results;
    }

//    public void createHistorial(Date startDate, Date endDate, String deviceId, double powerAverage) {
//        Device device = deviceService.getDeviceById(deviceId);
//        long secs = 0;
//        double powerConsumed = 0;
//
//        if (powerLog.size() != 0) {
//            secs = (endDate.getTime() - startDate.getTime()) / 1000;
//            powerConsumed = powerAverage * (secs * 0.000277778);
//        }
//
//        String historialId = historialDatabaseReference.push().getKey();
//        HistorialFB historialFB = new HistorialFB(
//                historialId,
//                deviceId,
//                device.getSpace().get_id(),
//                device.getBuilding().get_id(),
//                startDate.getTime(),
//                endDate.getTime(),
//                secs,
//                new ArrayList<>(powerLog),
//                powerAverage,
//                powerConsumed);
//
//        historialDatabaseReference.child(historialId).setValue(historialFB);
//
//        realm.beginTransaction();
//
//        Historial historial = realm.createObject(Historial.class, historialId);
//        historial.setStartDate(startDate);
//        historial.setEndDate(endDate);
//        historial.setDevice(device);
//        historial.setPowerLog(powerLog);
//        historial.setPowerAverage(powerAverage);
//        historial.setBuilding(device.getBuilding());
//        historial.setSpace(device.getSpace());
//        historial.setTotalTimeInSeconds(secs);
//        historial.setPowerConsumed(powerConsumed);
//
//        realm.commitTransaction();
//    }

    public String startHistorial(Date startDate, String deviceId) {
        Device device = deviceService.getDeviceById(deviceId);
        String historialId = historialDatabaseReference.push().getKey();

        HistorialFB historialFB = new HistorialFB(historialId, startDate.getTime(), deviceId);
        historialDatabaseReference.child(historialId).setValue(historialFB);

        realm.beginTransaction();

        Historial historial = realm.createObject(Historial.class, historialId);
        historial.setStartDate(startDate);
        historial.setDevice(device);
        historial.setBuilding(device.getBuilding());
        historial.setSpace(device.getSpace());

        realm.commitTransaction();

        return historialId;
    }

//    public void updateOrCreateHistorial(String _id, Date startDate, Date endDate, String deviceId, RealmList<Log> powerLog, double powerAverage) {
//        if (getHistorialById(_id) != null) {
//            updateHistorial(_id, startDate, endDate, deviceId, powerLog, powerAverage);
//        } else {
//            createHistorial(startDate, endDate, deviceId, powerLog, powerAverage);
//        }
//    }

    public Historial getHistorialById(String _id) {
        Historial historial = realm.where(Historial.class).equalTo("_id", _id).findFirst();

        return historial;
    }

    public void updateHistorialEndDate(final String _id, final Date endDate) {
        final Historial historial = getHistorialById(_id);
        final ArrayList<Log> logs = new ArrayList<>();

        historialDatabaseReference.child(_id).child("powerLogs").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                LogFB log = dataSnapshot.getValue(LogFB.class);
                logs.add(new Log(log.getPower()));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        historialDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                updateHistorials(historial.get_id(), historial.getStartDate(), endDate, historial.getDevice().get_id(), historial.getPowerAverage(), logs);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void updateHistorial(final String _id, final Date startDate, final Date endDate, final String deviceId, final double powerAverage) {
        android.util.Log.e("Entro", "updateHistorial");
        final ArrayList<Log> logs = new ArrayList<>();

        historialDatabaseReference.child(_id).child("powerLogs").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                LogFB log = dataSnapshot.getValue(LogFB.class);
                logs.add(new Log(log.getPower()));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        historialDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                updateHistorials(_id, startDate, endDate, deviceId, powerAverage, logs);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void updateHistorials(String _id, Date startDate, Date endDate, String deviceId, double powerAverage, ArrayList<Log> logs) {
        Historial historial = getHistorialById(_id);
        Device device = deviceService.getDeviceById(deviceId);

        double sum = 0;
        double average = 0;
        long secs = 0;
        double powerConsumed = 0;

        if (logs.size() != 0) {
            for (Log log : logs) {
                sum = log.getPower() + sum;
            }

            average = sum / logs.size();
            secs = (endDate.getTime() - startDate.getTime()) / 1000;
            powerConsumed = average * (secs * 0.000277778);
        }

        historialDatabaseReference.child(_id).child("totalTimeInSeconds").setValue(secs);
        historialDatabaseReference.child(_id).child("powerConsumed").setValue(powerConsumed);
        historialDatabaseReference.child(_id).child("powerAverage").setValue(average);

        realm.beginTransaction();

        historial.setStartDate(startDate);
        historial.setEndDate(endDate);
        historial.setDevice(device);
        historial.getPowerLog().addAll(logs);
        historial.setPowerAverage(average);
        historial.setBuilding(device.getBuilding());
        historial.setSpace(device.getSpace());
        historial.setTotalTimeInSeconds(secs);
        historial.setPowerConsumed(powerConsumed);

        realm.commitTransaction();
    }
}
