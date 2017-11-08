package com.example.martinjmartinez.proyectofinal.Services;

import com.example.martinjmartinez.proyectofinal.App.SmartPLugApplication;
import com.example.martinjmartinez.proyectofinal.Entities.Device;
import com.example.martinjmartinez.proyectofinal.Entities.Historial;
import com.example.martinjmartinez.proyectofinal.Models.HistorialFB;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class HistorialService extends SmartPLugApplication {
    private Realm realm;
    private DeviceService deviceService;
    public DatabaseReference historialDatabaseReference;
    private DatabaseReference deviceDatabaseReference;
    private DatabaseReference powerLogsDatabaseReference;

    public HistorialService(Realm realm) {
        this.realm = realm;
        deviceService = new DeviceService(realm);
        historialDatabaseReference = FirebaseDatabase.getInstance().getReference("Histories");
        deviceDatabaseReference = FirebaseDatabase.getInstance().getReference("Devices");
        powerLogsDatabaseReference = FirebaseDatabase.getInstance().getReference("powerLogs");
    }

    public List<Historial> allHistorial() {
        RealmResults<Historial> results = realm.where(Historial.class).findAll();

        return results;
    }

    public String startHistorial(Date startDate, String deviceId) {
        Device device = deviceService.getDeviceById(deviceId);
        String historialId = historialDatabaseReference.push().getKey();
        HistorialFB historialFB;

        if (device.getSpace() != null) {
            historialFB = new HistorialFB(historialId, startDate.getTime(), deviceId, device.getBuilding().get_id(), device.getSpace().get_id());
        } else {
            historialFB = new HistorialFB(historialId, startDate.getTime(), deviceId, device.getBuilding().get_id(), "");
        }

        historialDatabaseReference.child(historialId).setValue(historialFB);
        createHistory(historialFB);
        return historialId;
    }

    public void createHistory(HistorialFB historialFB) {
        Device device = deviceService.getDeviceById(historialFB.getDeviceId());
        realm.beginTransaction();

        Historial historial = realm.createObject(Historial.class, historialFB.get_id());
        historial.setStartDate(new Date(historialFB.getStartDate()));
        historial.setDevice(device);
        historial.setBuilding(device.getBuilding());
        historial.setSpace(device.getSpace());

        realm.commitTransaction();
    }

    public void closeHistory(HistorialFB historialFB) {
        //TODO determinar que endDate utilizar al momento de apagarlo
//
//        android.util.Log.e("average", historialFB.getPowerAverage() + "    000");
//        double secs = (historialFB.getEndDate() - historialFB.getStartDate()) / 1000;
//        double powerConsumed = historialFB.getPowerAverage() * (secs * 0.000277778);

        deviceDatabaseReference.child(historialFB.getDeviceId()).child("lastTimeUsed").setValue(historialFB.getEndDate());
        //historialDatabaseReference.child(historialFB.get_id()).child("powerConsumed").setValue(powerConsumed);
        historialDatabaseReference.child(historialFB.get_id()).child("endDate").setValue(historialFB.getEndDate());

        updateHistorialDataLocallty(historialFB);
    }


    public Historial getHistorialById(String _id) {
        Historial historial = realm.where(Historial.class).equalTo("_id", _id).findFirst();

        return historial;
    }

    public HistorialFB castToHistorialFB(final Historial historial, Date endDate) {
        HistorialFB historialFB = new HistorialFB();

        historialFB.set_id(historial.get_id());
        historialFB.setDeviceId(historial.getDevice().get_id());
        if (historial.getSpace() != null) {
            historialFB.setSpaceId(historial.getSpace().get_id());
        }

        historialFB.setBuildingId(historial.getBuilding().get_id());
        historialFB.setEndDate(endDate.getTime());
        historialFB.setPowerAverage(historial.getPowerAverage());
        historialFB.setStartDate(historial.getStartDate().getTime());

        return historialFB;
    }

    public HistorialFB castToHistorialFB(final Historial historial) {
        HistorialFB historialFB = new HistorialFB();

        historialFB.set_id(historial.get_id());
        historialFB.setDeviceId(historial.getDevice().get_id());
        if (historial.getSpace() != null) {
            historialFB.setSpaceId(historial.getSpace().get_id());
        }

        historialFB.setBuildingId(historial.getBuilding().get_id());
        historialFB.setStartDate(historial.getStartDate().getTime());

        return historialFB;
    }

    public void updateHistorialDataLocallty(HistorialFB historialFB) {
        Device device = deviceService.getDeviceById(historialFB.getDeviceId());
        Historial historial = getHistorialById(historialFB.get_id());

        realm.beginTransaction();

        device.setLastTimeUsed(new Date(historialFB.getEndDate()));
        historial.setStartDate(new Date(historialFB.getStartDate()));
        historial.setEndDate(new Date(historialFB.getEndDate()));
        historial.setDevice(device);
        historial.setLastLogDate(new Date(historialFB.getLastLogDate()));
        historial.setPowerAverage(historialFB.getPowerAverage());
        historial.setBuilding(device.getBuilding());
        if (device.getSpace() != null) {
            historial.setSpace(device.getSpace());
        }

        historial.setTotalTimeInSeconds(historialFB.getTotalTimeInSeconds());
        historial.setPowerConsumed(historialFB.getPowerConsumed());

        realm.commitTransaction();

        deviceService.updateDevicePowerAverageConsumption(device.get_id());
    }
}
