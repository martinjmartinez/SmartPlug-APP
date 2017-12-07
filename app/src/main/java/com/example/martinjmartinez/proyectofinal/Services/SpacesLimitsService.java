package com.example.martinjmartinez.proyectofinal.Services;


import com.example.martinjmartinez.proyectofinal.Entities.Device;
import com.example.martinjmartinez.proyectofinal.Entities.MonthlyLimit;
import com.example.martinjmartinez.proyectofinal.Entities.Space;
import com.example.martinjmartinez.proyectofinal.Models.HistorialFB;
import com.example.martinjmartinez.proyectofinal.Models.SpaceFB;
import com.example.martinjmartinez.proyectofinal.Models.GroupMonthConsumed;
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

public class SpacesLimitsService {
    private Realm realm;
    private DatabaseReference databaseReference;
    private DatabaseReference devicesComsumtions;
    private FirebaseUser currentUser;

    private FirebaseAuth mAuth;
    private SpaceService spaceService;
    private BuildingLimitsService buildingLimitsService;

    public SpacesLimitsService(Realm realm) {
        this.realm = realm;
        spaceService = new SpaceService(realm);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        buildingLimitsService = new BuildingLimitsService(realm);
        databaseReference = FirebaseDatabase.getInstance().getReference("Accounts/" + currentUser.getUid() + "/SpacesMonthlyConsumed");
    }


