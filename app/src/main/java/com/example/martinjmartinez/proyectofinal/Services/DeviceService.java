package com.example.martinjmartinez.proyectofinal.Services;


import com.example.martinjmartinez.proyectofinal.Entities.Building;
import com.example.martinjmartinez.proyectofinal.Entities.Device;
import com.example.martinjmartinez.proyectofinal.Entities.Space;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class DeviceService {

    private Realm realm;

    public DeviceService (Realm realm) {
        this.realm = realm;
    }

    public List<Device> allDevices() {
        RealmResults<Device> results = realm.where(Device.class).findAll();

        return realm.copyFromRealm(results);
    }

    public void createDevice(String _id, String name, Boolean isOn, String ip_address, String spaceId, String buildingId) {
        Space space = realm.where(Space.class).equalTo("_id", spaceId).findFirst();
        Building building = realm.where(Building.class).equalTo("_id", buildingId).findFirst();

        realm.beginTransaction();

        Device device = realm.createObject(Device.class, _id);

        device.setName(name);
        device.setStatus(isOn);
        device.setBuilding(building);
        device.setSpace(space);
        device.setIp_address(ip_address);

        realm.commitTransaction();
    }

    public Device getDeviceById(String _id) {
        Device device = realm.where(Device.class).equalTo("_id", _id).findFirst();

        return device;
    }

    public void updateOrCreateDevice(String _id, String name, Boolean isOn, String ip_address, String spaceId, String buildingId) {
        if(getDeviceById(_id) != null) {
            updateDevice(_id, name, isOn, ip_address, spaceId, buildingId);
        } else {
            createDevice(_id, name, isOn, ip_address, spaceId, buildingId);
        }
    }

    public void updateDevice(String _id, String name, Boolean isOn, String ip_address, String spaceId, String buildingId) {
        Device device = getDeviceById(_id);
        Space space = realm.where(Space.class).equalTo("_id", spaceId).findFirst();
        Building building = realm.where(Building.class).equalTo("_id", buildingId).findFirst();

        realm.beginTransaction();

        device.setName(name);
        device.setStatus(isOn);
        device.setBuilding(building);
        device.setSpace(space);
        device.setIp_address(ip_address);

        realm.commitTransaction();
    }

    public void updateDeviceLastHistoryId(String _id, String lastHistoryId) {
        Device device = getDeviceById(_id);

        realm.beginTransaction();

        device.setLastHistoryId(lastHistoryId);

        realm.commitTransaction();
    }

    public void updateDeviceSpaces(String _id, Space space) {
        Device device = getDeviceById(_id);

        realm.beginTransaction();

        device.setSpace(space);

        realm.commitTransaction();
    }

    public void updateDeviceBuilding(String _id, Building building) {
        Device device = getDeviceById(_id);

        realm.beginTransaction();

        device.setBuilding(building);

        realm.commitTransaction();
    }

    public void deleteDevice(String _id) {
        Device device = getDeviceById(_id);

        realm.beginTransaction();

        device.deleteFromRealm();

        realm.commitTransaction();
    }
}
