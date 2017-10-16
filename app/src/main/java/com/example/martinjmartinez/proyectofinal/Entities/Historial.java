package com.example.martinjmartinez.proyectofinal.Entities;


import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Historial extends RealmObject {

    @PrimaryKey
    private String _id;

    private Device device;

    private long startDate;

    private long endDate;

    private double totalHours;

    private RealmList<Log> powerLog;

    private double powerAverage;

    public Historial() {}

    public Historial(String _id, Device device, Date startDate, Date endDate, double totalHours, RealmList<Log> powerLog) {
        this._id = _id;
        this.device = device;
        this.startDate = startDate.getTime();
        this.endDate = endDate.getTime();
        this.totalHours = totalHours;
        this.powerLog = powerLog;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate.getTime();
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate.getTime();
    }

    public double getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(double totalHours) {
        this.totalHours = totalHours;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public RealmList<Log> getPowerLog() {
        return powerLog;
    }

    public void setPowerLog(RealmList<Log> powerLog) {
        this.powerLog = powerLog;
    }

    public double getPowerAverage() {
        return powerAverage;
    }

    public void setPowerAverage(double powerAverage) {
        this.powerAverage = powerAverage;
    }
}
