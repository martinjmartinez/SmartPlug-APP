package com.example.martinjmartinez.proyectofinal.UI.LaunchLoader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.example.martinjmartinez.proyectofinal.Models.HistorialFB;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Services.HistorialService;
import com.example.martinjmartinez.proyectofinal.UI.MainActivity.MainActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import io.realm.Realm;


public class LoaderActivity extends AppCompatActivity {


    private Realm realm;
    private HistorialService historialService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loader);

        realm = Realm.getDefaultInstance();
        historialService = new HistorialService(realm);

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
        getHistorials();
    }

    public void getHistorials() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Histories");
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                HistorialFB historialFB = dataSnapshot.getValue(HistorialFB.class);
                Log.e("Entro", "getHistorials");
                if (historialFB.getEndDate() == 0) {
                    historialService.updateHistorial(historialFB.get_id(), new Date(historialFB.getStartDate()),new Date(),historialFB.getDeviceId(), historialFB.getPowerAverage());
                } else {
                    historialService.updateHistorial(historialFB.get_id(), new Date(historialFB.getStartDate()),new Date(historialFB.getEndDate()),historialFB.getDeviceId(), historialFB.getPowerAverage());
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                loadFinished();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void loadFinished() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }
}
