package com.example.martinjmartinez.proyectofinal.Services;

import com.example.martinjmartinez.proyectofinal.Models.UserFB;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private UserService userService = new UserService();
    private UserFB actualUser;
    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        String token = FirebaseInstanceId.getInstance().getToken();
        if(user != null) {
            actualUser = new UserFB(user.getUid(), user.getDisplayName(), user.getEmail(), token);
            userService.updateUserFCMToken(actualUser);
        }

    }
}
