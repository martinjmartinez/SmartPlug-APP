package com.example.martinjmartinez.proyectofinal.Models;

public class BuildingFB {

    private String _id;

    private String name;

    private String uid;

    private double monthlyLimit;

    private boolean isActive;

    public BuildingFB(String _id, String name, boolean isActive, String uid, double monthlyLimit) {
        this._id = _id;
        this.name = name;
        this.isActive = isActive;
        this.uid = uid;
        this.monthlyLimit = monthlyLimit;
    }

    public BuildingFB() {
    }

    public double getMonthlyLimit() {
        return monthlyLimit;
    }

    public void setMonthlyLimit(double monthlyLimit) {
        this.monthlyLimit = monthlyLimit;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    @Override
    public String toString() {
        return "{\"name\":\"" + getName()+"\"}";
    }

    public String toIsActiveString() {
        return "{\"isActive\":\"" + isActive()+"\"}";
    }


}
