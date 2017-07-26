package com.example.martinjmartinez.proyectofinal.Entities;


import java.util.Date;

/**
 * Created by MartinJMartinez on 6/27/2017.
 */

public class Measures {

    private Device mDevice;

    private Date mDate;

    private float mPower;

    public Measures() {}

    public Measures(Device mDevice, Date mDate, float mPower) {
        this.mDevice = mDevice;
        this.mDate = mDate;
        this.mPower = mPower;
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
