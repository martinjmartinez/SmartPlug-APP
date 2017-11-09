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

    public BuildingService(Realm realm) {
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

    public void createBuildingCloud(BuildingFB buildingFB) {
        String buildingId = databaseReference.push().getKey();

        buildingFB.set_id(buildingId);
        databaseReference.child(buildingId).setValue(buildingFB);

        createBuildingLocal(buildingFB);
    }

    public void createBuildingLocal(BuildingFB buildingFB) {
        realm.beginTransaction();

        Building building = realm.createObject(Building.class, buildingFB.get_id());
        building.setName(buildingFB.getName());
        building.setActive(buildingFB.isActive());

        realm.commitTransaction();
    }


    public Building getBuildingById(String _id) {
        Building building = realm.where(Building.class).equalTo("_id", _id).findFirst();

        return building;
    }

    public void updateBuildingCloud(BuildingFB building) {
        databaseReference.child(building.get_id()).child("name").setValue(building.getName());
        databaseReference.child(building.get_id()).child("active").setValue(building.isActive());

        updateBuildingLocal(building);
    }

    public void updateOrCreate(BuildingFB buildingFB){
        Building building = getBuildingById(buildingFB.get_id());
        if(building != null){
            updateBuildingLocal(buildingFB);
        } else {
            createBuildingLocal(buildingFB);
        }
    }

    public void updateBuildingLocal(BuildingFB buildingFB) {
        Building building = getBuildingById(buildingFB.get_id());

        realm.beginTransaction();

        building.setName(buildingFB.getName());
        building.setActive(buildingFB.isActive());

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
