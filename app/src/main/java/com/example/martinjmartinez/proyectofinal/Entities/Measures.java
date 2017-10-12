package com.example.martinjmartinez.proyectofinal.Entities;


import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Measures extends RealmObject {

    @PrimaryKey
    private String _id;

    private Device mDevice;

    private Date mDate;

    private float mPower;

    public Measures() {}

    public Measures(Device mDevice, Date mDate, float mPower) {
        this.mDevice = mDevice;
        this.mDate = mDate;
        this.mPower = mPower;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Device getmDevice() {
        return mDevice;
    }

    public void setmDevice(Device mDevice) {
        this.mDevice = mDevice;
    }

    public Date getmDate() {
        return mDate;
    }

    public void setmDate(Date mDate) {
        this.mDate = mDate;
    }

    public float getmPower() {
        return mPower;
    }

    public void setmPower(float mPower) {
        this.mPower = mPower;
    }
}
