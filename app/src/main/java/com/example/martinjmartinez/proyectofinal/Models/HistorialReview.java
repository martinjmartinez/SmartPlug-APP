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

    private double powerConsumed;

    private double averagePower;

    public HistorialReview(String date, double powerConsumed, long totalTimeInSeconds, double averagePower) {
        this.date = date;
        this.powerConsumed = powerConsumed;
        this.totalTimeInSeconds = totalTimeInSeconds;
        this.averagePower = averagePower;
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

    public double getPowerConsumed() {
        return powerConsumed;
    }

    public void setPowerConsumed(double powerConsumed) {
        this.powerConsumed = powerConsumed;
    }

    public double getAveragePower() {
        return averagePower;
    }

    public void setAveragePower(double averagePower) {
        this.averagePower = averagePower;
    }
}
