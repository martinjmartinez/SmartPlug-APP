package com.example.martinjmartinez.proyectofinal.Services;

import com.example.martinjmartinez.proyectofinal.Entities.Building;
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

    public void createSpace(String _id, String name, String buildingId) {
        Building building = realm.where(Building.class).equalTo("_id", buildingId).findFirst();

        realm.beginTransaction();

        Space space = realm.createObject(Space.class, _id);

        space.setName(name);
        space.setBuilding(building);

        realm.commitTransaction();
    }

    public void updateOrCreateSpace(String _id, String name, String buildingId) {
        if(getSpaceById(_id) != null) {
            updateSpace(_id, name, buildingId);
        } else {
            createSpace(_id, name, buildingId);
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

    public void updateSpacePower(String _id, double power) {
        Space space = getSpaceById(_id);

        realm.beginTransaction();

        space.setPower(power);

        realm.commitTransaction();
    }

    public void updateSpace(String _id, String name, String buildingId) {
        Space space = getSpaceById(_id);
        Building building = realm.where(Building.class).equalTo("_id", buildingId).findFirst();

        realm.beginTransaction();

        space.setBuilding(building);
        space.setBuilding(building);

        realm.commitTransaction();
    }


    public void deleteSpace(String _id) {
        Space space = getSpaceById(_id);

        realm.beginTransaction();

        space.deleteFromRealm();

        realm.commitTransaction();
    }
}
