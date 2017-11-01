package com.example.martinjmartinez.proyectofinal.Services;

import com.example.martinjmartinez.proyectofinal.App.SmartPLugApplication;
import com.example.martinjmartinez.proyectofinal.Entities.Building;
import com.example.martinjmartinez.proyectofinal.Models.BuildingFB;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import io.realm.Realm;
import io.realm.RealmResults;

public class BuildingService extends SmartPLugApplication {

    private Realm realm;
    private DatabaseReference databaseReference;

    public BuildingService (Realm realm) {
        this.realm = realm;
        databaseReference = FirebaseDatabase.getInstance().getReference("Buildings");
    }

    public List<Building> allBuildings() {
        RealmResults<Building> results = realm.where(Building.class).findAll();

        return results;
    }

    public List<Building> allActiveBuildings() {
        RealmResults<Building> results = realm.where(Building.class).equalTo("isActive", true).findAll();

        return results;
    }

    public void createBuilding(String name, boolean isActive) {
        String buildingId = databaseReference.push().getKey();

        BuildingFB buildingFB = new BuildingFB(buildingId, name, isActive);
        databaseReference.child(buildingId).setValue(buildingFB);

        realm.beginTransaction();

        Building building = realm.createObject(Building.class, buildingId);
        building.setName(name);
        building.setActive(isActive);

        realm.commitTransaction();
    }

    public void updateOrCreateBuilding(String _id, String name, boolean isActive) {
        if(getBuildingById(_id) != null) {
           // updateBuildingName(_id, name, isActive);
        } else {
            createBuilding(name, isActive);
        }
    }

    public Building getBuildingById(String _id) {
        Building building = realm.where(Building.class).equalTo("_id", _id).findFirst();

        return building;
    }

    public void updateBuildingName(BuildingFB buildingFB) {
        String _id = buildingFB.get_id();
        String name = buildingFB.getName();
        boolean isActive =buildingFB.isActive();

        databaseReference.child(_id).child("name").setValue(name);
        databaseReference.child(_id).child("active").setValue(isActive);

        Building building = getBuildingById(_id);

        realm.beginTransaction();

        building.setName(name);
        building.setActive(isActive);

        realm.commitTransaction();
    }

    public void deleteBuilding(String _id) {
        databaseReference.child(_id).child("active").setValue(false);

        Building building = getBuildingById(_id);

        realm.beginTransaction();

        building.setActive(false);

        realm.commitTransaction();
    }

}
