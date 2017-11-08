package com.example.martinjmartinez.proyectofinal.Entities;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Log extends RealmObject {

    @PrimaryKey
    private String _id;

    private Historial historial;

    private double power;

    public Log() {
    }

    public Historial getHistorial() {
        return historial;
    }

    public void setHistorial(Historial historial) {
        this.historial = historial;
    }

    public Log(double power) {
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
