package com.example.martinjmartinez.proyectofinal.Services;

import com.example.martinjmartinez.proyectofinal.App.SmartPLugApplication;
import com.example.martinjmartinez.proyectofinal.Entities.Building;
import com.example.martinjmartinez.proyectofinal.Entities.Space;

import java.util.List;
import io.realm.Realm;
import io.realm.RealmResults;

public class BuildingService extends SmartPLugApplication {

    private Realm realm;

    public BuildingService (Realm realm) {
        this.realm = realm;
    }

    public List<Building> allBuildings() {
        RealmResults<Building> results = realm.where(Building.class).findAll();

        return realm.copyFromRealm(results);
    }

    public void createBuilding(String _id, String name) {
        realm.beginTransaction();

        Building building = realm.createObject(Building.class, _id);
        building.setName(name);

        realm.commitTransaction();
    }

    public void updateOrCreateBuilding(String _id, String name) {
        if(getBuildingById(_id) != null) {
            updateBuildingName(_id, name);
        } else {
            createBuilding(_id, name);
        }
    }

    public Building getBuildingById(String _id) {
        Building building = realm.where(Building.class).equalTo("_id", _id).findFirst();

        return building;
    }

    public void updateBuildingName(String _id, String name) {
        Building building = getBuildingById(_id);

        realm.beginTransaction();

        building.setName(name);

        realm.commitTransaction();
    }

    public void updateBuildingSpaces(String _id, Space space) {
        Building building = getBuildingById(_id);

        realm.beginTransaction();

        building.getSpaces().add(space);

        realm.commitTransaction();
    }


    public void deleteBuilding(String _id) {
        Building building = getBuildingById(_id);

        realm.beginTransaction();

        building.deleteFromRealm();

        realm.commitTransaction();
    }

}
