package com.example.martinjmartinez.proyectofinal.Models;

public class LogFB {
    private String _id;

    private String historialId;

    private double power;

    public LogFB() {
    }

    public LogFB(String historialId, double power) {
        this.historialId = historialId;
        this.power = power;
    }

    public LogFB(String _id, String historialId, double power) {
        this._id = _id;
        this.historialId = historialId;
        this.power = power;
    }

    public LogFB(double power) {
        this.power = power;
    }

    public String getHistorialId() {
        return historialId;
    }

    public void setHistorialId(String historialId) {
        this.historialId = historialId;
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
