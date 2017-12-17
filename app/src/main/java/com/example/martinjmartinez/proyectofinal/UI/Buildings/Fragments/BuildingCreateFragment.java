package com.example.martinjmartinez.proyectofinal.UI.Buildings.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.martinjmartinez.proyectofinal.Models.BuildingFB;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Services.BuildingLimitsService;
import com.example.martinjmartinez.proyectofinal.Services.BuildingService;
import com.example.martinjmartinez.proyectofinal.UI.MainActivity.MainActivity;
import com.example.martinjmartinez.proyectofinal.Utils.Constants;
import com.example.martinjmartinez.proyectofinal.Utils.Utils;
import com.google.firebase.auth.FirebaseAuth;

import io.realm.Realm;

public class BuildingCreateFragment extends Fragment {
    private Activity mActivity;
    private EditText name, limit;
    private TextView displayName;
    private Button saveBuilding;
    private MainActivity mMainActivity;
    private BuildingService buildingService;
    private BuildingLimitsService buildingLimitsService;
    private String userId;

    public BuildingCreateFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.building_creation_fragment, container, false);

        iniVariables();
        initView(view);
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

        mMainActivity.getSupportActionBar().setTitle(R.string.new_building);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (mMainActivity.getSupportFragmentManager().getBackStackEntryCount() <= 1) {
            mMainActivity.toggleDrawerIcon(true, 0, null);
        }

    }

    private void iniVariables() {
        buildingService = new BuildingService(Realm.getDefaultInstance());
        buildingLimitsService = new BuildingLimitsService(Realm.getDefaultInstance());
        userId = mActivity.getSharedPreferences(Constants.USER_INFO, 0).getString(Constants.USER_ID, FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    private void initListeners() {
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                displayName.setText(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                displayName.setText(s.toString());
            }
        });

        saveBuilding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utils.isEditTextEmpty(name)) {
                    BuildingFB buildingFB = new BuildingFB();
                    buildingFB.setName(name.getText().toString());
                    buildingFB.setActive(true);
                    buildingFB.setUid(userId);
                    buildingService.createBuildingCloud(buildingFB);
                    buildingFB.setMonthlyLimit(Utils.isEditTextEmpty(limit) ? 0.0 : Double.parseDouble(limit.getText().toString()));
                    buildingLimitsService.updateOrCreateCloud(buildingFB);
                    if (buildingService.allBuildings().size() > 1) {
                        mActivity.onBackPressed();
                    } else {
                        mMainActivity.prepareHomeFragment(false);
                    }

                } else {
                    Toast.makeText(getActivity(), R.string.name_building_mesage, Toast.LENGTH_SHORT).show();
                }
            }
        });

        mMainActivity.toggleDrawerIcon(false, R.drawable.ic_action_back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainActivity.onBackPressed();
            }
        });
    }

    private void initView(View view) {
        name = view.findViewById(R.id.building_create_name);
        displayName =  view.findViewById(R.id.building_create_display_name);
        saveBuilding =  view.findViewById(R.id.building_create_save_button);
        limit = view.findViewById(R.id.building_create_limit);
    }
}
