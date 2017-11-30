package com.example.martinjmartinez.proyectofinal.Services;

import android.util.Log;

import com.example.martinjmartinez.proyectofinal.Entities.Building;
import com.example.martinjmartinez.proyectofinal.Entities.Device;
import com.example.martinjmartinez.proyectofinal.Entities.Historial;
import com.example.martinjmartinez.proyectofinal.Entities.Space;
import com.example.martinjmartinez.proyectofinal.Models.DeviceFB;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class DeviceService {

    private Realm realm;
    private SpaceService spaceService;
    private BuildingService buildingService;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    public DeviceService(Realm realm) {
        this.realm = realm;
        spaceService = new SpaceService(realm);
        buildingService = new BuildingService(realm);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Accounts/"+ currentUser.getUid() + "/Devices");
    }

    public List<Device> allDevices() {
        RealmResults<Device> results = realm.where(Device.class).equalTo("isActive", true).findAll();

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

    public String createDeviceCloud(DeviceFB deviceFB) {
        String deviceId = databaseReference.push().getKey();
        deviceFB.set_id(deviceId);

        databaseReference.child(deviceId).child("_id").setValue(deviceId);
        databaseReference.child(deviceId).child("name").setValue(deviceFB.getName());
        databaseReference.child(deviceId).child("status").setValue(deviceFB.isStatus());
        databaseReference.child(deviceId).child("spaceId").setValue(deviceFB.getSpaceId());
        databaseReference.child(deviceId).child("power").setValue(deviceFB.getPower());
        databaseReference.child(deviceId).child("autoTurnOff").setValue(deviceFB.isAutoTurnOff());
        databaseReference.child(deviceId).child("monthlyLimit").setValue(deviceFB.getMonthlyLimit());
        databaseReference.child(deviceId).child("inConfigMode").setValue(deviceFB.isInConfigMode());
        databaseReference.child(deviceId).child("reset").setValue(deviceFB.isReset());
        databaseReference.child(deviceId).child("buildingId").setValue(deviceFB.getBuildingId());
        databaseReference.child(deviceId).child("averageConsumption").setValue(deviceFB.getAverageConsumption());
        databaseReference.child(deviceId).child("active").setValue(deviceFB.isActive());

        createDeviceLocal(deviceFB);

        return deviceId;
    }

    public void updateOrCreate(DeviceFB deviceFB){
        Device device = getDeviceById(deviceFB.get_id());
        if(device != null){
            updateDeviceLocal(deviceFB);
        } else {
            createDeviceLocal(deviceFB);
        }
    }

    public void createDeviceLocal(DeviceFB deviceFB) {
        Space space = spaceService.getSpaceById(deviceFB.getSpaceId());
        Building building = buildingService.getBuildingById(deviceFB.getBuildingId());

        realm.beginTransaction();
        Device device = realm.createObject(Device.class, deviceFB.get_id());

        device.setName(deviceFB.getName());
        device.setStatus(deviceFB.isStatus());
        device.setBuilding(building);
        device.setSpace(space);
        device.setMonthlyLimit(deviceFB.getMonthlyLimit());
        device.setConnected(deviceFB.isConnected());
        device.setAutoTurnOff(deviceFB.isAutoTurnOff());
        device.setInConfigMode(deviceFB.isInConfigMode());
        device.setSsid(deviceFB.getSsid());
        device.setPower(deviceFB.getPower());
        device.setAverageConsumption(deviceFB.getAverageConsumption());
        device.setActive(deviceFB.isActive());

        realm.commitTransaction();
    }

    public Device getDeviceById(String _id) {
        Device device = realm.where(Device.class).equalTo("_id", _id).findFirst();

        return device;
    }

    public DeviceFB castToDeviceFB(final Device device) {
        DeviceFB deviceFB = new DeviceFB();
        deviceFB.set_id(device.get_id());
        deviceFB.setName(device.getName());
        deviceFB.setStatus(device.isStatus());
        deviceFB.setConnected(device.isConnected());
        deviceFB.setMonthlyLimit(device.getMonthlyLimit());
        deviceFB.setBuildingId(device.getBuilding().get_id());
        if(device.getSpace()!= null){
            deviceFB.setSpaceId(device.getSpace().get_id());
        }
        deviceFB.setAverageConsumption(device.getAverageConsumption());
        deviceFB.setActive(device.isActive());
        deviceFB.setInConfigMode(device.isInConfigMode());
        deviceFB.setSsid(device.getSsid());
        deviceFB.setAutoTurnOff(device.isAutoTurnOff());
        deviceFB.setPower(device.getPower());
        deviceFB.setLastHistoryId(device.getLastHistoryId());
        if(device.getLastTimeUsed() != null){
            deviceFB.setLastTimeUsed(device.getLastTimeUsed().getTime());
        }

        return deviceFB;
    }

    public void updateDeviceLocal(DeviceFB deviceFB) {
        Device device = getDeviceById(deviceFB.get_id());
        Space space = spaceService.getSpaceById(deviceFB.getSpaceId());
        Building building = buildingService.getBuildingById(deviceFB.getBuildingId());

        realm.beginTransaction();

        device.setName(deviceFB.getName());
        device.setStatus(deviceFB.isStatus());
        device.setBuilding(building);
        device.setSpace(space);
        device.setMonthlyLimit(deviceFB.getMonthlyLimit());
        Log.e("limit3", deviceFB.getMonthlyLimit() + "ppp");
        device.setConnected(deviceFB.isConnected());
        device.setAverageConsumption(deviceFB.getAverageConsumption());
        device.setActive(deviceFB.isActive());
        device.setAutoTurnOff(deviceFB.isAutoTurnOff());
        device.setInConfigMode(deviceFB.isInConfigMode());
        device.setSsid(deviceFB.getSsid());
        device.setPower(deviceFB.getPower());
        device.setLastHistoryId(deviceFB.getLastHistoryId());
        device.setLastTimeUsed(new Date(deviceFB.getLastTimeUsed()));

        realm.commitTransaction();

        updateDevicePowerAverageConsumption(device.get_id());
    }

    public void updateDeviceCloud(DeviceFB deviceFB) {
        String _id = deviceFB.get_id();

        databaseReference.child(_id).child("name").setValue(deviceFB.getName());
        databaseReference.child(_id).child("status").setValue(deviceFB.isStatus());
        databaseReference.child(_id).child("spaceId").setValue(deviceFB.getSpaceId());
        databaseReference.child(_id).child("power").setValue(deviceFB.getPower());
        databaseReference.child(_id).child("inConfigMode").setValue(deviceFB.isInConfigMode());
        databaseReference.child(_id).child("connected").setValue(deviceFB.isConnected());
        databaseReference.child(_id).child("autoTurnOff").setValue(deviceFB.isAutoTurnOff());
        databaseReference.child(_id).child("monthlyLimit").setValue(deviceFB.getMonthlyLimit());
        Log.e("limit2", deviceFB.getMonthlyLimit() + "ppp");
        databaseReference.child(_id).child("reset").setValue(deviceFB.isReset());
        databaseReference.child(_id).child("ssid").setValue(deviceFB.getSsid());
        databaseReference.child(_id).child("buildingId").setValue(deviceFB.getBuildingId());
        databaseReference.child(_id).child("averageConsumption").setValue(deviceFB.getAverageConsumption());
        databaseReference.child(_id).child("active").setValue(deviceFB.isActive());

        updateDeviceLocal(deviceFB);
    }

    public void  updateDeviceLastHistoryId(String _id, String lastHistoryId) {
        Device device = getDeviceById(_id);

        databaseReference.child(_id).child("lastHistoryId").setValue(lastHistoryId);

        realm.beginTransaction();

        device.setLastHistoryId(lastHistoryId);

        realm.commitTransaction();
    }

    public void  updateDeviceAutoTurnOff(String _id, boolean autoTurnOff) {
        Device device = getDeviceById(_id);

        databaseReference.child(_id).child("autoTurnOff").setValue(autoTurnOff);

        realm.beginTransaction();

        device.setAutoTurnOff(autoTurnOff);

        realm.commitTransaction();
    }

    public void  updateDeviceReset(String _id, boolean reset) {
        databaseReference.child(_id).child("reset").setValue(reset);
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

    public void updateDeviceLimit(String _id, double limit) {
        Device device = getDeviceById(_id);

        databaseReference.child(_id).child("monthlyLimit").setValue(limit);

        realm.beginTransaction();

        device.setMonthlyLimit(limit);

        realm.commitTransaction();
    }

    public void updateDevicePowerAverageConsumption(String _id) {
        Device device = getDeviceById(_id);

        if (device != null) {
            double sum = 0;
            double average = 0;
            int counter = 0;

            if (!device.getHistorials().isEmpty()) {
                for (Historial historial : device.getHistorials()) {
                    if(historial.getLastLogDate().getTime() != 0){
                        sum = sum + historial.getPowerAverage();
                        counter++;
                    }
                }
                if(counter >0){
                    average = sum / counter;
                    databaseReference.child(_id).child("averageConsumption").setValue(average);
                }
            }

            realm.beginTransaction();

            device.setAverageConsumption(average);

            realm.commitTransaction();

            if (device.getSpace() != null) {
                spaceService.updateSpacePowerAverageConsumption(device.getSpace().get_id());
            }
        }
    }

    public void deleteDevice(String _id) {
        Device device = getDeviceById(_id);

        databaseReference.child(_id).child("active").setValue(false);
        databaseReference.child(_id).child("status").setValue(false);
        databaseReference.child(_id).child("reset").setValue(true);

        realm.beginTransaction();

        device.setActive(false);

        realm.commitTransaction();
    }
}
