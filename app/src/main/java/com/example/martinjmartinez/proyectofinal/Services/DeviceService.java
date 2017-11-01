package com.example.martinjmartinez.proyectofinal.Services;


import android.util.Log;

import com.example.martinjmartinez.proyectofinal.Entities.Building;
import com.example.martinjmartinez.proyectofinal.Entities.Device;
import com.example.martinjmartinez.proyectofinal.Entities.Historial;
import com.example.martinjmartinez.proyectofinal.Entities.Space;
import com.example.martinjmartinez.proyectofinal.Models.DeviceFB;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class DeviceService {

    private Realm realm;
    private SpaceService spaceService;
    private BuildingService buildingService;
    private DatabaseReference databaseReference;

    public DeviceService (Realm realm) {
        this.realm = realm;
        spaceService = new SpaceService(realm);
        buildingService = new BuildingService(realm);
        databaseReference = FirebaseDatabase.getInstance().getReference("Devices");
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

    public void createDevice(DeviceFB deviceFB) {
        String name = deviceFB.getName();
        boolean isOn = deviceFB.isStatus();
        String ip_address = deviceFB.getIp_address();
        String spaceId = deviceFB.getSpaceId();
        String buildingId = deviceFB.getBuildingId();
        double powerAverage = deviceFB.getAverageConsumption();
        boolean isActive = deviceFB.isActive();
        double power = deviceFB.getPower();

        Space space = spaceService.getSpaceById(spaceId);
        Building building = buildingService.getBuildingById(buildingId);

        String deviceId = databaseReference.push().getKey();
        DeviceFB deviceFB2 = new DeviceFB(deviceId, name, isOn, ip_address, spaceId, isActive, buildingId, powerAverage, power);

        databaseReference.child(deviceId).setValue(deviceFB2);

        realm.beginTransaction();

        Device device = realm.createObject(Device.class, deviceId);

        device.setName(name);
        device.setStatus(isOn);
        device.setBuilding(building);
        device.setSpace(space);
        device.setIp_address(ip_address);
        device.setPower(power);
        device.setAverageConsumption(powerAverage);
        device.setActive(isActive);

        realm.commitTransaction();
    }

    public Device getDeviceById(String _id) {
        Device device = realm.where(Device.class).equalTo("_id", _id).findFirst();

        return device;
    }

//    public void updateOrCreateDevice(DeviceFB device) {
//        if(getDeviceById(_id) != null) {
//            updateDevice(device);
//        } else {
//            //createDevice(name, isOn, ip_address, spaceId, buildingId, powerAverage, isActive);
//        }
//    }

    public void updateDeviceLocal(DeviceFB deviceFB) {
        String _id = deviceFB.get_id();
        Device device = getDeviceById(_id);
        String name = deviceFB.getName();
        boolean isOn = deviceFB.isStatus();
        String ip_address = deviceFB.getIp_address();
        String spaceId = deviceFB.getSpaceId();
        String buildingId = deviceFB.getBuildingId();
        double powerAverage = deviceFB.getAverageConsumption();
        boolean isActive = deviceFB.isActive();
        double power = deviceFB.getPower();

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
        device.setPower(power);

        realm.commitTransaction();
    }
    public void updateDeviceCloud(DeviceFB deviceFB) {
        String _id = deviceFB.get_id();

        String name = deviceFB.getName();
        boolean isOn = deviceFB.isStatus();
        String ip_address = deviceFB.getIp_address();
        String spaceId = deviceFB.getSpaceId();
        String buildingId = deviceFB.getBuildingId();
        double powerAverage = deviceFB.getAverageConsumption();
        boolean isActive = deviceFB.isActive();
        double power = deviceFB.getPower();

        databaseReference.child(_id).child("name").setValue(name);
        databaseReference.child(_id).child("status").setValue(isOn);
        databaseReference.child(_id).child("ip_address").setValue(ip_address);
        databaseReference.child(_id).child("spaceId").setValue(spaceId);
        databaseReference.child(_id).child("power").setValue(power);
        databaseReference.child(_id).child("buildingId").setValue(buildingId);
        databaseReference.child(_id).child("averageConsumption").setValue(powerAverage);
        databaseReference.child(_id).child("active").setValue(isActive);

        updateDeviceLocal(deviceFB);
    }

    public void updateDeviceLastHistoryId(String _id, String lastHistoryId) {
        Device device = getDeviceById(_id);

        databaseReference.child(_id).child("lastHistoryId").setValue(lastHistoryId);

        realm.beginTransaction();

        device.setLastHistoryId(lastHistoryId);

        realm.commitTransaction();
    }

    public void updateDeviceStatus(String _id, boolean isOn) {
        Device device = getDeviceById(_id);

        databaseReference.child(_id).child("status").setValue(isOn);

        realm.beginTransaction();

        device.setStatus(isOn);

        realm.commitTransaction();
    }

    public void updateDevicePower(String _id, double power) {
        Device device = getDeviceById(_id);

        databaseReference.child(_id).child("power").setValue(power);

        realm.beginTransaction();

        device.setPower(power);

        realm.commitTransaction();
    }

    public void updateDevicePowerAverageConsumption(String _id) {
        Device device = getDeviceById(_id);

        if (device != null) {
            double sum = 0;
            double average = 0;

            if (!device.getHistorials().isEmpty()) {
                for (Historial historial : device.getHistorials()) {
                    sum = sum + historial.getPowerAverage();
                }
                average = sum/device.getHistorials().size();
            }

            databaseReference.child(_id).child("averageConsumption").setValue(average);

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

        databaseReference.child(_id).child("active").setValue(false);
        databaseReference.child(_id).child("status").setValue(false);

        realm.beginTransaction();

        device.setActive(false);

        realm.commitTransaction();
    }
}
