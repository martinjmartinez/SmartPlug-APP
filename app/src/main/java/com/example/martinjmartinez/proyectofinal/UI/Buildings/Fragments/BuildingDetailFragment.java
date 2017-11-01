package com.example.martinjmartinez.proyectofinal.UI.Buildings.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.martinjmartinez.proyectofinal.Entities.Building;
import com.example.martinjmartinez.proyectofinal.Entities.Space;
import com.example.martinjmartinez.proyectofinal.Models.BuildingFB;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Services.BuildingService;
import com.example.martinjmartinez.proyectofinal.UI.MainActivity.MainActivity;
import com.example.martinjmartinez.proyectofinal.Utils.API;
import com.example.martinjmartinez.proyectofinal.Utils.Constants;
import com.example.martinjmartinez.proyectofinal.Utils.FragmentKeys;
import com.example.martinjmartinez.proyectofinal.Utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.realm.Realm;

public class BuildingDetailFragment extends Fragment {

    private Building mBuilding;
    private Activity mActivity;
    private String mBuildingId;
    private TextView mNameTextView;
    private TextView mSpacesTextView;
    private TextView mDevicesTextView;
    private Button mEditButton;
    private Button mDeleteButton;
    private TextView mPowerTextView;
    private TextView mInfoTextView;
    private MainActivity mMainActivity;
    private BuildingService buildingService;
    private DatabaseReference databaseReference;
    private Context context;

    public BuildingDetailFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        mBuildingId = bundle != null ? bundle.getString(Constants.BUILDING_ID, "") : "";
        context = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.building_fragment, container, false);

        iniVariables(view);
        initListeners();
        getBuilding();

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

        mMainActivity.getSupportActionBar().setTitle("Building Details");
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
        mNameTextView = (TextView) view.findViewById(R.id.building_detail_name);
        mSpacesTextView = (TextView) view.findViewById(R.id.building_detail_spaces);
        mEditButton = (Button) view.findViewById(R.id.building_detail_update);
        mDeleteButton = (Button) view.findViewById(R.id.building_detail_delete);
        mPowerTextView = (TextView) view.findViewById(R.id.building_detail_power);
        mDevicesTextView = (TextView) view.findViewById(R.id.building_detail_devices);
        mInfoTextView = (TextView) view.findViewById(R.id.building_detail_delete_info);
        databaseReference = FirebaseDatabase.getInstance().getReference("Buildings");
    }

    private void initListeners() {
        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuildingUpdateFragment buildingUpdateFragment = new BuildingUpdateFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.BUILDING_ID, mBuilding.get_id());
                buildingUpdateFragment.setArguments(bundle);
                Utils.loadContentFragment(getFragmentManager().findFragmentByTag(FragmentKeys.BUILDING_DETAIL_FRAGMENT),
                        buildingUpdateFragment, FragmentKeys.BUILDING_UPDATE_FRAGMENT, true);
            }
        });

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = Utils.createDialog(mActivity, "Delete Building", "Do you want to delete this Building?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        buildingService.deleteBuilding(mBuildingId);
                        mActivity.onBackPressed();
                        //deleteBuilding(mAPI.getClient());
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                // Create the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        mMainActivity.toggleDrawerIcon(false, R.drawable.ic_action_back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainActivity.onBackPressed();
            }
        });

        databaseReference.child(mBuildingId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                BuildingFB buildingFB = dataSnapshot.getValue(BuildingFB.class);
                buildingService.updateBuildingName(buildingFB);
                Building building = buildingService.getBuildingById(mBuildingId);
                initView(building);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getBuilding() {
        mBuilding = buildingService.getBuildingById(mBuildingId);

        initView(mBuilding);
    }

    private void initView(Building building) {

        mNameTextView.setText(building.getName());

        if (building.getSpaces() != null) {
            mSpacesTextView.setText(building.getSpaces().size() + "");
            int devices = getBuildingDevices();
            mDevicesTextView.setText(devices + "");
            if (devices > 0) {
                mDeleteButton.setClickable(false);
                mInfoTextView.setVisibility(View.VISIBLE);
                mDeleteButton.setBackgroundColor(ContextCompat.getColor(context, R.color.disabled));
            } else {
                mDeleteButton.setClickable(true);
                mInfoTextView.setVisibility(View.GONE);
                mDeleteButton.setBackgroundColor(ContextCompat.getColor(context, R.color.alert));
            }
        }
    }

    private int getBuildingDevices() {
        int total = 0;
        for (Space space : mBuilding.getSpaces()) {
            total = space.getDevices().size() + total;
        }
        return total;
    }
}
