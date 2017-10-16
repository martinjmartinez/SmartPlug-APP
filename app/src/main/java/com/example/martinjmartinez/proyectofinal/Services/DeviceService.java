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
        spaceService = new SpaceService(Realm.getDefaultInstance());
        buildingService = new BuildingService(Realm.getDefaultInstance());
    }

    public List<Device> allDevices() {
        RealmResults<Device> results = realm.where(Device.class).findAll();

        return results;
    }

    public void createDevice(String _id, String name, Boolean isOn, String ip_address, String spaceId, String buildingId, double powerAverage) {
        Space space = spaceService.getSpaceById(spaceId);
        Building building = buildingService.getBuildingById(buildingId);
        Log.e("BUILDING6", building.getName());
        realm.beginTransaction();

        Device device = realm.createObject(Device.class, _id);

        device.setName(name);
        device.setStatus(isOn);
        device.setBuilding(building);
        device.setSpace(space);
        device.setIp_address(ip_address);
        device.setAverageConsumption(powerAverage);

        realm.commitTransaction();
    }

    public Device getDeviceById(String _id) {
        Device device = realm.where(Device.class).equalTo("_id", _id).findFirst();

        return device;
    }

    public void updateOrCreateDevice(String _id, String name, Boolean isOn, String ip_address, String spaceId, String buildingId, double powerAverage) {
        if(getDeviceById(_id) != null) {
            updateDevice(_id, name, isOn, ip_address, spaceId, buildingId, powerAverage);
        } else {
            createDevice(_id, name, isOn, ip_address, spaceId, buildingId, powerAverage);
        }
    }

    public void updateDevice(String _id, String name, Boolean isOn, String ip_address, String spaceId, String buildingId, double powerAverage) {
        Device device = getDeviceById(_id);
        Space space = realm.where(Space.class).equalTo("_id", spaceId).findFirst();
        Building building = realm.where(Building.class).equalTo("_id", buildingId).findFirst();

        realm.beginTransaction();

        device.setName(name);
        device.setStatus(isOn);
        device.setBuilding(building);
        device.setSpace(space);
        device.setIp_address(ip_address);
        device.setAverageConsumption(powerAverage);

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
                Log.e("Num of Histories", device.getHistorials().size() + "");
                for (Historial historial : device.getHistorials()) {
                    Log.e("History ave", historial.getPowerAverage() + "");
                    sum = sum + historial.getPowerAverage();
                }

                Log.e("History sum", sum + "");
                average = sum/device.getHistorials().size();
                Log.e("average device", average + "");
            }

            realm.beginTransaction();

            device.setAverageConsumption(average);

            realm.commitTransaction();

            spaceService.updateSapacePowerAverageConsumption(device.getSpace().get_id());
        }
    }

    public void deleteDevice(String _id) {
        Device device = getDeviceById(_id);

        realm.beginTransaction();

        device.deleteFromRealm();

        realm.commitTransaction();
    }
}
