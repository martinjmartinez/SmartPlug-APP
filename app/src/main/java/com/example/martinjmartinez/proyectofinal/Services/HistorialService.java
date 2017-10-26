package com.example.martinjmartinez.proyectofinal.Services;

import com.example.martinjmartinez.proyectofinal.App.SmartPLugApplication;
import com.example.martinjmartinez.proyectofinal.Entities.Device;
import com.example.martinjmartinez.proyectofinal.Entities.Historial;
import com.example.martinjmartinez.proyectofinal.Entities.Log;
import com.example.martinjmartinez.proyectofinal.Utils.DateUtils;

import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class HistorialService extends SmartPLugApplication {
    private Realm realm;
    private DeviceService deviceService;

    public HistorialService (Realm realm) {
        this.realm = realm;
        deviceService = new DeviceService(realm);
    }

    public List<Historial> allHistorial() {
        RealmResults<Historial> results = realm.where(Historial.class).findAll();

        return results;
    }

    public void createHistorial(String _id, Date startDate, Date endDate, String deviceId, RealmList<Log> powerLog, double powerAverage) {
        Device device = deviceService.getDeviceById(deviceId);
        long secs = 0;
        if (powerLog.size() != 0) {
            secs = (endDate.getTime() - startDate.getTime()) / 1000;
            DateUtils.timeFormatter(secs);
        }
        realm.beginTransaction();

        Historial historial = realm.createObject(Historial.class, _id);
        historial.setStartDate(startDate);
        historial.setEndDate(endDate);
        historial.setDevice(device);
        historial.setPowerLog(powerLog);
        historial.setPowerAverage(powerAverage);
        historial.setBuilding(device.getBuilding());
        historial.setSpace(device.getSpace());
        historial.setTotalTimeInSeconds(secs);

        realm.commitTransaction();
    }

    public void startHistorial(String _id, Date startDate, String deviceId) {
        Device device = deviceService.getDeviceById(deviceId);

        realm.beginTransaction();

        Historial historial = realm.createObject(Historial.class, _id);
        historial.setStartDate(startDate);
        historial.setDevice(device);
        historial.setBuilding(device.getBuilding());
        historial.setSpace(device.getSpace());

        realm.commitTransaction();
    }

    public void updateOrCreateHistorial(String _id, Date startDate, Date endDate, String deviceId, RealmList<Log> powerLog, double powerAverage) {
        if(getHistorialById(_id) != null) {
            updateHistorial(_id, startDate, endDate, deviceId, powerLog, powerAverage);
        } else {
            createHistorial(_id, startDate, endDate, deviceId, powerLog, powerAverage);
        }
    }

    public Historial getHistorialById(String _id) {
        Historial historial = realm.where(Historial.class).equalTo("_id", _id).findFirst();

        return historial;
    }

    public void updateHistorialEndDate(String _id, Date endDate, RealmList<Log> powerLog, double powerAverage) {
        Historial historial = getHistorialById(_id);

        realm.beginTransaction();

        historial.setEndDate(endDate);
        historial.setPowerLog(powerLog);
        historial.setPowerAverage(powerAverage);

        realm.commitTransaction();
    }

    public void updateHistorial(String _id, Date startDate, Date endDate, String deviceId, RealmList<Log> powerLog, double powerAverage) {
        Historial historial = getHistorialById(_id);
        Device device = deviceService.getDeviceById(deviceId);

        double totalHours = 0;
        if (powerLog.size() != 0) {
            long secs = (endDate.getTime() - startDate.getTime()) / 1000;
            totalHours= secs;
        }
        realm.beginTransaction();

        historial.setStartDate(startDate);
        historial.setEndDate(endDate);
        historial.setDevice(device);
        historial.setPowerLog(powerLog);
        historial.setPowerAverage(powerAverage);
        historial.setBuilding(device.getBuilding());
        historial.setSpace(device.getSpace());
        historial.setTotalTimeInSeconds(totalHours);

        realm.commitTransaction();
    }
}
