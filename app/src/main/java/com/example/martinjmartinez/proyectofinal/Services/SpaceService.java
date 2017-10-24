package com.example.martinjmartinez.proyectofinal.Services;

import android.util.Log;

import com.example.martinjmartinez.proyectofinal.Entities.Building;
import com.example.martinjmartinez.proyectofinal.Entities.Device;
import com.example.martinjmartinez.proyectofinal.Entities.Historial;
import com.example.martinjmartinez.proyectofinal.Entities.Space;

import java.util.List;
import io.realm.Realm;
import io.realm.RealmResults;


public class SpaceService {

    private Realm realm;

    public SpaceService (Realm realm) {
        this.realm = realm;
    }

    public List<Space> allSpaces() {
        RealmResults<Space> results = realm.where(Space.class).findAll();

        return results;
    }

    public List<Space> allActiveSpacesByBuilding(String buildingId) {
        RealmResults<Space> results = realm.where(Space.class).equalTo("building._id", buildingId).equalTo("isActive", true).findAll();

        return results;
    }

    public void createSpace(String _id, String name, String buildingId, boolean isActive) {
        Building building = realm.where(Building.class).equalTo("_id", buildingId).findFirst();

        realm.beginTransaction();

        Space space = realm.createObject(Space.class, _id);

        space.setName(name);
        space.setBuilding(building);
        space.setActive(isActive);

        realm.commitTransaction();
    }

    public void updateOrCreateSpace(String _id, String name, String buildingId, boolean isActive) {
        if(getSpaceById(_id) != null) {
            updateSpace(_id, name, buildingId, isActive);
        } else {
            createSpace(_id, name, buildingId,isActive);
        }
    }

    public Space getSpaceById(String _id) {
        Space space = realm.where(Space.class).equalTo("_id", _id).findFirst();

        return space;
    }

    public void updateSpaceName(String _id, String name) {
        Space space = getSpaceById(_id);

        realm.beginTransaction();

        space.setName(name);

        realm.commitTransaction();
    }

    public void updateSapacePowerAverageConsumption(String _id) {
        Space space = getSpaceById(_id);
        double sum = 0;

        if (space.getDevices() != null) {
            for (Device device : space.getDevices()) {
                sum = device.getAverageConsumption() + sum;
            }
        }

        double average = sum/space.getDevices().size();

        realm.beginTransaction();

        space.setAverageConsumption(average);

        realm.commitTransaction();
    }

    public void updateSpacePower(String _id, double power) {
        Space space = getSpaceById(_id);

        realm.beginTransaction();

        space.setPower(power);

        realm.commitTransaction();
    }

    public void updateSpace(String _id, String name, String buildingId, boolean isActive) {
        Space space = getSpaceById(_id);
        Building building = realm.where(Building.class).equalTo("_id", buildingId).findFirst();

        realm.beginTransaction();

        space.setName(name);
        space.setBuilding(building);
        space.setActive(isActive);

        realm.commitTransaction();
    }


    public void deleteSpace(String _id) {
        Space space = getSpaceById(_id);

        realm.beginTransaction();

        space.setActive(false);

        realm.commitTransaction();
    }
}
