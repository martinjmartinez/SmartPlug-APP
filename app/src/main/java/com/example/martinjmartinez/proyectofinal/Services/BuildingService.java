package com.example.martinjmartinez.proyectofinal.Services;

import com.example.martinjmartinez.proyectofinal.App.SmartPLugApplication;
import com.example.martinjmartinez.proyectofinal.Entities.Building;
import com.example.martinjmartinez.proyectofinal.Entities.Device;
import com.example.martinjmartinez.proyectofinal.Models.BuildingFB;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class BuildingService extends SmartPLugApplication {

    private Realm realm;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    public BuildingService(Realm realm) {
        this.realm = realm;
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Accounts/"+ currentUser.getUid() + "/Buildings");
    }

    public List<Building> allBuildings() {
        RealmResults<Building> results = realm.where(Building.class).equalTo("uid", currentUser.getUid()).findAll();

        return results;
    }

    public List<Building> allActiveBuildings() {
        RealmResults<Building> results = realm.where(Building.class).equalTo("uid", currentUser.getUid()).equalTo("isActive", true).findAll();

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
        building.setUid(buildingFB.getUid());

        realm.commitTransaction();
    }


    public Building getBuildingById(String _id) {
        Building building = realm.where(Building.class).equalTo("_id", _id).findFirst();

        return building;
    }

    public void updateBuildingCloud(BuildingFB building) {
        databaseReference.child(building.get_id()).child("name").setValue(building.getName());
        databaseReference.child(building.get_id()).child("active").setValue(building.isActive());
        databaseReference.child(building.get_id()).child("uid").setValue(building.getUid());

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

    public void updateBuildingPowerAverageConsumption(String _id) {
        Building building = getBuildingById(_id);
        double sum = 0;
        double average = 0;
        int counter = 0;

        if (!building.getDevices().isEmpty()) {
            for (Device device : building.getDevices()) {
                if(device.getAverageConsumption() >0){
                    sum = device.getAverageConsumption() + sum;
                    counter++;
                }
            }

            if (counter > 0) {
                average = sum / counter;
                databaseReference.child(_id).child("averageConsumption").setValue(average);
            }
        }else {
            databaseReference.child(_id).child("averageConsumption").setValue(average);
        }

        realm.beginTransaction();

        building.setAverageConsumption(average);

        realm.commitTransaction();
    }

    public void updateBuildingLocal(BuildingFB buildingFB) {
        Building building = getBuildingById(buildingFB.get_id());

        realm.beginTransaction();

        building.setName(buildingFB.getName());
        building.setActive(buildingFB.isActive());
        building.setUid(buildingFB.getUid());

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
