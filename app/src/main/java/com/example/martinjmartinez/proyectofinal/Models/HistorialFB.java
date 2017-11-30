package com.example.martinjmartinez.proyectofinal.Models;

public class HistorialFB {

    private String _id;

    private String deviceId;

    private String spaceId;

    private String buildingId;

    private long startDate;

    private long endDate;

    private int numberOfLogs;

    private double deviceLimit;

    private long lastLogDate;

    private double sumOfLogs;

    private double totalTimeInSeconds;

    private double powerAverage;

    private double powerConsumed;

    public HistorialFB() {
    }

    public HistorialFB(String _id, String deviceId, long startDate, long endDate, double powerAverage) {
        this._id = _id;
        this.deviceId = deviceId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.powerAverage = powerAverage;

    }

    public HistorialFB(String _id, long startDate, String deviceId, String buildingId, String spaceId, double deviceLimit) {
        this._id = _id;
        this.deviceId = deviceId;
        this.startDate = startDate;
        this.buildingId = buildingId;
        this.spaceId = spaceId;
        this.deviceLimit = deviceLimit;
    }

    public HistorialFB(String id, String deviceId, String spaceId, String buildingId, long startDate, long endDate, long lastLogDate, double totalTimeInSeconds, double powerAverage, double powerConsumed) {
        this._id = id;
        this.deviceId = deviceId;
        this.spaceId = spaceId;
        this.buildingId = buildingId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.lastLogDate = lastLogDate;
        this.totalTimeInSeconds = totalTimeInSeconds;
        this.powerAverage = powerAverage;
        this.powerConsumed = powerConsumed;
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

    public double getDeviceLimit() {
        return deviceLimit;
    }

    public void setDeviceLimit(double deviceLimit) {
        this.deviceLimit = deviceLimit;
    }

    public int getNumberOfLogs() {
        return numberOfLogs;
    }

    public void setNumberOfLogs(int numberOfLogs) {
        this.numberOfLogs = numberOfLogs;
    }

    public double getSumOfLogs() {
        return sumOfLogs;
    }

    public void setSumOfLogs(double sumOfLogs) {
        this.sumOfLogs = sumOfLogs;
    }

    public long getLastLogDate() {
        return lastLogDate;
    }

    public void setLastLogDate(long lastLogDate) {
        this.lastLogDate = lastLogDate;
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

}
