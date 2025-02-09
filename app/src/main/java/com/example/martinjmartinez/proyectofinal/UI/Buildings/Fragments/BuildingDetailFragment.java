package com.example.martinjmartinez.proyectofinal.UI.Buildings.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.martinjmartinez.proyectofinal.Entities.Building;
import com.example.martinjmartinez.proyectofinal.Entities.MonthlyLimit;
import com.example.martinjmartinez.proyectofinal.Entities.Space;
import com.example.martinjmartinez.proyectofinal.Models.BuildingFB;
import com.example.martinjmartinez.proyectofinal.Models.GroupMonthConsumed;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Services.BuildingLimitsService;
import com.example.martinjmartinez.proyectofinal.Services.BuildingService;
import com.example.martinjmartinez.proyectofinal.UI.Buildings.Statistics.BuildingStatistics;
import com.example.martinjmartinez.proyectofinal.UI.Buildings.Statistics.BuildingStatisticsDetailsFragment;
import com.example.martinjmartinez.proyectofinal.UI.MainActivity.MainActivity;
import com.example.martinjmartinez.proyectofinal.Utils.Constants;
import com.example.martinjmartinez.proyectofinal.Utils.DateUtils;
import com.example.martinjmartinez.proyectofinal.Utils.FragmentKeys;
import com.example.martinjmartinez.proyectofinal.Utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.acl.Group;

import io.realm.Realm;

public class BuildingDetailFragment extends Fragment {

    private Building mBuilding;
    private Activity mActivity;
    private String mBuildingId;
    private TextView mNameTextView;
    private TextView mSpacesTextView;
    private TextView mDevicesTextView;
    private Button mEditButton;
    private TextView percentageTextView, actualConsumptionTextView, limitTextView;
    private ProgressBar progressBar;
    private Button mDeleteButton;
    private TextView mPowerTextView;
    private TextView mInfoTextView;
    private MainActivity mMainActivity;
    private BuildingService buildingService;
    private DatabaseReference databaseReference;
    private Context context;
    private ValueEventListener buildingListener;
    private ValueEventListener limitListener;
    private LinearLayout statisticsButton;
    private String userId;
    private DatabaseReference limitReference;
    private BuildingLimitsService buildingLimitsService;

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

        mMainActivity.getSupportActionBar().setTitle(R.string.building_details);
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
        buildingLimitsService = new BuildingLimitsService(Realm.getDefaultInstance());
        mBuilding = new Building();
        mNameTextView = view.findViewById(R.id.building_detail_name);
        mSpacesTextView = view.findViewById(R.id.building_detail_spaces);
        mEditButton = view.findViewById(R.id.building_detail_update);
        percentageTextView = view.findViewById(R.id.building_detail_limit_percentage);
        actualConsumptionTextView = view.findViewById(R.id.building_actual_limit);
        mDeleteButton = view.findViewById(R.id.building_detail_delete);
        progressBar = view.findViewById(R.id.building_limit_progressbas);
        limitTextView = view.findViewById(R.id.building_detail_limit);
        mPowerTextView = view.findViewById(R.id.building_detail_average);
        mDevicesTextView = view.findViewById(R.id.building_detail_devices);
        mInfoTextView = view.findViewById(R.id.building_detail_delete_info);
        statisticsButton = view.findViewById(R.id.building_statistics_button);
        userId = mActivity.getSharedPreferences(Constants.USER_INFO, 0).getString(Constants.USER_ID, FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference = FirebaseDatabase.getInstance().getReference("Accounts/" + userId + "/Buildings");
        limitReference = FirebaseDatabase.getInstance().getReference("Accounts/" + userId + "/BuildingMonthlyConsumed");
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

        statisticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuildingStatistics buildingStatistics = new BuildingStatistics();
                Bundle bundle = new Bundle();

                bundle.putString(Constants.BUILDING_ID, mBuildingId);
                buildingStatistics.setArguments(bundle);

                Utils.loadContentFragment(getFragmentManager().findFragmentByTag(FragmentKeys.BUILDING_DETAIL_FRAGMENT),
                        buildingStatistics, FragmentKeys.BUILDING_STATISTICS_FRAGMENT, true);
            }
        });

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = Utils.createDialog(mActivity, getString(R.string.delete_building), getString(R.string.delete_building_message));

                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        buildingService.deleteBuilding(mBuildingId);
                        mActivity.onBackPressed();
                        //deleteBuilding(mAPI.getClient());
                    }
                });

                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
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

        limitListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (isAdded()) {
                    GroupMonthConsumed groupMonthConsumed = dataSnapshot.getValue(GroupMonthConsumed.class);
                    if (groupMonthConsumed != null) {
                        buildingLimitsService.updateOrCreateLocal(groupMonthConsumed);
                        MonthlyLimit monthlyLimit = buildingLimitsService.getMonthlyById(groupMonthConsumed.get_id(), mBuildingId);
                        initMonthView(monthlyLimit);
                    } else {
                        MonthlyLimit monthlyLimit = buildingLimitsService.getMonthlyById(groupMonthConsumed.get_id(), mBuildingId);
                        initMonthView(monthlyLimit);
                    }
                }
                limitReference.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mMainActivity.toggleDrawerIcon(false, R.drawable.ic_action_back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainActivity.onBackPressed();
            }
        });

        buildingListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                BuildingFB buildingFB = dataSnapshot.getValue(BuildingFB.class);
                buildingService.updateBuildingLocal(buildingFB);
                Building building = buildingService.getBuildingById(mBuildingId);
                String monthId = DateUtils.getMonthAndYear(DateUtils.getCurrentDate());
                limitReference.child(mBuildingId).child(monthId).addValueEventListener(limitListener);
                initView(building);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReference.child(mBuildingId).addValueEventListener(buildingListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        databaseReference.child(mBuildingId).removeEventListener(buildingListener);
    }

    private void getBuilding() {
        mBuilding = buildingService.getBuildingById(mBuildingId);

        initView(mBuilding);
    }

    private void initMonthView(MonthlyLimit monthlyLimit) {
        if (monthlyLimit != null) {
            if (monthlyLimit.getLimit() == 0) {
                limitTextView.setText(R.string.Not_set);
            } else {
                progressBar.setMax(Double.valueOf(monthlyLimit.getLimit()).intValue());
                progressBar.setProgress(Double.valueOf(monthlyLimit.getTotalConsumed()).intValue());
                limitTextView.setText(monthlyLimit.getLimit() + " W/h");
                actualConsumptionTextView.setText(Utils.decimalFormat.format(monthlyLimit.getTotalConsumed()));
                double percentage = (monthlyLimit.getTotalConsumed() / monthlyLimit.getLimit()) * 100;
                if (percentage >= 100) {
                    progressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.alert)));
                } else {
                    if (percentage < 100 && percentage >= 75) {
                        progressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.color4)));
                    }
                }

                percentageTextView.setText(Utils.decimalFormat.format(percentage) + "%");
            }
            buildingService.updateBuildingLimit(mBuildingId, monthlyLimit.getLimit());
        } else {
            limitTextView.setText(R.string.Not_set);
        }

    }

    private void initView(Building building) {

        mNameTextView.setText(building.getName());
        mPowerTextView.setText(Utils.decimalFormat.format(building.getAverageConsumption()) + " W");
        if (building.getSpaces() != null) {
            mSpacesTextView.setText(buildingService.allActiveSpaces(mBuildingId).size() + "");
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
        for (Space space : buildingService.allActiveSpaces(mBuildingId)) {
            total = space.getDevices().size() + total;
        }
        return total;
    }
}
