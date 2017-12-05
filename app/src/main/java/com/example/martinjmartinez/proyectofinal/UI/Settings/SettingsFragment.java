package com.example.martinjmartinez.proyectofinal.UI.Settings;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.martinjmartinez.proyectofinal.Entities.Settings;
import com.example.martinjmartinez.proyectofinal.Models.SettingsFB;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Services.SettingsService;
import com.example.martinjmartinez.proyectofinal.UI.MainActivity.MainActivity;
import com.example.martinjmartinez.proyectofinal.Utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ValueEventListener;

import io.realm.Realm;


public class SettingsFragment extends Fragment {

    private Activity mActivity;
    private MainActivity mMainActivity;
    private EditText cat1Price, cat2Price, cat3Price, cat4Price, fixed1Price, fixed2Price;
    private Button spanishButton, englishButton, saveButton;
    private ValueEventListener settingsListener;
    private SettingsService settingsService;
    private boolean isEnglish;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private Settings settings;

    public SettingsFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment, container, false);

        iniVariables(view);
        initListeners();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mActivity = getActivity();
        mMainActivity = (MainActivity) mActivity;

    }

    @Override
    public void onResume() {
        super.onResume();

        mMainActivity.getSupportActionBar().setTitle(R.string.settings);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (mMainActivity.getSupportFragmentManager().getBackStackEntryCount() <= 1) {
            mMainActivity.toggleDrawerIcon(true, 0, null);
        }

    }

    private void iniVariables(View view) {
        settingsService = new SettingsService(Realm.getDefaultInstance());
        cat1Price = view.findViewById(R.id.cat1_price);
        cat2Price = view.findViewById(R.id.cat2_price);
        cat3Price = view.findViewById(R.id.cat3_price);
        cat4Price = view.findViewById(R.id.cat4_price);
        spanishButton = view.findViewById(R.id.spanish_button);
        englishButton = view.findViewById(R.id.english_button);
        saveButton = view.findViewById(R.id.save_button);
        fixed1Price = view.findViewById(R.id.fixed1_price);
        fixed2Price = view.findViewById(R.id.fixed2_price);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        settings = settingsService.getSettingsById(currentUser.getUid());
        if(settings != null) {
            initDeviceView(settingsService.castToSettingsFB(settings));
        }
    }

    private void initListeners() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cat1PriceString = Utils.isEditTextEmpty(cat1Price) ? "0.0" : cat1Price.getText().toString();
                String cat2PriceString = Utils.isEditTextEmpty(cat2Price) ? "0.0" : cat2Price.getText().toString();
                String cat3PriceString = Utils.isEditTextEmpty(cat3Price) ? "0.0" : cat3Price.getText().toString();
                String cat4PriceString = Utils.isEditTextEmpty(cat4Price) ? "0.0" : cat4Price.getText().toString();
                String fixed1PriceString = Utils.isEditTextEmpty(fixed1Price) ? "0.0" : fixed1Price.getText().toString();
                String fixed2PriceString = Utils.isEditTextEmpty(fixed2Price) ? "0.0" : fixed2Price.getText().toString();


                double cat1PriceNumber = Double.parseDouble(cat1PriceString);
                double cat2PriceNumber = Double.parseDouble(cat2PriceString);
                double cat3PriceNumber = Double.parseDouble(cat3PriceString);
                double cat4PriceNumber = Double.parseDouble(cat4PriceString);
                double fixed1PriceNumber = Double.parseDouble(fixed1PriceString);
                double fixed2PriceNumber = Double.parseDouble(fixed2PriceString);
                //todo change language
                SettingsFB settingsFB = new SettingsFB(currentUser.getUid(), cat1PriceNumber, cat2PriceNumber, cat3PriceNumber, cat4PriceNumber, fixed1PriceNumber, fixed2PriceNumber, true);

                settingsService.updateDeviceCloud(settingsFB);

                mActivity.onBackPressed();
            }
        });

        mMainActivity.toggleDrawerIcon(false, R.drawable.ic_action_back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainActivity.onBackPressed();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();


    }

    private void initDeviceView(SettingsFB settingsFB) {
        cat1Price.setText(String.valueOf(settingsFB.getCat1Price()));
        cat2Price.setText(String.valueOf(settingsFB.getCat2Price()));
        cat3Price.setText(String.valueOf(settingsFB.getCat3Price()));
        cat4Price.setText(String.valueOf(settingsFB.getCat4Price()));
        fixed1Price.setText(String.valueOf(settingsFB.getFixed1Price()));
        fixed2Price.setText(String.valueOf(settingsFB.getFixed2Price()));

        //todo set buttons depending the language
    }
}
