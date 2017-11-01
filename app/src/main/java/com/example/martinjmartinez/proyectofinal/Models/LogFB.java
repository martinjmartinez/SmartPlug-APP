package com.example.martinjmartinez.proyectofinal.Models;

/**
 * Created by MartinJMartinez on 11/1/2017.
 */

public class LogFB {
    private String _id;

    private double power;

    public LogFB() {
    }

    public LogFB(double power) {
        this.power = power;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public double getPower() {
        return power;
    }

    public void setPower(double power) {
        this.power = power;
    }
}
