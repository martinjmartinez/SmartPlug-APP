package com.example.martinjmartinez.proyectofinal.Models;

public class UserFB {

    private String uid;

    private String name;

    private String email;

    private String FCM_TOKEN;

    public UserFB() {
    }

    public UserFB(String uid, String name, String email, String FCM_TOKEN) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.FCM_TOKEN = FCM_TOKEN;
    }

    public UserFB(String uid, String name, String email) {
        this.uid = uid;
        this.name = name;
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFCM_TOKEN() {
        return FCM_TOKEN;
    }

    public void setFCM_TOKEN(String FCM_TOKEN) {
        this.FCM_TOKEN = FCM_TOKEN;
    }
}
