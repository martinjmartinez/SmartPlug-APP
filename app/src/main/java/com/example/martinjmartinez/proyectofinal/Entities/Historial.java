package com.example.martinjmartinez.proyectofinal.Entities;


import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Historial extends RealmObject {

    @PrimaryKey
    private String _id;

    private Device device;

    private Date startDate;

    private Date endDate;

    private double totalHours;

    private RealmList<Log> powerLog;

    private double powerAverage;

    public Historial() {}

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

    public double getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(double totalHours) {
        this.totalHours = totalHours;
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
