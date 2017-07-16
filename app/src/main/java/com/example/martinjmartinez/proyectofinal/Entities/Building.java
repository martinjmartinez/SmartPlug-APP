package com.example.martinjmartinez.proyectofinal.Entities;

import java.util.List;

/**
 * Created by MartinJMartinez on 7/9/2017.
 */

public class Building {

    private String _id;

    private String name;

    private List<Space> spaces;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Space> getSpaces() {
        return spaces;
    }

    public void setSpaces(List<Space> spaces) {
        this.spaces = spaces;
    }

}
