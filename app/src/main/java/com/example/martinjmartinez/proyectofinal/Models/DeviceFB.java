package com.example.martinjmartinez.proyectofinal.Models;

public class DeviceFB {

    private String _id;

    private String name;

    private boolean status;

    private String spaceId;

    private boolean isActive;

    private String buildingId;

    private double power;

    private String lastHistoryId;

    private double averageConsumption;

    private long lastTimeUsed;

    public DeviceFB() {
    }

    public DeviceFB(String _id, String name, boolean status, String spaceId, boolean isActive, String buildingId, double averageConsumption, double power) {
        this._id = _id;
        this.name = name;
        this.status = status;
        this.spaceId = spaceId;
        this.isActive = isActive;
        this.buildingId = buildingId;
        this.averageConsumption = averageConsumption;
        this.power = power;
    }

    public long getLastTimeUsed() {
        return lastTimeUsed;
    }

    public void setLastTimeUsed(long lastTimeUsed) {
        this.lastTimeUsed = lastTimeUsed;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public double getPower() {
        return power;
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

    public double getAverageConsumption() {
        return averageConsumption;
    }

    public void setAverageConsumption(double averageConsumption) {
        this.averageConsumption = averageConsumption;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public String getLastHistoryId() {
        return lastHistoryId;
    }

    public void setLastHistoryId(String lastHistoryId) {
        this.lastHistoryId = lastHistoryId;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

}
