package com.example.martinjmartinez.proyectofinal.Services;

import com.example.martinjmartinez.proyectofinal.Entities.Building;
import com.example.martinjmartinez.proyectofinal.Entities.Device;
import com.example.martinjmartinez.proyectofinal.Entities.Space;
import com.example.martinjmartinez.proyectofinal.Models.SpaceFB;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;


public class SpaceService {

    private Realm realm;
    public DatabaseReference databaseReference;
    private BuildingService buildingService;
    private FirebaseAuth mAuth;

    public SpaceService(Realm realm) {
        this.realm = realm;
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Accounts/"+ currentUser.getUid() + "/Spaces");
        buildingService = new BuildingService(realm);
    }

    public List<Space> allSpaces() {
        RealmResults<Space> results = realm.where(Space.class).findAll();

        return results;
    }

    public List<Space> allActiveSpacesByBuilding(String buildingId) {
        RealmResults<Space> results = realm.where(Space.class).equalTo("building._id", buildingId).equalTo("isActive", true).findAll();

        return results;
    }

    public String createSpaceCloud(SpaceFB spaceFB) {
        String spaceId = databaseReference.push().getKey();

        spaceFB.set_id(spaceId);
        databaseReference.child(spaceId).setValue(spaceFB);

        createSpaceLocal(spaceFB);
        return spaceId;
    }

    public void createSpaceLocal(SpaceFB spaceFB) {
        Building building = buildingService.getBuildingById(spaceFB.getBuildingId());

        realm.beginTransaction();

        Space space = realm.createObject(Space.class, spaceFB.get_id());

        space.setName(spaceFB.getName());
        space.setBuilding(building);
        space.setMonthlyLimit(spaceFB.getMonthlyLimit());
        space.setActive(spaceFB.isActive());

        realm.commitTransaction();
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


    public void updateSpaceLimit(String _id, double limit) {
        Space space = getSpaceById(_id);
        databaseReference.child(_id).child("monthlyLimit").setValue(limit);

        realm.beginTransaction();

        space.setMonthlyLimit(limit);

        realm.commitTransaction();
    }

    public void updateSpacePowerAverageConsumption(String _id) {
        Space space = getSpaceById(_id);
        double sum = 0;
        double average = 0;
        int counter = 0;

        if (!space.getDevices().isEmpty()) {
            for (Device device : space.getDevices()) {
                if(device.getAverageConsumption() >0){
                    sum = device.getAverageConsumption() + sum;
                    counter++;
                }
            }

            if (counter > 0) {
                average = sum / counter;
                databaseReference.child(_id).child("averageConsumption").setValue(average);
            }
        } else {
            databaseReference.child(_id).child("averageConsumption").setValue(average);
        }

        realm.beginTransaction();

        space.setAverageConsumption(average);

        realm.commitTransaction();

        buildingService.updateBuildingPowerAverageConsumption(space.getBuilding().get_id());
    }

    public void updateOrCreate(SpaceFB spaceFB){
        Space space = getSpaceById(spaceFB.get_id());
        if(space != null){
            updateSpaceLocal(spaceFB);
        } else {
            createSpaceLocal(spaceFB);
        }
    }

    public void updateSpaceCloud(Space space) {
        databaseReference.child(space.get_id()).child("name").setValue(space.getName());
        databaseReference.child(space.get_id()).child("buildingId").setValue(space.getBuilding().get_id());
        databaseReference.child(space.get_id()).child("averageConsumption").setValue(space.getAverageConsumption());
        databaseReference.child(space.get_id()).child("monthlyLimit").setValue(space.getMonthlyLimit());
    }

    public void updateSpaceLocal(SpaceFB spaceFB) {
        Space space = getSpaceById(spaceFB.get_id());
        Building building = buildingService.getBuildingById(spaceFB.getBuildingId());

        realm.beginTransaction();

        space.setName(spaceFB.getName());
        space.setBuilding(building);
        space.setActive(spaceFB.isActive());
        space.setMonthlyLimit(spaceFB.getMonthlyLimit());
        space.setAverageConsumption(spaceFB.getAverageConsumption());

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
