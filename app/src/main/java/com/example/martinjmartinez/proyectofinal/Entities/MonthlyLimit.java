package com.example.martinjmartinez.proyectofinal.Entities;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class MonthlyLimit extends RealmObject {

    @PrimaryKey
    private String _id;

    private String month;

    private Device device;

    private double limit;

    private Date date;

    private double totalConsumed;

    private double liveConsumed;

    private double accumulatedConsumed;

    private boolean autoTurnOff;

    public MonthlyLimit() {}

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
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

    public boolean isAutoTurnOff() {
        return autoTurnOff;
    }

    public void setAutoTurnOff(boolean autoTurnOff) {
        this.autoTurnOff = autoTurnOff;
    }
}
