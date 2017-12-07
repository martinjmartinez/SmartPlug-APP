package com.example.martinjmartinez.proyectofinal.Entities;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Settings extends RealmObject {
    @PrimaryKey
    private String _id;

    private double cat1Price;
    private double cat2Price;
    private double cat3Price;
    private double cat4Price;
    private double fixed1Price;
    private double fixed2Price;
    private boolean english;

    public Settings() {}

    public double getFixed1Price() {
        return fixed1Price;
    }

    public void setFixed1Price(double fixed1Price) {
        this.fixed1Price = fixed1Price;
    }

    public double getFixed2Price() {
        return fixed2Price;
    }

    public void setFixed2Price(double fixed2Price) {
        this.fixed2Price = fixed2Price;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public double getCat1Price() {
        return cat1Price;
    }

    public void setCat1Price(double cat1Price) {
        this.cat1Price = cat1Price;
    }

    public double getCat2Price() {
        return cat2Price;
    }

    public void setCat2Price(double cat2Price) {
        this.cat2Price = cat2Price;
    }

    public double getCat3Price() {
        return cat3Price;
    }

    public void setCat3Price(double cat3Price) {
        this.cat3Price = cat3Price;
    }

    public double getCat4Price() {
        return cat4Price;
    }

    public void setCat4Price(double cat4Price) {
        this.cat4Price = cat4Price;
    }

    public boolean isEnglish() {
        return english;
    }

    public void setEnglish(boolean english) {
        this.english = english;
    }
}
