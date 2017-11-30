package com.example.martinjmartinez.proyectofinal.Services;

import com.example.martinjmartinez.proyectofinal.App.SmartPLugApplication;
import com.example.martinjmartinez.proyectofinal.Entities.Device;
import com.example.martinjmartinez.proyectofinal.Entities.Historial;
import com.example.martinjmartinez.proyectofinal.Entities.Log;
import com.example.martinjmartinez.proyectofinal.Models.DeviceFB;
import com.example.martinjmartinez.proyectofinal.Models.HistorialFB;
import com.example.martinjmartinez.proyectofinal.Models.LogFB;
import com.example.martinjmartinez.proyectofinal.Models.MonthConsumed;
import com.example.martinjmartinez.proyectofinal.Utils.DateUtils;
import com.example.martinjmartinez.proyectofinal.Utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import io.realm.Realm;
import io.realm.annotations.PrimaryKey;

public class LimitService extends SmartPLugApplication {

    private Realm realm;
    private DatabaseReference databaseReference;
    private DatabaseReference historyDatabaseReference;
    private FirebaseAuth mAuth;
    private DeviceService deviceService;

    public LimitService (Realm realm) {
        this.realm = realm;
        deviceService = new DeviceService(realm);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Accounts/" + currentUser.getUid() + "/MonthlyConsumed");
        historyDatabaseReference = FirebaseDatabase.getInstance().getReference("Accounts/" + currentUser.getUid() + "/Histories");
    }


    public void updateOrCreate(final HistorialFB historialFB) {
        final String _id = historialFB.getDeviceId();
        final Device device = deviceService.getDeviceById(historialFB.getDeviceId());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean exist = false;
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    if(snapshot.getKey().equals(_id)){
                        exist = true;
                        String monthId = DateUtils.getMonthAndYear(new Date(historialFB.getStartDate()));
                        if(!snapshot.hasChild(monthId)) {
                            MonthConsumed monthConsumed = new MonthConsumed(monthId, false, false, false, device.getMonthlyLimit(), device.isAutoTurnOff());
                            databaseReference.child(_id).child(monthId).setValue(monthConsumed);
                        }
                    }
                }

                if (!exist) {
                    String monthId = DateUtils.getMonthAndYear(new Date(historialFB.getStartDate()));
                    MonthConsumed monthConsumed = new MonthConsumed(monthId, false, false, false, device.getMonthlyLimit(), device.isAutoTurnOff());
                    databaseReference.child(_id).child(monthId).setValue(monthConsumed);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void updateOrCreate(final DeviceFB deviceFB) {
        final String _id = deviceFB.get_id();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean exist = false;
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    if(snapshot.getKey().equals(_id)){
                        exist = true;
                        String monthId = DateUtils.getMonthAndYear(DateUtils.getCurrentDate());
                        if(!snapshot.hasChild(monthId)) {
                            MonthConsumed monthConsumed = new MonthConsumed(monthId, false, false, false, deviceFB.getMonthlyLimit(), deviceFB.isAutoTurnOff());
                            historyDatabaseReference.child(deviceFB.getLastHistoryId()).child("deviceLimit").setValue(deviceFB.getMonthlyLimit());
                            databaseReference.child(_id).child(monthId).setValue(monthConsumed);
                        } else {
                            if(snapshot.child(monthId).child("limit").getValue(Double.class) != deviceFB.getMonthlyLimit()) {
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
                    MonthConsumed monthConsumed = new MonthConsumed(monthId, false, false, false, deviceFB.getMonthlyLimit(), deviceFB.isAutoTurnOff());
                    databaseReference.child(_id).child(monthId).setValue(monthConsumed);
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
    public void updateAutoTurnOff(DeviceFB deviceFB) {
        String monthId = DateUtils.getMonthAndYear(DateUtils.getCurrentDate());
        databaseReference.child(deviceFB.get_id()).child(monthId).child("autoTurnOff").setValue(deviceFB.isAutoTurnOff());
    }

    public Log getLogById(String _id) {
        Log log = realm.where(Log.class).equalTo("_id", _id).findFirst();

        return log;
    }
}
