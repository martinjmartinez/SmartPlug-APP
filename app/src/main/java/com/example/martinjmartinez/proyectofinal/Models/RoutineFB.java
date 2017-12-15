package com.example.martinjmartinez.proyectofinal.Models;


public class RoutineFB {
    private String _id;
    private String deviceId;
    private String startTime;
    private String endTime;
    private String name;
    private boolean action;
    private boolean enabled;
    private boolean startTriggered;
    private boolean endTriggered;
    private String buildingId;

    public RoutineFB() {}

    public RoutineFB(String deviceId, String startTime, String endTime, String name, boolean action, boolean enabled, boolean startTriggered, String buildingId, boolean endTriggered) {
        this.deviceId = deviceId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.name = name;
        this.action = action;
        this.enabled = enabled;
        this.startTriggered = startTriggered;
        this.endTriggered = endTriggered;
        this.buildingId = buildingId;
    }

    public String getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(String buildingId) {
        this.buildingId = buildingId;
    }

    public boolean isStartTriggered() {
        return startTriggered;
    }

    public void setStartTriggered(boolean startTriggered) {
        this.startTriggered = startTriggered;
    }

    public boolean isEndTriggered() {
        return endTriggered;
    }

    public void setEndTriggered(boolean endTriggered) {
        this.endTriggered = endTriggered;
    }

    public String get_id() {
        return _id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
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

    public boolean isAction() {
        return action;
    }

    public void setAction(boolean action) {
        this.action = action;
    }

}
