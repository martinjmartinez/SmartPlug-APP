package com.example.martinjmartinez.proyectofinal.Services;

import com.example.martinjmartinez.proyectofinal.Entities.Building;
import com.example.martinjmartinez.proyectofinal.Entities.Device;
import com.example.martinjmartinez.proyectofinal.Entities.Routine;
import com.example.martinjmartinez.proyectofinal.Models.RoutineFB;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;

public class RoutineService {
    private Realm realm;
    private DeviceService deviceService;
    private BuildingService buildingService;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    public RoutineService(Realm realm) {
        this.realm = realm;

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        buildingService = new BuildingService(realm);
        deviceService = new DeviceService(realm);
        databaseReference = FirebaseDatabase.getInstance().getReference("Accounts/"+ currentUser.getUid() + "/Routines");
    }

    public void updateOrCreateLocal(RoutineFB routineFB) {
        Routine routine = getRoutineById(routineFB.get_id());
        if (routine != null) {
            updateRoutineLocal(routineFB);
        } else {
            createRoutineLocal(routineFB);
        }
    }

    public Routine getRoutineById(String _id) {
        Routine routine = realm.where(Routine.class).equalTo("_id", _id).findFirst();
        return routine;
    }

    public List<Routine> getRoutineByBuilding(String buildingiId) {
        List<Routine> routine = realm.where(Routine.class).equalTo("building._id", buildingiId).findAll();
        return routine;
    }

    public void createRoutineLocal(RoutineFB routineFB, RealmList<Integer> weekDays) {
        Device device = deviceService.getDeviceById(routineFB.getDeviceId());
        Building building = buildingService.getBuildingById(routineFB.getBuildingId());

        realm.beginTransaction();

        Routine routine= realm.createObject(Routine.class, routineFB.get_id());
        routine.setName(routineFB.getName());
        routine.setAction(routineFB.isAction());
        routine.setDevice(device);
        routine.setBuilding(building);
        routine.setEnable(routineFB.isEnabled());
        routine.setStartTriggered(routineFB.isStartTriggered());
        routine.setEndTriggered(routineFB.isEndTriggered());
        routine.setStartTime(routineFB.getStartTime());
        routine.setEndTime(routineFB.getEndTime());
        routine.setDayOfWeek(weekDays);

        realm.commitTransaction();
    }

    public void updateRoutineLocal(RoutineFB routineFB, RealmList<Integer> weekDays) {
        Device device = deviceService.getDeviceById(routineFB.getDeviceId());
        Building building = buildingService.getBuildingById(routineFB.getBuildingId());
        Routine routine = getRoutineById(routineFB.get_id());


        realm.beginTransaction();

        routine.setName(routineFB.getName());
        routine.setAction(routineFB.isAction());
        routine.setDevice(device);
        routine.setBuilding(building);
        routine.setEnable(routineFB.isEnabled());
        routine.setStartTriggered(routineFB.isStartTriggered());
        routine.setEndTriggered(routineFB.isEndTriggered());
        routine.setStartTime(routineFB.getStartTime());
        routine.setEndTime(routineFB.getEndTime());
        routine.setDayOfWeek(weekDays);

        realm.commitTransaction();
    }

    public void updateRoutineLocal(final RoutineFB routineFB) {
        DatabaseReference weekDaysRef = FirebaseDatabase.getInstance().getReference("Accounts/" + currentUser.getUid() + "/Routines/" + routineFB.getDeviceId() + "/" + routineFB.get_id() + "/WeekDays");
        final RealmList<Integer> days = new RealmList<>();

        weekDaysRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot != null) {
                        if(snapshot.getValue(Boolean.class) == true) {
                            days.add(Integer.parseInt(snapshot.getKey()));
                        }
                    }
                }

                updateRoutineLocal(routineFB, days);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void createRoutineLocal(final RoutineFB routineFB) {
        DatabaseReference weekDaysRef = FirebaseDatabase.getInstance().getReference("Accounts/" + currentUser.getUid() + "/Routines/" + routineFB.getDeviceId() + "/" + routineFB.get_id() + "/WeekDays");
        final RealmList<Integer> days = new RealmList<>();

        weekDaysRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot != null) {
                        if(snapshot.getValue(Boolean.class) == true) {
                            days.add(Integer.parseInt(snapshot.getKey()));
                        }
                    }
                }

                createRoutineLocal(routineFB, days);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void createRoutineCloud(RoutineFB routineFB, HashMap<Integer, Boolean> weekDays) {
        String routineId = databaseReference.child(routineFB.getDeviceId()).push().getKey();
        routineFB.set_id(routineId);
        databaseReference.child(routineFB.getDeviceId()).child(routineId).setValue(routineFB);
        for(Integer integer : weekDays.keySet()) {
            databaseReference.child(routineFB.getDeviceId()).child(routineId).child("WeekDays").child(integer.toString()).setValue(weekDays.get(integer));
        }
        createRoutineLocal(routineFB);
    }

    public  void updateIsEnable(String routineId, boolean isEnable){
        Routine routine = getRoutineById(routineId);
        databaseReference.child(routine.getDevice().get_id()).child(routine.get_id()).child("enabled").setValue(isEnable);

        realm.beginTransaction();

        routine.setEnable(isEnable);

        realm.commitTransaction();
    }

    public void updateRoutineCloud(RoutineFB routineFB, HashMap<Integer, Boolean> weekDays) {
        String _id = routineFB.get_id();

        databaseReference.child(routineFB.getDeviceId()).child(_id).child("name").setValue(routineFB.getName());
        databaseReference.child(routineFB.getDeviceId()).child(_id).child("deviceId").setValue(routineFB.getDeviceId());
        databaseReference.child(routineFB.getDeviceId()).child(_id).child("startTime").setValue(routineFB.getStartTime());
        databaseReference.child(routineFB.getDeviceId()).child(_id).child("endTime").setValue(routineFB.getEndTime());
        databaseReference.child(routineFB.getDeviceId()).child(_id).child("buildingId").setValue(routineFB.getBuildingId());
        databaseReference.child(routineFB.getDeviceId()).child(_id).child("action").setValue(routineFB.isAction());
        databaseReference.child(routineFB.getDeviceId()).child(_id).child("enabled").setValue(routineFB.isEnabled());
        databaseReference.child(routineFB.getDeviceId()).child(_id).child("startTriggered").setValue(routineFB.isStartTriggered());
        databaseReference.child(routineFB.getDeviceId()).child(_id).child("endTriggered").setValue(routineFB.isEndTriggered());

        for(Integer integer : weekDays.keySet()) {
            databaseReference.child(routineFB.getDeviceId()).child(_id).child("WeekDays").child(integer.toString()).setValue(weekDays.get(integer));
        }

        updateRoutineLocal(routineFB);
    }

    public  void deleteRoutine(String routineId){
        Routine routine = getRoutineById(routineId);
        databaseReference.child(routine.getDevice().get_id()).child(routine.get_id()).removeValue();

        realm.beginTransaction();

        routine.deleteFromRealm();

        realm.commitTransaction();
    }
}
