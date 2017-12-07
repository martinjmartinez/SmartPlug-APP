package com.example.martinjmartinez.proyectofinal.Models;

public class GroupMonthConsumed {
    private String _id;

    private double limit;

    private double totalConsumed;

    private String objectId;

    private long date;

    private boolean halfReachedNotificationSend;

    private boolean almostReachNotificationSend;

    private boolean limitReachedNotificationSend;

    public GroupMonthConsumed() {}


    public GroupMonthConsumed(String _id, double limit, double totalConsumed, String objectId, long date, boolean halfReachedNotificationSend, boolean almostReachNotificationSend, boolean limitReachedNotificationSend) {
        this._id = _id;
        this.limit = limit;
        this.totalConsumed = totalConsumed;
        this.objectId = objectId;
        this.date = date;
        this.halfReachedNotificationSend = halfReachedNotificationSend;
        this.almostReachNotificationSend = almostReachNotificationSend;
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

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
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
}