    public void updateOrCreateCloud(final HistorialFB historialFB) {
        final String _id = historialFB.getSpaceId();
        final Space space = spaceService.getSpaceById(historialFB.getSpaceId());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean exist = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getKey().equals(_id)) {
                        exist = true;
                        String monthId = DateUtils.getMonthAndYear(new Date(historialFB.getStartDate()));
                        if (!snapshot.hasChild(monthId)) {
                            GroupMonthConsumed groupMonthConsumed = new GroupMonthConsumed(monthId, space.getMonthlyLimit(), 0, historialFB.getSpaceId(), new Date().getTime(), false, false, false);
                            databaseReference.child(_id).child(monthId).setValue(groupMonthConsumed);
                            for (Device device : space.getDevices()) {
                                MonthlyLimit monthlyLimit = realm.where(MonthlyLimit.class).equalTo("device._id", device.get_id()).equalTo("month", monthId).findFirst();
                                databaseReference.child(_id).child(monthId).child("DevicesConsumptions").child(monthlyLimit.getDevice().get_id()).setValue(monthlyLimit.getTotalConsumed());
                            }
                        }
                    }
                }

                if (!exist) {
                    String monthId = DateUtils.getMonthAndYear(new Date(historialFB.getStartDate()));
                    GroupMonthConsumed groupMonthConsumed = new GroupMonthConsumed(monthId, space.getMonthlyLimit(), 0, historialFB.getSpaceId(), new Date().getTime(), false, false, false);
                    databaseReference.child(_id).child(monthId).setValue(groupMonthConsumed);
                    for (Device device : space.getDevices()) {
                        MonthlyLimit monthlyLimit = realm.where(MonthlyLimit.class).equalTo("device._id", device.get_id()).equalTo("month", monthId).findFirst();
                        databaseReference.child(_id).child(monthId).child("DevicesConsumptions").child(monthlyLimit.getDevice().get_id()).setValue(monthlyLimit.getTotalConsumed());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    public void updateOrCreateCloud(final SpaceFB spaceFB) {
        final String _id = spaceFB.get_id();
        final Space space = spaceService.getSpaceById(spaceFB.get_id());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean exist = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getKey().equals(_id)) {
                        exist = true;
                        String monthId = DateUtils.getMonthAndYear(DateUtils.getCurrentDate());
                        if (!snapshot.hasChild(monthId)) {
                            GroupMonthConsumed groupMonthConsumed = new GroupMonthConsumed(monthId, spaceFB.getMonthlyLimit(), 0, spaceFB.get_id(), new Date().getTime(), false, false, false);
                            databaseReference.child(_id).child(monthId).setValue(groupMonthConsumed);
                            for (Device device : space.getDevices()) {
                                MonthlyLimit monthlyLimit = realm.where(MonthlyLimit.class).equalTo("device._id", device.get_id()).equalTo("month", monthId).findFirst();
                                databaseReference.child(_id).child(monthId).child("DevicesConsumptions").child(monthlyLimit.getDevice().get_id()).setValue(monthlyLimit.getTotalConsumed());
                            }
                        } else {
                            if (snapshot.child(monthId).child("limit").getValue(Double.class) != spaceFB.getMonthlyLimit()) {
                                databaseReference.child(_id).child(monthId).child("limit").setValue(spaceFB.getMonthlyLimit());
                                databaseReference.child(_id).child(monthId).child("halfReachedNotificationSend").setValue(false);
                                databaseReference.child(_id).child(monthId).child("limitReachedNotificationSend").setValue(false);
                                databaseReference.child(_id).child(monthId).child("almostReachNotificationSend").setValue(false);
                            }
                        }
                    }
                }

                if (!exist) {
                    String monthId = DateUtils.getMonthAndYear(DateUtils.getCurrentDate());
                    GroupMonthConsumed groupMonthConsumed = new GroupMonthConsumed(monthId, spaceFB.getMonthlyLimit(), 0, spaceFB.get_id(), new Date().getTime(), false, false, false);
                    databaseReference.child(_id).child(monthId).setValue(groupMonthConsumed);
                    for (Device device : space.getDevices()) {
                        MonthlyLimit monthlyLimit = realm.where(MonthlyLimit.class).equalTo("device._id", device.get_id()).equalTo("month", monthId).findFirst();
                        databaseReference.child(_id).child(monthId).child("DevicesConsumptions").child(monthlyLimit.getDevice().get_id()).setValue(monthlyLimit.getTotalConsumed());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void updateDevices(final String prevSpace, final String newSpace, final String deviceId) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String monthId = DateUtils.getMonthAndYear(DateUtils.getCurrentDate());
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(prevSpace != null) {
                        if (!prevSpace.equals(newSpace)) {
                            if (snapshot.getKey().equals(prevSpace)) {
                                databaseReference.child(prevSpace).child(monthId).child("DevicesConsumptions").child(deviceId).removeValue();
                                getConsumption(prevSpace);
                            } else if (snapshot.getKey().equals(newSpace)) {
                                MonthlyLimit monthlyLimit = realm.where(MonthlyLimit.class).equalTo("device._id", deviceId).equalTo("month", monthId).findFirst();
                                databaseReference.child(newSpace).child(monthId).child("DevicesConsumptions").child(deviceId).setValue(monthlyLimit.getTotalConsumed());
                                getConsumption(newSpace);
                            }
                        }
                    } else {
                        if (snapshot.getKey().equals(newSpace)) {
                            MonthlyLimit monthlyLimit = realm.where(MonthlyLimit.class).equalTo("device._id", deviceId).equalTo("month", monthId).findFirst();
                            databaseReference.child(newSpace).child(monthId).child("DevicesConsumptions").child(deviceId).setValue(monthlyLimit.getTotalConsumed());
                            getConsumption(newSpace);
                        }
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getConsumption(final String newSpace) {
        final String monthId = DateUtils.getMonthAndYear(DateUtils.getCurrentDate());
        final Space space = spaceService.getSpaceById(newSpace);
        DatabaseReference newDevicesComsumtions = FirebaseDatabase.getInstance().getReference("Accounts/" + currentUser.getUid() + "/SpacesMonthlyConsumed/" + newSpace + "/" + monthId + "/DevicesConsumptions");
        final DatabaseReference buildingShit = FirebaseDatabase.getInstance().getReference("Accounts/" + currentUser.getUid() + "/BuildingMonthlyConsumed/" + space.getBuilding().get_id() + "/" + monthId + "/SpacesConsumptions");
        newDevicesComsumtions.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Double totalConsumption2 = 0.0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Double value = snapshot.getValue(Double.class);
                    if (value != null) {
                        totalConsumption2 = totalConsumption2 + value;
                    }
                }
                databaseReference.child(newSpace).child(monthId).child("totalConsumed").setValue(totalConsumption2);
                buildingShit.child(newSpace).setValue(totalConsumption2);
                buildingLimitsService.getConsumption(space.getBuilding().get_id());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void updateOrCreateLocal(GroupMonthConsumed monthConsumed) {
        MonthlyLimit monthlyLimit = getMonthlyById(monthConsumed.get_id(), monthConsumed.getObjectId());
        if (monthlyLimit != null) {
            android.util.Log.e("LIMITID", monthlyLimit.get_id() + "KLK");
            updateMonthlyLimitLocal(monthConsumed);
        } else {
            createMonthlyLimitLocal(monthConsumed);
        }
    }

    public MonthlyLimit getMonthlyById(String _id, String spaceId) {
        MonthlyLimit monthlyLimit = realm.where(MonthlyLimit.class).equalTo("_id", _id + "_" + spaceId).findFirst();
        return monthlyLimit;
    }

    public void createMonthlyLimitLocal(GroupMonthConsumed monthConsumed) {
        Space space = spaceService.getSpaceById(monthConsumed.getObjectId());
        realm.beginTransaction();

        MonthlyLimit monthlyLimit= realm.createObject(MonthlyLimit.class, monthConsumed.get_id() + "_" + monthConsumed.getObjectId());
        monthlyLimit.setMonth(monthConsumed.get_id());
        monthlyLimit.setSpace(space);
        monthlyLimit.setDate(new Date(monthConsumed.getDate()));
        monthlyLimit.setTotalConsumed(monthConsumed.getTotalConsumed());
        monthlyLimit.setLimit(monthConsumed.getLimit());

        realm.commitTransaction();
    }

    public void updateMonthlyLimitLocal(GroupMonthConsumed monthConsumed) {
        Space space = spaceService.getSpaceById(monthConsumed.getObjectId());
        MonthlyLimit monthlyLimit = getMonthlyById(monthConsumed.get_id(), monthConsumed.getObjectId());

        realm.beginTransaction();
        monthlyLimit.setMonth(monthConsumed.get_id());
        monthlyLimit.setSpace(space);
        monthlyLimit.setDate(new Date(monthConsumed.getDate()));
        monthlyLimit.setTotalConsumed(monthConsumed.getTotalConsumed());
        monthlyLimit.setLimit(monthConsumed.getLimit());

        realm.commitTransaction();
    }
}
