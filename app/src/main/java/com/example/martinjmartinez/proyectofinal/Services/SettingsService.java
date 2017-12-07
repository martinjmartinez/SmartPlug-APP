package com.example.martinjmartinez.proyectofinal.Services;

import com.example.martinjmartinez.proyectofinal.Entities.Settings;
import com.example.martinjmartinez.proyectofinal.Models.SettingsFB;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.realm.Realm;

public class SettingsService {
    private Realm realm;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    public SettingsService(Realm realm) {
        this.realm = realm;

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users/"+ currentUser.getUid() + "/Settings");
    }

    public Settings getSettingsById(String _id) {
        Settings settings= realm.where(Settings.class).equalTo("_id", _id).findFirst();

        return settings;
    }

    public SettingsFB castToSettingsFB(final Settings settings) {
        SettingsFB settingsFB = new SettingsFB(settings.get_id(), settings.getCat1Price(), settings.getCat2Price(), settings.getCat3Price(), settings.getCat4Price(),settings.getFixed1Price(), settings.getFixed2Price(), settings.isEnglish());

        return settingsFB;
    }

    public void createSettingsLocal(SettingsFB settingsFB) {

        realm.beginTransaction();
        Settings settings= realm.createObject(Settings.class, settingsFB.get_id());

        settings.setCat1Price(settingsFB.getCat1Price());
        settings.setCat2Price(settingsFB.getCat2Price());
        settings.setCat3Price(settingsFB.getCat3Price());
        settings.setCat4Price(settingsFB.getCat4Price());
        settings.setFixed1Price(settingsFB.getFixed1Price());
        settings.setFixed2Price(settingsFB.getFixed2Price());
        settings.setEnglish(settingsFB.isEnglish());

        realm.commitTransaction();
    }

    public void updateDeviceLocal(SettingsFB settingsFB) {
        Settings settings = getSettingsById(settingsFB.get_id());

        if (settings != null) {
            realm.beginTransaction();

            settings.setCat1Price(settingsFB.getCat1Price());
            settings.setCat2Price(settingsFB.getCat2Price());
            settings.setCat3Price(settingsFB.getCat3Price());
            settings.setCat4Price(settingsFB.getCat4Price());
            settings.setFixed1Price(settingsFB.getFixed1Price());
            settings.setFixed2Price(settingsFB.getFixed2Price());
            settings.setEnglish(settingsFB.isEnglish());

            realm.commitTransaction();
        } else {
            createSettingsLocal(settingsFB);
        }



    }

    public void updateDeviceCloud(SettingsFB settingsFB) {
        String _id = settingsFB.get_id();

        databaseReference.child("cat1Price").setValue(settingsFB.getCat1Price());
        databaseReference.child("cat2Price").setValue(settingsFB.getCat2Price());
        databaseReference.child("cat3Price").setValue(settingsFB.getCat3Price());
        databaseReference.child("cat4Price").setValue(settingsFB.getCat4Price());
        databaseReference.child("fixed1Price").setValue(settingsFB.getFixed1Price());
        databaseReference.child("fixed2Price").setValue(settingsFB.getFixed2Price());
        databaseReference.child("english").setValue(settingsFB.isEnglish());

        Settings settings = getSettingsById(_id);
        if (settings != null) {
            updateDeviceLocal(settingsFB);
        } else {
            createSettingsLocal(settingsFB);
        }
    }

}
