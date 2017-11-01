package com.example.martinjmartinez.proyectofinal.Services;

import com.example.martinjmartinez.proyectofinal.Entities.Building;
import com.example.martinjmartinez.proyectofinal.Entities.Device;
import com.example.martinjmartinez.proyectofinal.Entities.Space;
import com.example.martinjmartinez.proyectofinal.Models.SpaceFB;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import io.realm.Realm;
import io.realm.RealmResults;


public class SpaceService {

    private Realm realm;
    private DatabaseReference databaseReference;

    public SpaceService (Realm realm) {
        this.realm = realm;
        databaseReference = FirebaseDatabase.getInstance().getReference("Spaces");
    }

    public List<Space> allSpaces() {
        RealmResults<Space> results = realm.where(Space.class).findAll();

        return results;
    }

    public List<Space> allActiveSpacesByBuilding(String buildingId) {
        RealmResults<Space> results = realm.where(Space.class).equalTo("building._id", buildingId).equalTo("isActive", true).findAll();

        return results;
    }

    public void createSpace(String name, String buildingId, boolean isActive) {
        Building building = realm.where(Building.class).equalTo("_id", buildingId).findFirst();

        String spaceId = databaseReference.push().getKey();
        SpaceFB spaceFB = new SpaceFB(spaceId, name, buildingId, isActive);
        databaseReference.child(spaceId).setValue(spaceFB);

        realm.beginTransaction();

        Space space = realm.createObject(Space.class, spaceId);

        space.setName(name);
        space.setBuilding(building);
        space.setActive(isActive);

        realm.commitTransaction();
    }

    public void updateOrCreateSpace(String _id, String name, String buildingId, boolean isActive) {
        if(getSpaceById(_id) != null) {
            //updateSpace(_id, name, buildingId, isActive);
        } else {
            createSpace(name, buildingId,isActive);
        }
    }

    public Space getSpaceById(String _id) {
        Space space = realm.where(Space.class).equalTo("_id", _id).findFirst();

        return space;
    }

    public void updateSpaceName(String _id, String name) {
        Space space = getSpaceById(_id);
        databaseReference.child(_id).child("name").setValue(name);

        realm.beginTransaction();

        space.setName(name);

        realm.commitTransaction();
    }

    public void updateSapacePowerAverageConsumption(String _id) {
        Space space = getSpaceById(_id);
        double sum = 0;
        double average= 0;
        if (!space.getDevices().isEmpty()) {
            for (Device device : space.getDevices()) {
                sum = device.getAverageConsumption() + sum;
            }
            average = sum/space.getDevices().size();
        }

        databaseReference.child(_id).child("averageConsumption").setValue(average);
        realm.beginTransaction();

        space.setAverageConsumption(average);

        realm.commitTransaction();
    }

    public void updateSpace(SpaceFB spaceFB) {
        String _id = spaceFB.get_id();
        String buildingId = spaceFB.getBuildingId();
        String name = spaceFB.getName();
        double averagePower = spaceFB.getAverageConsumption();
        boolean isActive = spaceFB.isActive();

        Space space = getSpaceById(_id);
        Building building = realm.where(Building.class).equalTo("_id", buildingId).findFirst();

        realm.beginTransaction();

        space.setName(name);
        space.setBuilding(building);
        space.setActive(isActive);
        space.setAverageConsumption(averagePower);

        realm.commitTransaction();
    }


    public void deleteSpace(String _id) {
        Space space = getSpaceById(_id);
        databaseReference.child(_id).child("active").setValue(false);

        realm.beginTransaction();

        space.setActive(false);

        realm.commitTransaction();
    }
}
