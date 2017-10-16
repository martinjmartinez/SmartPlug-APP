package com.example.martinjmartinez.proyectofinal.Entities;

import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.LinkingObjects;
import io.realm.annotations.PrimaryKey;

public class Space extends RealmObject {

    @PrimaryKey
    private String _id;

    private String name;

    private Building building;

    private double averageConsumption;

    @LinkingObjects("space")
    private final RealmResults<Device> devices = null;

    private double power;

    public double getPower() {
        return power;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public Space() {}

    public double getAverageConsumption() {
        return averageConsumption;
    }

    public void setAverageConsumption(double averageConsumption) {
        this.averageConsumption = averageConsumption;
    }

    public Space(String _id) {
        this._id = _id;
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

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    public RealmResults<Device> getDevices() {
        return devices;
    }

    @Override
    public String toString() {
        return "{" +
                "\"name\":\"" + name + "\"," +
                "\"building\":\"" + building.get_id() + "\"" +
                '}';
    }
}
