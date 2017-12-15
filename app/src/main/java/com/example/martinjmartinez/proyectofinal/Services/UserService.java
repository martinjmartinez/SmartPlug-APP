package com.example.martinjmartinez.proyectofinal.Services;


import android.util.Log;

import com.example.martinjmartinez.proyectofinal.Models.SettingsFB;
import com.example.martinjmartinez.proyectofinal.Models.UserFB;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.realm.Realm;

public class UserService {
    private DatabaseReference databaseReferenceAccounts;
    private DatabaseReference databaseReferenceUsers;
    private SettingsService settingsService;


    public UserService() {
        databaseReferenceAccounts = FirebaseDatabase.getInstance().getReference("Accounts");
        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference("Users");

    }

    public void createOrUpdateUser(final UserFB user) {
        databaseReferenceUsers.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserFB userFB = dataSnapshot.getValue(UserFB.class);
                settingsService = new SettingsService(Realm.getDefaultInstance());
                if(userFB != null){

                    updateUserFB(user);
                } else {
                    createUserFB(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void createUserFB(UserFB userFB) {
        //todo change depending the languaje
        SettingsFB settingsFB = new SettingsFB(userFB.getUid(),0,0,0,0,0,0,true);
        databaseReferenceUsers.child(userFB.getUid()).child("Settings").setValue(settingsFB);
        settingsService.createSettingsLocal(settingsFB);

        databaseReferenceUsers.child(userFB.getUid()).child("Information").setValue(userFB);
        databaseReferenceAccounts.child(userFB.getUid()).child("Information").setValue(userFB);
    }

    public void deleteUserFCMToken(UserFB userFB) {
        databaseReferenceUsers.child(userFB.getUid()).child("Information").child("fcm_TOKEN").setValue("");
    }

    public void updateUserFCMToken(UserFB userFB) {
        databaseReferenceUsers.child(userFB.getUid()).child("Information").child("fcm_TOKEN").setValue(userFB.getFCM_TOKEN());
    }

    public void updateUserFB(UserFB userFB) {
        databaseReferenceUsers.child(userFB.getUid()).child("Information").setValue(userFB);
        databaseReferenceAccounts.child(userFB.getUid()).child("Information").child("uid").setValue(userFB.getUid());
        databaseReferenceAccounts.child(userFB.getUid()).child("Information").child("email").setValue(userFB.getEmail());
        databaseReferenceAccounts.child(userFB.getUid()).child("Information").child("name").setValue(userFB.getName());
    }

}
