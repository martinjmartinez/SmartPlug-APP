package com.example.martinjmartinez.proyectofinal.Entities;

/**
 * Created by MartinJMartinez on 6/20/2017.
 */

public class Device {

    private String _id;

    private String name;

    private boolean status;

    private String ip_address;

    private Space space;

    private Building building;

    private double power;

    private String lastHistoryId;

    public String getLastHistoryId() {
        return lastHistoryId;
    }

    public void setLastHistoryId(String lastHistoryId) {
        this.lastHistoryId = lastHistoryId;
    }

    public double getPower() {
        return power;
    }

    public void setPower(double power) {
        this.power = power;
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

    public String statusToString() {
        return "{" +
                "\"status\":" + status +
                '}';
    }

    public String deviceToString() {
        return "{" +
                "\"name\":\"" + name + "\"," +
                "\"ip_address\":\"" + ip_address + "\"," +
                "\"status\":" + status +
                '}';
    }
}
