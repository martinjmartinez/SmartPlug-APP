package com.example.martinjmartinez.proyectofinal.UI.Spaces.Fragments;

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
import com.example.martinjmartinez.proyectofinal.Entities.Space;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Services.BuildingService;
import com.example.martinjmartinez.proyectofinal.Services.SpaceService;
import com.example.martinjmartinez.proyectofinal.UI.MainActivity.MainActivity;
import com.example.martinjmartinez.proyectofinal.Utils.API;
import com.example.martinjmartinez.proyectofinal.Utils.Utils;

import io.realm.Realm;

public class SpaceCreateFragment extends Fragment {

    private Space mSpace;
    private Activity mActivity;
    private EditText name;
    private TextView displayName;
    private TextView spaceBuilding;
    private Button saveSpace;
    private String mBuildingId, mSpaceId;
    private Building mBuilding;
    private MainActivity mMainActivity;
    private SpaceService spaceService;
    private BuildingService buildingService;

    public SpaceCreateFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        mBuildingId = bundle != null ? bundle.getString("BUILDING_ID", "") : "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.space_creation_fragment, container, false);

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

        mMainActivity.getSupportActionBar().setTitle("New Space");
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
        spaceService = new SpaceService(Realm.getDefaultInstance());
        mSpace = new Space();
        mActivity = getActivity();
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

        saveSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utils.isEditTextEmpty(name) && mSpace != null) {
                    spaceService.createSpace(name.getText().toString(), mBuildingId, true);
                    mActivity.onBackPressed();
                } else {
                    Toast.makeText(getActivity(), "Please, name your Space.", Toast.LENGTH_SHORT).show();
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
        name = (EditText) view.findViewById(R.id.space_create_name);
        displayName = (TextView) view.findViewById(R.id.space_create_display_name);
        spaceBuilding = (TextView) view.findViewById(R.id.space_create_building);
        saveSpace = (Button) view.findViewById(R.id.space_create_save_button);
        getBuilding();
    }

    private void getBuilding() {
        mBuilding = buildingService.getBuildingById(mBuildingId);
        spaceBuilding.setText(mBuilding.getName());
    }
}
