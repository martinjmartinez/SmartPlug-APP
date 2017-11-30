package com.example.martinjmartinez.proyectofinal.Models;


public class SpaceFB {

    private String _id;

    private String name;

    private String buildingId;

    private double averageConsumption;

    private boolean isActive;

    public SpaceFB(String _id, String name, String buildingId, boolean isActive) {
        this._id = _id;
        this.name = name;
        this.buildingId = buildingId;
        this.isActive = isActive;
    }

    public double getAverageConsumption() {
        return averageConsumption;
    }

    public void setAverageConsumption(double averageConsumption) {
        this.averageConsumption = averageConsumption;
    }

    public SpaceFB() {
    }

    public String getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(String buildingId) {
        this.buildingId = buildingId;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
