package com.example.martinjmartinez.proyectofinal.Services;


import com.example.martinjmartinez.proyectofinal.Entities.Building;
import com.example.martinjmartinez.proyectofinal.Entities.MonthlyLimit;
import com.example.martinjmartinez.proyectofinal.Entities.Space;
import com.example.martinjmartinez.proyectofinal.Models.BuildingFB;
import com.example.martinjmartinez.proyectofinal.Models.GroupMonthConsumed;
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

public class BuildingLimitsService {
    private Realm realm;
    private DatabaseReference databaseReference;
    private DatabaseReference devicesComsumtions;
    private FirebaseUser currentUser;

    private FirebaseAuth mAuth;
    private BuildingService buildingService;

    public BuildingLimitsService(Realm realm) {
        this.realm = realm;
        buildingService = new BuildingService(realm);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Accounts/" + currentUser.getUid() + "/BuildingMonthlyConsumed");
    }


    public void updateOrCreateCloud(final HistorialFB historialFB) {
        final String _id = historialFB.getBuildingId();
        final Building building = buildingService.getBuildingById(historialFB.getBuildingId());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean exist = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getKey().equals(_id)) {
                        exist = true;
                        String monthId = DateUtils.getMonthAndYear(new Date(historialFB.getStartDate()));
                        if (!snapshot.hasChild(monthId)) {
                            GroupMonthConsumed groupMonthConsumed = new GroupMonthConsumed(monthId, building.getMonthlyLimit(), 0, historialFB.getBuildingId(), new Date().getTime(), false, false, false);
                            databaseReference.child(_id).child(monthId).setValue(groupMonthConsumed);
                            for (Space space : building.getSpaces()) {
                                MonthlyLimit monthlyLimit = realm.where(MonthlyLimit.class).equalTo("space._id", space.get_id()).equalTo("month", monthId).findFirst();
                                databaseReference.child(_id).child(monthId).child("SpacesConsumptions").child(monthlyLimit.getSpace().get_id()).setValue(monthlyLimit.getTotalConsumed());
                            }
                        }
                    }
                }

                if (!exist) {
                    String monthId = DateUtils.getMonthAndYear(new Date(historialFB.getStartDate()));
                    GroupMonthConsumed groupMonthConsumed = new GroupMonthConsumed(monthId, building.getMonthlyLimit(), 0, historialFB.getBuildingId(), new Date().getTime(), false, false, false);
                    databaseReference.child(_id).child(monthId).setValue(groupMonthConsumed);
                    for (Space space : building.getSpaces()) {
                        MonthlyLimit monthlyLimit = realm.where(MonthlyLimit.class).equalTo("space._id", space.get_id()).equalTo("month", monthId).findFirst();
                        databaseReference.child(_id).child(monthId).child("SpacesConsumptions").child(monthlyLimit.getSpace().get_id()).setValue(monthlyLimit.getTotalConsumed());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    public void updateOrCreateCloud(final BuildingFB buildingFB) {
        final String _id = buildingFB.get_id();
        final Building building = buildingService.getBuildingById(buildingFB.get_id());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean exist = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getKey().equals(_id)) {
                        exist = true;
                        String monthId = DateUtils.getMonthAndYear(DateUtils.getCurrentDate());
                        if (!snapshot.hasChild(monthId)) {
                            GroupMonthConsumed groupMonthConsumed = new GroupMonthConsumed(monthId, buildingFB.getMonthlyLimit(), 0, buildingFB.get_id(), new Date().getTime(), false, false, false);
                            databaseReference.child(_id).child(monthId).setValue(groupMonthConsumed);
                            for (Space space : building.getSpaces()) {
                                MonthlyLimit monthlyLimit = realm.where(MonthlyLimit.class).equalTo("space._id", space.get_id()).equalTo("month", monthId).findFirst();
                                databaseReference.child(_id).child(monthId).child("SpacesConsumptions").child(monthlyLimit.getSpace().get_id()).setValue(monthlyLimit.getTotalConsumed());
                            }
                        } else {
                            if (snapshot.child(monthId).child("limit").getValue(Double.class) != buildingFB.getMonthlyLimit()) {
                                databaseReference.child(_id).child(monthId).child("limit").setValue(buildingFB.getMonthlyLimit());
                                databaseReference.child(_id).child(monthId).child("halfReachedNotificationSend").setValue(false);
                                databaseReference.child(_id).child(monthId).child("limitReachedNotificationSend").setValue(false);
                                databaseReference.child(_id).child(monthId).child("almostReachNotificationSend").setValue(false);
                            }
                        }
                    }
                }

                if (!exist) {
                    String monthId = DateUtils.getMonthAndYear(DateUtils.getCurrentDate());
                    GroupMonthConsumed groupMonthConsumed = new GroupMonthConsumed(monthId, buildingFB.getMonthlyLimit(), 0, buildingFB.get_id(), new Date().getTime(), false, false, false);
                    databaseReference.child(_id).child(monthId).setValue(groupMonthConsumed);
                    for (Space space : building.getSpaces()) {
                        MonthlyLimit monthlyLimit = realm.where(MonthlyLimit.class).equalTo("space._id", space.get_id()).equalTo("month", monthId).findFirst();
                        databaseReference.child(_id).child(monthId).child("SpacesConsumptions").child(monthlyLimit.getSpace().get_id()).setValue(monthlyLimit.getTotalConsumed());
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void addSpaceToBuildingLimit(final String buildingId, String spaceId) {
        final String monthId = DateUtils.getMonthAndYear(DateUtils.getCurrentDate());
        DatabaseReference newDevicesComsumtions = FirebaseDatabase.getInstance().getReference("Accounts/" + currentUser.getUid() + "/BuildingMonthlyConsumed/" + buildingId + "/" + monthId + "/SpacesConsumptions/" + spaceId);
        newDevicesComsumtions.setValue(0.0);
    }

    public void getConsumption(final String buildingId) {
        final String monthId = DateUtils.getMonthAndYear(DateUtils.getCurrentDate());
        DatabaseReference newDevicesComsumtions = FirebaseDatabase.getInstance().getReference("Accounts/" + currentUser.getUid() + "/BuildingMonthlyConsumed/" + buildingId + "/" + monthId + "/SpacesConsumptions");
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
                databaseReference.child(buildingId).child(monthId).child("totalConsumed").setValue(totalConsumption2);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void updateOrCreateLocal(GroupMonthConsumed monthConsumed) {
        MonthlyLimit monthlyLimit = getMonthlyById(monthConsumed.get_id(), monthConsumed.getObjectId());
        if (monthlyLimit != null) {
            updateMonthlyLimitLocal(monthConsumed);
        } else {
            createMonthlyLimitLocal(monthConsumed);
        }
    }

    public MonthlyLimit getMonthlyById(String _id, String buildingId) {
        MonthlyLimit monthlyLimit = realm.where(MonthlyLimit.class).equalTo("_id", _id + "_" + buildingId).findFirst();
        return monthlyLimit;
    }

    public void createMonthlyLimitLocal(GroupMonthConsumed monthConsumed) {
        Building building = buildingService.getBuildingById(monthConsumed.getObjectId());
        realm.beginTransaction();

        MonthlyLimit monthlyLimit = realm.createObject(MonthlyLimit.class, monthConsumed.get_id() + "_" + monthConsumed.getObjectId());
        monthlyLimit.setMonth(monthConsumed.get_id());
        monthlyLimit.setBuilding(building);
        monthlyLimit.setDate(new Date(monthConsumed.getDate()));
        monthlyLimit.setTotalConsumed(monthConsumed.getTotalConsumed());
        monthlyLimit.setLimit(monthConsumed.getLimit());

        realm.commitTransaction();
    }

    public void updateMonthlyLimitLocal(GroupMonthConsumed monthConsumed) {
        Building building = buildingService.getBuildingById(monthConsumed.getObjectId());
        MonthlyLimit monthlyLimit = getMonthlyById(monthConsumed.get_id(), monthConsumed.getObjectId());

        realm.beginTransaction();
        monthlyLimit.setMonth(monthConsumed.get_id());
        monthlyLimit.setBuilding(building);
        monthlyLimit.setDate(new Date(monthConsumed.getDate()));
        monthlyLimit.setTotalConsumed(monthConsumed.getTotalConsumed());
        monthlyLimit.setLimit(monthConsumed.getLimit());

        realm.commitTransaction();
    }
}
