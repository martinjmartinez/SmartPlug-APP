package com.example.martinjmartinez.proyectofinal.Entities;

import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.LinkingObjects;
import io.realm.annotations.PrimaryKey;

public class Device extends RealmObject {

    @PrimaryKey
    private String _id;

    private String name;

    private boolean status;

    private String ip_address;

    private Space space;

    private Building building;

    private double power;

    private String lastHistoryId;

    private double averageConsumption;

    @LinkingObjects("device")
    private final RealmResults<Historial> historials = null;

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

    public String getIp_address() {
        return ip_address;
    }

    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }

    public Space getSpace() {
        return space;
    }

    public void setSpace(Space space) {
        this.space = space;
    }

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    public double getPower() {
        return power;
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

    public double getAverageConsumption() {
        return averageConsumption;
    }

    public void setAverageConsumption(double averageConsumption) {
        this.averageConsumption = averageConsumption;
    }

    public RealmResults<Historial> getHistorials() {
        return historials;
    }

    public String statusToString() {
        return "{" +
                "\"status\":" + status +
                '}';
    }

    public String averageToString() {
        return "{" +
                "\"powerAverage\":" + averageConsumption +
                '}';
    }

    public String deviceToString() {
        String id = space == null ? "" : ",\"space\":\"" + space.get_id() + "\"";

        return "{" +
                "\"name\":\"" + name + "\"," +
                "\"ip_address\":\"" + ip_address + "\"," +
                "\"status\":" + status + "," +
                "\"building\":\"" + building.get_id() + "\"" +
                  id +
                '}';
    }
}
