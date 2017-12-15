package com.example.martinjmartinez.proyectofinal.Entities;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Routine extends RealmObject {

    @PrimaryKey
    private String _id;
    private Device device;
    private Building building;
    private RealmList<Integer> dayOfWeek;
    private String startTime;
    private String endTime;
    private String name;
    private boolean enable;
    private boolean action;
    private boolean startTriggered;
    private boolean endTriggered;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public boolean isAction() {
        return action;
    }

    public void setAction(boolean action) {
        this.action = action;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public RealmList<Integer> getDayOfWeek() {
        return dayOfWeek;
    }

    public boolean isEndTriggered() {
        return endTriggered;
    }

    public void setEndTriggered(boolean endTriggered) {
        this.endTriggered = endTriggered;
    }

    public void setDayOfWeek(RealmList<Integer> dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    public boolean isStartTriggered() {
        return startTriggered;
    }

    public void setStartTriggered(boolean startTriggered) {
        this.startTriggered = startTriggered;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
