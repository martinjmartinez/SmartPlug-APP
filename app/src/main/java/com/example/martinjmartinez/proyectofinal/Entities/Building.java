package com.example.martinjmartinez.proyectofinal.Entities;

import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.LinkingObjects;
import io.realm.annotations.PrimaryKey;

public class Building  extends RealmObject{

    @PrimaryKey
    private String _id;

    private String name;

    @LinkingObjects("building")
    private final RealmResults<Space> spaces = null;

    @LinkingObjects("building")
    private final RealmResults<Device> devices = null;


    public RealmResults<Device> getDevices() {
        return devices;
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

    public RealmResults<Space> getSpaces() {
        return spaces;
    }

    @Override
    public String toString() {
        return "{\"name\":\"" + getName()+"\"}";
    }
}
