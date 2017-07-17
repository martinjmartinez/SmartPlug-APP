package com.example.martinjmartinez.proyectofinal.Entities;

import java.util.List;

/**
 * Created by MartinJMartinez on 6/20/2017.
 */

public class Space {

    private String _id;

    private String name;

    private Building building;

    private List<Device> devices;

    public Space() {}

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

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    @Override
    public String toString() {
        return "{" +
                "\"name\":\"" + name + "\"" +
                '}';
    }
}
