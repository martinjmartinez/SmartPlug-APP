package com.example.martinjmartinez.proyectofinal.Services;

import com.example.martinjmartinez.proyectofinal.App.SmartPLugApplication;
import com.example.martinjmartinez.proyectofinal.Entities.Device;
import com.example.martinjmartinez.proyectofinal.Entities.Historial;
import com.example.martinjmartinez.proyectofinal.Models.HistorialFB;
import com.example.martinjmartinez.proyectofinal.Utils.DateUtils;
import com.example.martinjmartinez.proyectofinal.Utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.List;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmResults;

public class HistorialService extends SmartPLugApplication {
    private Realm realm;
    private DeviceService deviceService;
    private DevicesLimitService devicesLimitService;
    public DatabaseReference historialDatabaseReference;
    private DatabaseReference deviceDatabaseReference;
    private FirebaseAuth mAuth;

    public HistorialService(Realm realm) {
        this.realm = realm;
        deviceService = new DeviceService(realm);
        devicesLimitService = new DevicesLimitService(realm);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        historialDatabaseReference = FirebaseDatabase.getInstance().getReference("Accounts/" + currentUser.getUid() + "/Histories");
        deviceDatabaseReference = FirebaseDatabase.getInstance().getReference("Accounts/" + currentUser.getUid() + "/Devices");
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
            historialFB = new HistorialFB(historialId, startDate.getTime(), deviceId, device.getBuilding().get_id(), device.getSpace().get_id(), device.getMonthlyLimit());
        } else {
            historialFB = new HistorialFB(historialId, startDate.getTime(), deviceId, device.getBuilding().get_id(), "", device.getMonthlyLimit());
        }

        historialDatabaseReference.child(historialId).setValue(historialFB);
        devicesLimitService.updateOrCreateCloud(historialFB);
        createHistoryLocal(historialFB);
        return historialId;
    }

    public void createHistoryLocal(HistorialFB historialFB) {
        Device device = deviceService.getDeviceById(historialFB.getDeviceId());
        realm.beginTransaction();

        Historial historial = realm.createObject(Historial.class, historialFB.get_id());
        historial.setStartDate(new Date(historialFB.getStartDate()));
        historial.setEndDate(new Date(historialFB.getEndDate()));
        historial.setDevice(device);
        historial.setDeviceLimit(device.getMonthlyLimit());
        historial.setLastLogDate(new Date(historialFB.getLastLogDate()));
        historial.setBuilding(device.getBuilding());
        historial.setSpace(device.getSpace());
        historial.setTotalTimeInSeconds(historialFB.getTotalTimeInSeconds());
        historial.setPowerConsumed(historialFB.getPowerConsumed());
        historial.setPowerAverage(historialFB.getPowerAverage());

        realm.commitTransaction();
    }

    public void closeHistory(HistorialFB historialFB) {
        deviceDatabaseReference.child(historialFB.getDeviceId()).child("lastTimeUsed").setValue(historialFB.getEndDate());
        historialDatabaseReference.child(historialFB.get_id()).child("endDate").setValue(historialFB.getEndDate());

        updateHistorialDataLocal(historialFB);
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
        historialFB.setDeviceLimit(historial.getDeviceLimit());
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

    public void updateOrCreate(HistorialFB historialFB) {
        Historial historial = getHistorialById(historialFB.get_id());
        if (historial != null) {
            updateHistorialDataLocal(historialFB);
        } else {
            createHistoryLocal(historialFB);
        }
    }

    public void createFeakData() {
        Integer id = 0;
        for (Device device : deviceService.allDevices()) {
            Date today = DateUtils.getCurrentDate();
            for (Date firstDate = Utils.firstDayOfPreviousWeek(); firstDate.before(today); firstDate.setDate(firstDate.getDate() + 1)) {
                int time = new Random().nextInt((82800 - 1800) + 1) + 1800;
                double powerAverage = new Random().nextInt((100 - 5) + 1) + 5;
                double consumption = powerAverage * (time * 0.000277778);

                HistorialFB historialFB;
                if (device.getSpace() != null) {
                    historialFB = new HistorialFB(id.toString(), device.get_id(), device.getSpace().get_id(), device.getBuilding().get_id(), firstDate.getTime(), firstDate.getTime(), firstDate.getTime(), time, powerAverage, consumption);
                } else {
                    historialFB = new HistorialFB(id.toString(), device.get_id(), "", device.getBuilding().get_id(), firstDate.getTime(), firstDate.getTime(), firstDate.getTime(), time, powerAverage, consumption);

                }
                updateOrCreate(historialFB);
                id++;
            }
        }
    }

    public void updateHistorialDataLocal(HistorialFB historialFB) {
        Device device = deviceService.getDeviceById(historialFB.getDeviceId());
        Historial historial = getHistorialById(historialFB.get_id());

        realm.beginTransaction();

        historial.setStartDate(new Date(historialFB.getStartDate()));
        historial.setEndDate(new Date(historialFB.getEndDate()));
        historial.setDevice(device);
        historial.setDeviceLimit(device.getMonthlyLimit());
        historial.setLastLogDate(new Date(historialFB.getLastLogDate()));
        historial.setPowerAverage(historialFB.getPowerAverage());
        historial.setBuilding(device.getBuilding());
        if (device.getSpace() != null) {
            historial.setSpace(device.getSpace());
        }

        historial.setTotalTimeInSeconds(historialFB.getTotalTimeInSeconds());
        historial.setPowerConsumed(historialFB.getPowerConsumed());

        realm.commitTransaction();
    }
}
