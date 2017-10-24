package com.example.martinjmartinez.proyectofinal.Entities;


import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Historial extends RealmObject {

    @PrimaryKey
    private String _id;

    private Device device;

    private Space space;

    private Building building;

    private Date startDate;

    private Date endDate;

    private double totalTimeInSeconds;

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

    public double getTotalTimeInSeconds() {
        return totalTimeInSeconds;
    }

    public void setTotalTimeInSeconds(double totalTimeInSeconds) {
        this.totalTimeInSeconds = totalTimeInSeconds;
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

    public Space getSpace() {
        return space;
    }

    public void setSpace(Space space) {
        this.space = space;
    }

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
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
