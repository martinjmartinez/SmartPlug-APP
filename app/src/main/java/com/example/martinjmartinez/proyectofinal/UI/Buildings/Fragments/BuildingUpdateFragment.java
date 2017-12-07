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

import com.example.martinjmartinez.proyectofinal.Entities.Building;
import com.example.martinjmartinez.proyectofinal.Models.BuildingFB;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Services.BuildingLimitsService;
import com.example.martinjmartinez.proyectofinal.Services.BuildingService;
import com.example.martinjmartinez.proyectofinal.UI.MainActivity.MainActivity;
import com.example.martinjmartinez.proyectofinal.Utils.Constants;
import com.example.martinjmartinez.proyectofinal.Utils.Utils;

import io.realm.Realm;

public class BuildingUpdateFragment extends Fragment {

    private Building mBuilding;
    private Activity mActivity;
    private String mBuildingId;
    private EditText name, limit;
    private TextView displayName;
    private Button updateBuilding;
    private MainActivity mMainActivity;
    private BuildingService buildingService;
    private BuildingLimitsService buildingLimitsService;

    public BuildingUpdateFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mBuildingId = bundle.getString(Constants.BUILDING_ID, "");
        } else {
            mBuildingId = "";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.building_creation_fragment, container, false);

        iniVariables(view);
        getBuilding();
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

        mMainActivity.getSupportActionBar().setTitle("Building Edit");
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (mMainActivity.getSupportFragmentManager().getBackStackEntryCount() <= 1) {
            mMainActivity.toggleDrawerIcon(true, 0, null);
        }

    }

    private void iniVariables(View view) {
        buildingService = new BuildingService(Realm.getDefaultInstance());
        mBuilding = new Building();
        name = view.findViewById(R.id.building_create_name);
        limit = view.findViewById(R.id.building_create_limit);
        displayName =  view.findViewById(R.id.building_create_display_name);
        updateBuilding = view.findViewById(R.id.building_create_save_button);
        buildingLimitsService = new BuildingLimitsService(Realm.getDefaultInstance());
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

        updateBuilding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utils.isEditTextEmpty(name)) {
                    BuildingFB buildingFB =  new BuildingFB();
                    buildingFB.set_id(mBuildingId);
                    if (!name.getText().toString().equals(mBuilding.getName()) || !limit.getText().toString().equals(mBuilding.getMonthlyLimit())) {
                        if (Utils.isEditTextEmpty(limit)) {
                            buildingService.updateBuildingLimit(mBuildingId, 0.0);
                            buildingFB.setMonthlyLimit(0.0);

                        } else {
                            buildingService.updateBuildingLimit(mBuildingId, Double.parseDouble(limit.getText().toString()));
                            buildingFB.setMonthlyLimit(Double.parseDouble(limit.getText().toString()));
                        }
                        buildingFB.setName(name.getText().toString());
                        buildingFB.setUid(mBuilding.getUid());
                        buildingFB.setActive(true);
                        buildingService.updateBuildingCloud(buildingFB);
                        buildingLimitsService.updateOrCreateCloud(buildingFB);
                        mActivity.onBackPressed();
                    } else {
                        Toast.makeText(getActivity(), "Please, update something.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Please, name your building.", Toast.LENGTH_SHORT).show();
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

    private void getBuilding() {
        mBuilding = buildingService.getBuildingById(mBuildingId);
        initView(mBuilding);
    }

    private void initView(Building building) {
        name.setText(building.getName());
        displayName.setText(name.getText());
        limit.setText(building.getMonthlyLimit() + "");
    }
}
