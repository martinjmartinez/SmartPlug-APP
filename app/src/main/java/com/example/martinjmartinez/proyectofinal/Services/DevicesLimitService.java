package com.example.martinjmartinez.proyectofinal.Services;


import com.example.martinjmartinez.proyectofinal.Entities.Device;
import com.example.martinjmartinez.proyectofinal.Entities.MonthlyLimit;
import com.example.martinjmartinez.proyectofinal.Models.DeviceFB;
import com.example.martinjmartinez.proyectofinal.Models.DevicesMonthConsumed;
import com.example.martinjmartinez.proyectofinal.Models.HistorialFB;
import com.example.martinjmartinez.proyectofinal.Utils.DateUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import io.realm.Realm;

public class DevicesLimitService {

    private Realm realm;
    private DatabaseReference databaseReference;
    private DatabaseReference historyDatabaseReference;
    private FirebaseAuth mAuth;
    private DeviceService deviceService;
    private SpacesLimitsService spacesLimitsService;
    private BuildingLimitsService buildingLimitsService;

    public DevicesLimitService(Realm realm) {
        this.realm = realm;
        deviceService = new DeviceService(realm);
        spacesLimitsService = new SpacesLimitsService(realm);
        buildingLimitsService = new BuildingLimitsService(realm);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Accounts/" + currentUser.getUid() + "/MonthlyConsumed");
        historyDatabaseReference = FirebaseDatabase.getInstance().getReference("Accounts/" + currentUser.getUid() + "/Histories");
    }


