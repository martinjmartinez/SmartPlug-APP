package com.example.martinjmartinez.proyectofinal.Models;

import com.example.martinjmartinez.proyectofinal.Entities.Log;

import java.util.ArrayList;

/**
 * Created by MartinJMartinez on 10/30/2017.
 */

public class HistorialFB {

    private String _id;

    private String deviceId;

    private String spaceId;

    private String buildingId;

    private long startDate;

    private long endDate;

    private double totalTimeInSeconds;

    private ArrayList<Log> powerLog;

    private double powerAverage;

    private double powerConsumed;

    public HistorialFB() {
    }

    public HistorialFB(String _id, String deviceId, String spaceId, String buildingId, long startDate, long endDate, double totalTimeInSeconds, ArrayList<Log> powerLog, double powerAverage, double powerConsumed) {
        this._id = _id;
        this.deviceId = deviceId;
        this.spaceId = spaceId;
        this.buildingId = buildingId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalTimeInSeconds = totalTimeInSeconds;
        this.powerLog = powerLog;
        this.powerAverage = powerAverage;
        this.powerConsumed = powerConsumed;
    }

    public HistorialFB(String _id, long startDate, String deviceId) {
        this._id = _id;
        this.deviceId = deviceId;
        this.startDate = startDate;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public double getTotalTimeInSeconds() {
        return totalTimeInSeconds;
    }

    public void setTotalTimeInSeconds(double totalTimeInSeconds) {
        this.totalTimeInSeconds = totalTimeInSeconds;
    }

    public double getPowerAverage() {
        return powerAverage;
    }

    public void setPowerAverage(double powerAverage) {
        this.powerAverage = powerAverage;
    }

    public double getPowerConsumed() {
        return powerConsumed;
    }

    public void setPowerConsumed(double powerConsumed) {
        this.powerConsumed = powerConsumed;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }

    public String getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(String buildingId) {
        this.buildingId = buildingId;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public ArrayList<Log> getPowerLog() {
        return powerLog;
    }

    public void setPowerLog(ArrayList<Log> powerLog) {
        this.powerLog = powerLog;
    }
}
