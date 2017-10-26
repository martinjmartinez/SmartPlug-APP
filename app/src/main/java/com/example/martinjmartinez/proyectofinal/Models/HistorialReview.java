package com.example.martinjmartinez.proyectofinal.Models;

import com.example.martinjmartinez.proyectofinal.Entities.Log;

import java.util.List;

public class HistorialReview {

    private String date;

    private String deviceId;

    private String spaceId;

    private String buildingId;

    private long totalTimeInSeconds;

    private List<Log> powerLog;

    private double powerAverage;

    public HistorialReview(String date, double powerAverage, long totalTimeInSeconds) {
        this.date = date;
        this.powerAverage = powerAverage;
        this.totalTimeInSeconds = totalTimeInSeconds;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getTotalTimeInSeconds() {
        return totalTimeInSeconds;
    }

    public void setTotalTimeInSeconds(long totalTimeInSeconds) {
        this.totalTimeInSeconds = totalTimeInSeconds;
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

    public List<Log> getPowerLog() {
        return powerLog;
    }

    public void setPowerLog(List<Log> powerLog) {
        this.powerLog = powerLog;
    }

    public double getPowerAverage() {
        return powerAverage;
    }

    public void setPowerAverage(double powerAverage) {
        this.powerAverage = powerAverage;
    }
}
