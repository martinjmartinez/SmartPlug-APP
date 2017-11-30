package com.example.martinjmartinez.proyectofinal.Services;


import com.example.martinjmartinez.proyectofinal.Models.UserFB;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserService {
    private DatabaseReference databaseReferenceAccounts;
    private DatabaseReference databaseReferenceUsers;

    public UserService() {
        databaseReferenceAccounts = FirebaseDatabase.getInstance().getReference("Accounts");
        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference("Users");
    }

    public void createOrUpdateUser(final UserFB user) {
        databaseReferenceUsers.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserFB userFB = dataSnapshot.getValue(UserFB.class);
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
        databaseReferenceUsers.child(userFB.getUid()).setValue(userFB);
        databaseReferenceAccounts.child(userFB.getUid()).setValue(userFB);
    }

    public void deleteUserFCMToken(UserFB userFB) {
        databaseReferenceUsers.child(userFB.getUid()).child("fcm_TOKEN").setValue("");
    }

    public void updateUserFCMToken(UserFB userFB) {
        databaseReferenceUsers.child(userFB.getUid()).child("fcm_TOKEN").setValue(userFB.getFCM_TOKEN());
    }

    public void updateUserFB(UserFB userFB) {
        databaseReferenceUsers.child(userFB.getUid()).setValue(userFB);
        databaseReferenceAccounts.child(userFB.getUid()).child("uid").setValue(userFB.getUid());
        databaseReferenceAccounts.child(userFB.getUid()).child("email").setValue(userFB.getEmail());
        databaseReferenceAccounts.child(userFB.getUid()).child("name").setValue(userFB.getName());
    }
}
