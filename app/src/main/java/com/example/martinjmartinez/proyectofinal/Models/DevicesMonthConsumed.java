package com.example.martinjmartinez.proyectofinal.Models;

public class DevicesMonthConsumed {

    private String _id;

    private double limit;

    private double totalConsumed;

    private String deviceId;

    private long date;

    private double liveConsumed;

    private double accumulatedConsumed;

    private boolean autoTurnOff;

    private boolean halfReachedNotificationSend;

    private boolean almostReachNotificationSend;

    private boolean limitReachedNotificationSend;

    public DevicesMonthConsumed() {}

    public DevicesMonthConsumed(String _id, boolean halfReachedNotificationSend, boolean almostReachNotificationSend, boolean limitReachedNotificationSend, double limit, boolean autoTurnOff, String deviceId, long date) {
        this._id = _id;
        this.limit = limit;
        this.halfReachedNotificationSend = halfReachedNotificationSend;
        this.almostReachNotificationSend = almostReachNotificationSend;
        this.limitReachedNotificationSend = limitReachedNotificationSend;
        this.autoTurnOff = autoTurnOff;
        this.deviceId = deviceId;
        this.date = date;
    }

    public DevicesMonthConsumed(String _id, double limit, double totalConsumed, String spaceId, long date, boolean halfReachedNotificationSend, boolean almostReachNotificationSend, boolean limitReachedNotificationSend) {
        this._id = _id;
        this.limit = limit;
        this.totalConsumed = totalConsumed;
        this.deviceId = spaceId;
        this.date = date;
        this.halfReachedNotificationSend = halfReachedNotificationSend;
        this.almostReachNotificationSend = almostReachNotificationSend;
        this.limitReachedNotificationSend = limitReachedNotificationSend;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public boolean isAutoTurnOff() {
        return autoTurnOff;
    }

    public void setAutoTurnOff(boolean autoTurnOff) {
        this.autoTurnOff = autoTurnOff;
    }

    public boolean isHalfReachedNotificationSend() {
        return halfReachedNotificationSend;
    }

    public void setHalfReachedNotificationSend(boolean halfReachedNotificationSend) {
        this.halfReachedNotificationSend = halfReachedNotificationSend;
    }

    public boolean isAlmostReachNotificationSend() {
        return almostReachNotificationSend;
    }

    public void setAlmostReachNotificationSend(boolean almostReachNotificationSend) {
        this.almostReachNotificationSend = almostReachNotificationSend;
    }

    public boolean isLimitReachedNotificationSend() {
        return limitReachedNotificationSend;
    }

    public void setLimitReachedNotificationSend(boolean limitReachedNotificationSend) {
        this.limitReachedNotificationSend = limitReachedNotificationSend;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public double getLimit() {
        return limit;
    }

    public void setLimit(double limit) {
        this.limit = limit;
    }

    public double getTotalConsumed() {
        return totalConsumed;
    }

    public void setTotalConsumed(double totalConsumed) {
        this.totalConsumed = totalConsumed;
    }

    public double getLiveConsumed() {
        return liveConsumed;
    }

    public void setLiveConsumed(double liveConsumed) {
        this.liveConsumed = liveConsumed;
    }

    public double getAccumulatedConsumed() {
        return accumulatedConsumed;
    }

    public void setAccumulatedConsumed(double accumulatedConsumed) {
        this.accumulatedConsumed = accumulatedConsumed;
    }
}
