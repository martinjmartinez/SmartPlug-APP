package com.example.martinjmartinez.proyectofinal.Services;


import android.util.Log;

import com.example.martinjmartinez.proyectofinal.Entities.Building;
import com.example.martinjmartinez.proyectofinal.Entities.Device;
import com.example.martinjmartinez.proyectofinal.Entities.Historial;
import com.example.martinjmartinez.proyectofinal.Entities.Space;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class DeviceService {

    private Realm realm;
    private SpaceService spaceService;
    private BuildingService buildingService;

    public DeviceService (Realm realm) {
        this.realm = realm;
        spaceService = new SpaceService(realm);
        buildingService = new BuildingService(realm);
    }

    public List<Device> allDevices() {
        RealmResults<Device> results = realm.where(Device.class).findAll();

        return results;
    }

    public List<Device> allActiveDevicesByBuilding(String buildingId) {
        RealmResults<Device> results = realm.where(Device.class).equalTo("building._id", buildingId).equalTo("isActive", true).findAll();

        return results;
    }

    public List<Device> allActiveDevicesBySpace(String spaceId) {
        RealmResults<Device> results = realm.where(Device.class).equalTo("space._id", spaceId).equalTo("isActive", true).findAll();

        return results;
    }

    public void createDevice(String _id, String name, Boolean isOn, String ip_address, String spaceId, String buildingId, double powerAverage, boolean isActive) {
        Space space = spaceService.getSpaceById(spaceId);
        Building building = buildingService.getBuildingById(buildingId);

        realm.beginTransaction();

        Device device = realm.createObject(Device.class, _id);

        device.setName(name);
        device.setStatus(isOn);
        device.setBuilding(building);
        device.setSpace(space);
        device.setIp_address(ip_address);
        device.setAverageConsumption(powerAverage);
        device.setActive(isActive);

        realm.commitTransaction();
    }

    public Device getDeviceById(String _id) {
        Device device = realm.where(Device.class).equalTo("_id", _id).findFirst();

        return device;
    }

    public void updateOrCreateDevice(String _id, String name, Boolean isOn, String ip_address, String spaceId, String buildingId, double powerAverage, boolean isActive) {
        if(getDeviceById(_id) != null) {
            updateDevice(_id, name, isOn, ip_address, spaceId, buildingId, powerAverage, isActive);
        } else {
            createDevice(_id, name, isOn, ip_address, spaceId, buildingId, powerAverage, isActive);
        }
    }

    public void updateDevice(String _id, String name, Boolean isOn, String ip_address, String spaceId, String buildingId, double powerAverage, boolean isActive) {
        Device device = getDeviceById(_id);
        Space space = spaceService.getSpaceById(spaceId);
        Building building = buildingService.getBuildingById(buildingId);

        realm.beginTransaction();

        device.setName(name);
        device.setStatus(isOn);
        device.setBuilding(building);
        device.setSpace(space);
        device.setIp_address(ip_address);
        device.setAverageConsumption(powerAverage);
        device.setActive(isActive);

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

    public void updateDeviceStatus(String _id, boolean isOn) {
        Device device = getDeviceById(_id);

        realm.beginTransaction();

        device.setStatus(isOn);

        realm.commitTransaction();
    }

    public void updateDevicePower(String _id, double power) {
        Device device = getDeviceById(_id);

        realm.beginTransaction();

        device.setPower(power);

        realm.commitTransaction();
    }

    public void updateDevicePowerAverageConsumption(String _id) {
        Device device = getDeviceById(_id);
        if (device != null) {
            double sum = 0;
            double average = 0;

            if (device.getHistorials() != null) {
                for (Historial historial : device.getHistorials()) {
                    sum = sum + historial.getPowerAverage();
                }
                average = sum/device.getHistorials().size();
            }

            realm.beginTransaction();

            device.setAverageConsumption(average);

            realm.commitTransaction();
            if(device.getSpace() !=null){
                spaceService.updateSapacePowerAverageConsumption(device.getSpace().get_id());
            }

        }
    }

    public void deleteDevice(String _id) {
        Device device = getDeviceById(_id);

        realm.beginTransaction();

        device.setActive(false);

        realm.commitTransaction();
    }
}
