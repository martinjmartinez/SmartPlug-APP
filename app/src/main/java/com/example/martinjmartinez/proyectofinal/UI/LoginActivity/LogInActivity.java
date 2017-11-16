package com.example.martinjmartinez.proyectofinal.UI.LoginActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.martinjmartinez.proyectofinal.Models.UserFB;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Services.UserService;
import com.example.martinjmartinez.proyectofinal.UI.LaunchLoader.LoaderActivity;

import com.example.martinjmartinez.proyectofinal.Utils.Constants;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.iid.FirebaseInstanceId;


public class LogInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 123;
    private UserService userService;
    private Button signInButton;
    private EditText emailField;
    private EditText passwordField;
    private SignInButton signInGoogle;
    private EditText confirnPassword;
    private EditText userName;
    private boolean doubleBackToExitPressedOnce;
    private GoogleSignInOptions gso;
    private FirebaseUser currentUser;
    private TextView signUp;
    private GoogleApiClient googleApiClient;
    private SharedPreferences settings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        initVariables();
        initListenners();
        settings = getSharedPreferences(Constants.USER_INFO, 0);
    }

    public void initVariables() {
        signInButton = findViewById(R.id.normal_sign_in_buttom);
        emailField = findViewById(R.id.email_field);
        passwordField = findViewById(R.id.password_field);
        signInGoogle = findViewById(R.id.sign_in_google);
        signUp = findViewById(R.id.sign_up_text);
        mAuth = FirebaseAuth.getInstance();
        confirnPassword = findViewById(R.id.confirm_password_field);
        userName = findViewById(R.id.name_field);
        mAuth = FirebaseAuth.getInstance();
        userService = new UserService();

        // Configure Google Sign In
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    public void signInWithGoogle() {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("LOGIN", getString(R.string.sign_in_fail_google), e);
                // ...
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.isEmailVerified()) {
            updateData();
        }
    }

    public void firebaseAuthWithGoogle(GoogleSignInAccount result) {
        AuthCredential credential = GoogleAuthProvider.getCredential(result.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            UserFB userFB = new UserFB(user.getUid(), user.getDisplayName(), user.getEmail(), FirebaseInstanceId.getInstance().getToken());
                            userService.createOrUpdateUser(userFB);
                            settings.edit().putString(Constants.USER_ID, user.getUid()).apply();
                            updateData();
                        } else {
                            Toast.makeText(LogInActivity.this, R.string.sign_in_fail_google,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void initListenners() {
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailField.getText().toString();
                String password = passwordField.getText().toString();
                String confirmpass = confirnPassword.getText().toString();

                if (confirnPassword.getVisibility() == View.GONE) {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LogInActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information

                                        FirebaseUser user = mAuth.getCurrentUser();
                                        UserFB userFB = new UserFB(user.getUid(), user.getDisplayName(), user.getEmail(), FirebaseInstanceId.getInstance().getToken());
                                        userService.updateUserFB(userFB);
                                        settings.edit().putString(Constants.USER_ID, user.getUid()).apply();
                                        if (user.isEmailVerified()) {
                                            Log.d("LOGIN", "signInWithEmail:success");
                                            updateData();
                                        } else {
                                            Toast.makeText(LogInActivity.this, R.string.email_verification_message,
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w("LOGIN", "signInWithEmail:failure", task.getException());
                                        Toast.makeText(LogInActivity.this, R.string.login_fail_message,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    if (password.equals(confirmpass)) {
                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(LogInActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            final FirebaseUser user = mAuth.getCurrentUser();
                                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                    .setDisplayName(userName.getText().toString()).build();
                                            user.updateProfile(profileUpdates);
                                            UserFB userFB = new UserFB(user.getUid(), user.getDisplayName(), user.getEmail(), FirebaseInstanceId.getInstance().getToken());
                                            userService.createOrUpdateUser(userFB);
                                            settings.edit().putString(Constants.USER_ID, user.getUid()).apply();
                                            onBackPressed();
                                            user.sendEmailVerification()
                                                    .addOnCompleteListener(LogInActivity.this, new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(LogInActivity.this,
                                                                        "Verification email sent to " + user.getEmail(),
                                                                        Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Toast.makeText(LogInActivity.this,
                                                                        "Failed to send verification email.",
                                                                        Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        } else {

                                            Toast.makeText(LogInActivity.this, R.string.login_fail_message,
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(LogInActivity.this, "Password didn't match!",
                                Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirnPassword.setVisibility(View.VISIBLE);
                userName.setVisibility(View.VISIBLE);
            }
        });

        signInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithGoogle();
            }
        });
    }

    public void updateData() {
        Intent intent = new Intent(this, LoaderActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        if (confirnPassword.getVisibility() == View.VISIBLE) {
            confirnPassword.setVisibility(View.GONE);
            userName.setVisibility(View.GONE);
        }
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