    public void updateOrCreateCloud(final HistorialFB historialFB) {
        final String _id = historialFB.getDeviceId();
        final Device device = deviceService.getDeviceById(historialFB.getDeviceId());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean exist = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getKey().equals(_id)) {
                        exist = true;
                        String monthId = DateUtils.getMonthAndYear(new Date(historialFB.getStartDate()));
                        if (!snapshot.hasChild(monthId)) {
                            DevicesMonthConsumed devicesMonthConsumed = new DevicesMonthConsumed(monthId, false, false, false, device.getMonthlyLimit(), device.isAutoTurnOff(), historialFB.getDeviceId(), new Date().getTime());
                            databaseReference.child(_id).child(monthId).setValue(devicesMonthConsumed);
                        }
                    }
                }

                if (!exist) {
                    String monthId = DateUtils.getMonthAndYear(new Date(historialFB.getStartDate()));
                    DevicesMonthConsumed devicesMonthConsumed = new DevicesMonthConsumed(monthId, false, false, false, device.getMonthlyLimit(), device.isAutoTurnOff(), historialFB.getDeviceId(), new Date().getTime());
                    databaseReference.child(_id).child(monthId).setValue(devicesMonthConsumed);
                }

                spacesLimitsService.updateOrCreateCloud(historialFB);
                buildingLimitsService.updateOrCreateCloud(historialFB);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void updateOrCreateCloud(final DeviceFB deviceFB) {
        final String _id = deviceFB.get_id();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean exist = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getKey().equals(_id)) {
                        exist = true;
                        String monthId = DateUtils.getMonthAndYear(DateUtils.getCurrentDate());
                        if (!snapshot.hasChild(monthId)) {
                            DevicesMonthConsumed devicesMonthConsumed = new DevicesMonthConsumed(monthId, false, false, false, deviceFB.getMonthlyLimit(), deviceFB.isAutoTurnOff(), deviceFB.get_id(), new Date().getTime());
                            historyDatabaseReference.child(deviceFB.getLastHistoryId()).child("deviceLimit").setValue(deviceFB.getMonthlyLimit());
                            databaseReference.child(_id).child(monthId).setValue(devicesMonthConsumed);
                        } else {
                            if (snapshot.child(monthId).child("limit").getValue(Double.class) != deviceFB.getMonthlyLimit()) {
                                databaseReference.child(_id).child(monthId).child("limit").setValue(deviceFB.getMonthlyLimit());
                                databaseReference.child(_id).child(monthId).child("halfReachedNotificationSend").setValue(false);
                                databaseReference.child(_id).child(monthId).child("limitReachedNotificationSend").setValue(false);
                                databaseReference.child(_id).child(monthId).child("almostReachNotificationSend").setValue(false);
                                historyDatabaseReference.child(deviceFB.getLastHistoryId()).child("deviceLimit").setValue(deviceFB.getMonthlyLimit());
                            }
                            databaseReference.child(_id).child(monthId).child("autoTurnOff").setValue(deviceFB.isAutoTurnOff());
                        }
                    }
                }

                if (!exist) {
                    String monthId = DateUtils.getMonthAndYear(DateUtils.getCurrentDate());
                    DevicesMonthConsumed devicesMonthConsumed = new DevicesMonthConsumed(monthId, false, false, false, deviceFB.getMonthlyLimit(), deviceFB.isAutoTurnOff(), deviceFB.get_id(), new Date().getTime());
                    databaseReference.child(_id).child(monthId).setValue(devicesMonthConsumed);
                    if (deviceFB.getLastHistoryId() != null) {
                        historyDatabaseReference.child(deviceFB.getLastHistoryId()).child("deviceLimit").setValue(deviceFB.getMonthlyLimit());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void updateOrCreateLocal(DevicesMonthConsumed devicesMonthConsumed) {
        MonthlyLimit monthlyLimit = getMonthlyById(devicesMonthConsumed.get_id(), devicesMonthConsumed.getDeviceId());
        if (monthlyLimit != null) {
            android.util.Log.e("LIMITID", monthlyLimit.get_id() + "KLK");
            updateMonthlyLimitLocal(devicesMonthConsumed);
        } else {
            createMonthlyLimitLocal(devicesMonthConsumed);
        }
    }

    public MonthlyLimit getMonthlyById(String _id, String deviceId) {
        MonthlyLimit monthlyLimit = realm.where(MonthlyLimit.class).equalTo("_id", _id + "_" + deviceId).findFirst();
        return monthlyLimit;
    }

    public void createMonthlyLimitLocal(DevicesMonthConsumed devicesMonthConsumed) {
        Device device = deviceService.getDeviceById(devicesMonthConsumed.getDeviceId());
        realm.beginTransaction();

        MonthlyLimit monthlyLimit= realm.createObject(MonthlyLimit.class, devicesMonthConsumed.get_id() + "_" + devicesMonthConsumed.getDeviceId());
        monthlyLimit.setMonth(devicesMonthConsumed.get_id());
        monthlyLimit.setDevice(device);
        monthlyLimit.setDate(new Date(devicesMonthConsumed.getDate()));
        monthlyLimit.setAutoTurnOff(devicesMonthConsumed.isAutoTurnOff());
        monthlyLimit.setAccumulatedConsumed(devicesMonthConsumed.getAccumulatedConsumed());
        monthlyLimit.setTotalConsumed(devicesMonthConsumed.getTotalConsumed());
        monthlyLimit.setLimit(devicesMonthConsumed.getLimit());
        monthlyLimit.setLiveConsumed(devicesMonthConsumed.getLiveConsumed());

        realm.commitTransaction();
    }

    public void updateMonthlyLimitLocal(DevicesMonthConsumed devicesMonthConsumed) {
        Device device = deviceService.getDeviceById(devicesMonthConsumed.getDeviceId());
        MonthlyLimit monthlyLimit = getMonthlyById(devicesMonthConsumed.get_id(), devicesMonthConsumed.getDeviceId());

        realm.beginTransaction();
        monthlyLimit.setMonth(devicesMonthConsumed.get_id());
        monthlyLimit.setDevice(device);
        monthlyLimit.setDate(new Date(devicesMonthConsumed.getDate()));
        monthlyLimit.setAutoTurnOff(devicesMonthConsumed.isAutoTurnOff());
        monthlyLimit.setAccumulatedConsumed(devicesMonthConsumed.getAccumulatedConsumed());
        monthlyLimit.setTotalConsumed(devicesMonthConsumed.getTotalConsumed());
        monthlyLimit.setLimit(devicesMonthConsumed.getLimit());
        monthlyLimit.setLiveConsumed(devicesMonthConsumed.getLiveConsumed());

        realm.commitTransaction();
    }
}
