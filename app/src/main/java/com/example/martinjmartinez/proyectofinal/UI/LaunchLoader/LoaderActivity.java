package com.example.martinjmartinez.proyectofinal.UI.LaunchLoader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.martinjmartinez.proyectofinal.Entities.Routine;
import com.example.martinjmartinez.proyectofinal.Models.BuildingFB;
import com.example.martinjmartinez.proyectofinal.Models.DeviceFB;
import com.example.martinjmartinez.proyectofinal.Models.DevicesMonthConsumed;
import com.example.martinjmartinez.proyectofinal.Models.GroupMonthConsumed;
import com.example.martinjmartinez.proyectofinal.Models.HistorialFB;
import com.example.martinjmartinez.proyectofinal.Models.RoutineFB;
import com.example.martinjmartinez.proyectofinal.Models.SettingsFB;
import com.example.martinjmartinez.proyectofinal.Models.SpaceFB;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Services.BuildingLimitsService;
import com.example.martinjmartinez.proyectofinal.Services.BuildingService;
import com.example.martinjmartinez.proyectofinal.Services.DeviceService;
import com.example.martinjmartinez.proyectofinal.Services.HistorialService;
import com.example.martinjmartinez.proyectofinal.Services.DevicesLimitService;
import com.example.martinjmartinez.proyectofinal.Services.RoutineService;
import com.example.martinjmartinez.proyectofinal.Services.SettingsService;
import com.example.martinjmartinez.proyectofinal.Services.SpaceService;
import com.example.martinjmartinez.proyectofinal.Services.SpacesLimitsService;
import com.example.martinjmartinez.proyectofinal.UI.MainActivity.MainActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.realm.Realm;


public class LoaderActivity extends AppCompatActivity {


    private Realm realm;
    private HistorialService historialService;
    private BuildingService buildingService;
    private SettingsService settingsService;
    private SpaceService spaceService;
    private DeviceService deviceService;
    private SpacesLimitsService spacesLimitsService;
    private BuildingLimitsService buildingLimitsService;
    private DevicesLimitService devicesLimitService;
    private RoutineService routineService;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loader);

        realm = Realm.getDefaultInstance();
        historialService = new HistorialService(realm);
        settingsService = new SettingsService(realm);
        buildingLimitsService = new BuildingLimitsService(realm);
        routineService = new RoutineService(realm);
        deviceService = new DeviceService(realm);
        spacesLimitsService = new SpacesLimitsService(realm);
        devicesLimitService = new DevicesLimitService(realm);
        buildingService = new BuildingService(realm);
        spaceService = new SpaceService(realm);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        Log.e("LOADERACTIVITY", "ENTROOO");
        getData();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void getData() {
        Log.e("getData", "ENTRO");
        getBuildings();
    }

    public void getBuildings() {
        Log.e("getBuildings", "ENTRO");
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Accounts/" + currentUser.getUid() + "/Buildings");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    BuildingFB buildingFB = dataSnapshot1.getValue(BuildingFB.class);
                    if (buildingFB != null) {
                        Log.e("LoaderActivity", "Buildings Listener");
                        buildingService.updateOrCreate(buildingFB);

                    } else {
                        Log.e("LoaderActivity", "Buildings Listener 2");
                        databaseReference.removeEventListener(this);
                    }
                }
                databaseReference.removeEventListener(this);
                getSpaces();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getSpaces() {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Accounts/" + currentUser.getUid() + "/Spaces");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    SpaceFB spaceFB = dataSnapshot1.getValue(SpaceFB.class);
                    if (spaceFB != null) {
                        Log.e("LoaderActivity", "Spaces Listener");
                        spaceService.updateOrCreate(spaceFB);

                    } else {
                        databaseReference.removeEventListener(this);
                    }
                }
                databaseReference.removeEventListener(this);
                getDevices();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getDevices() {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Accounts/" + currentUser.getUid() + "/Devices");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    DeviceFB deviceFB = dataSnapshot1.getValue(DeviceFB.class);
                    if (deviceFB != null) {
                        Log.e("LoaderActivity", "Devices Listener");
                        deviceService.updateOrCreate(deviceFB);

                    } else {
                        databaseReference.removeEventListener(this);
                    }
                }
                databaseReference.removeEventListener(this);
                getHistorials();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getHistorials() {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Accounts/" + currentUser.getUid() + "/Histories");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    HistorialFB historialFB = dataSnapshot1.getValue(HistorialFB.class);
                    if (historialFB != null) {
                        Log.e("LoaderActivity", "Historial Listener");
                        historialService.updateOrCreate(historialFB);

                    } else {
                        loadFinished();
                        databaseReference.removeEventListener(this);
                    }
                }
                // historialService.createFeakData();
                databaseReference.removeEventListener(this);
               getSettings();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getSettings() {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users/" + currentUser.getUid() + "/Settings");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                SettingsFB settingsFB = dataSnapshot.getValue(SettingsFB.class);
                if (settingsFB != null) {
                    Log.e("LoaderActivity", "Historial Listener");
                    settingsService.updateDeviceLocal(settingsFB);

                } else {
                    loadFinished();
                    databaseReference.removeEventListener(this);
                }

                databaseReference.removeEventListener(this);
                getDevicesLimits();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getDevicesLimits() {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Accounts/" + currentUser.getUid() + "/MonthlyConsumed");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                   for (DataSnapshot snapshot : dataSnapshot1.getChildren()) {
                       DevicesMonthConsumed devicesMonthConsumed = snapshot.getValue(DevicesMonthConsumed.class);
                       if(devicesMonthConsumed != null) {
                           devicesLimitService.updateOrCreateLocal(devicesMonthConsumed);
                       }
                   }
                }
                databaseReference.removeEventListener(this);
                getSpacesLimits();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void getSpacesLimits() {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Accounts/" + currentUser.getUid() + "/SpacesMonthlyConsumed");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    for (DataSnapshot snapshot : dataSnapshot1.getChildren()) {
                        GroupMonthConsumed groupMonthConsumed= snapshot.getValue(GroupMonthConsumed.class);
                        if(groupMonthConsumed != null) {
                            spacesLimitsService.updateOrCreateLocal(groupMonthConsumed);
                        }
                    }
                }
                databaseReference.removeEventListener(this);
                getBuildingLimits();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void getBuildingLimits() {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Accounts/" + currentUser.getUid() + "/BuildingMonthlyConsumed");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    for (DataSnapshot snapshot : dataSnapshot1.getChildren()) {
                        GroupMonthConsumed groupMonthConsumed= snapshot.getValue(GroupMonthConsumed.class);
                        if(groupMonthConsumed != null) {
                            buildingLimitsService.updateOrCreateLocal(groupMonthConsumed);
                        }
                    }
                }
                databaseReference.removeEventListener(this);
                getRoutines();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void getRoutines() {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Accounts/" + currentUser.getUid() + "/Routines");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    for (DataSnapshot snapshot : dataSnapshot1.getChildren()) {
                        RoutineFB routineFB = snapshot.getValue(RoutineFB.class);
                        if(routineFB != null) {
                            routineService.updateOrCreateLocal(routineFB);
                        }
                    }
                }
                databaseReference.removeEventListener(this);
                loadFinished();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void loadFinished() {
        Log.e("loadFinished", "ENTROO");
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
