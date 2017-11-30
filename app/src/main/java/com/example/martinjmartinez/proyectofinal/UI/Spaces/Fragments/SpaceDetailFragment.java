package com.example.martinjmartinez.proyectofinal.UI.Spaces.Fragments;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.martinjmartinez.proyectofinal.Entities.Device;
import com.example.martinjmartinez.proyectofinal.Entities.Space;
import com.example.martinjmartinez.proyectofinal.Models.SpaceFB;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Services.BuildingService;
import com.example.martinjmartinez.proyectofinal.Services.DeviceService;
import com.example.martinjmartinez.proyectofinal.Services.SpaceService;
import com.example.martinjmartinez.proyectofinal.UI.Devices.Statistics.DeviceStatisticsDetailsFragment;
import com.example.martinjmartinez.proyectofinal.UI.MainActivity.MainActivity;
import com.example.martinjmartinez.proyectofinal.UI.Spaces.Statistics.SpaceStatisticsDetailsFragment;
import com.example.martinjmartinez.proyectofinal.Utils.Constants;
import com.example.martinjmartinez.proyectofinal.Utils.FragmentKeys;
import com.example.martinjmartinez.proyectofinal.Utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import io.realm.Realm;

public class SpaceDetailFragment extends Fragment {

    private Space mSpace;
    private Activity mActivity;
    private String mSpaceId, userId;
    private TextView mNameTextView;
    private TextView mDevicesTextView;
    private TextView mBuildingTextView;
    private TextView mPowerTextView;
    private TextView mInfoTextView;
    private Button mEditButton;
    private Button mDeleteButton;
    private MainActivity mMainActivity;
    private SpaceService spaceService;
    private DeviceService deviceService;
    private LinearLayout statisticsButton;
    private DatabaseReference databaseReference;
    private Context context;
    private ValueEventListener spaceListener;

    public SpaceDetailFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        mSpaceId = bundle != null ? bundle.getString(Constants.SPACE_ID, "") : "";
        context = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.space_fragment, container, false);

        iniVariables(view);
        initListeners();
        getSpace();

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

        mMainActivity.getSupportActionBar().setTitle("Space Details");
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (mMainActivity.getSupportFragmentManager().getBackStackEntryCount() <= 1) {
            mMainActivity.toggleDrawerIcon(true, 0, null);
        }

    }

    private void iniVariables(View view) {
        spaceService = new SpaceService(Realm.getDefaultInstance());
        deviceService = new DeviceService(Realm.getDefaultInstance());
        mSpace = new Space();
        mNameTextView =  view.findViewById(R.id.space_detail_name);
        mDevicesTextView =  view.findViewById(R.id.space_detail_devices);
        mBuildingTextView =  view.findViewById(R.id.space_detail_building);
        mPowerTextView =  view.findViewById(R.id.space_average_power);
        mInfoTextView =  view.findViewById(R.id.space_detail_delete_info);
        mEditButton =  view.findViewById(R.id.space_detail_update);
        mDeleteButton =  view.findViewById(R.id.space_detail_delete);
        statisticsButton =  view.findViewById(R.id.space_statistics_button);
        userId = mActivity.getSharedPreferences(Constants.USER_INFO, 0).getString(Constants.USER_ID, FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference = FirebaseDatabase.getInstance().getReference("Accounts/"+ userId + "/Spaces");
    }

    private void initListeners() {
        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpaceUpdateFragment spaceUpdateFragment = new SpaceUpdateFragment();
                Bundle bundle = new Bundle();

                bundle.putString(Constants.SPACE_ID, mSpaceId);
                spaceUpdateFragment.setArguments(bundle);
                Utils.loadContentFragment(getFragmentManager().findFragmentByTag(FragmentKeys.SPACE_DETAIL_FRAGMENT),
                        spaceUpdateFragment, FragmentKeys.SPACE_UPDATE_FRAGMENT, true);
            }
        });

        statisticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpaceStatisticsDetailsFragment spaceStatisticsDetailsFragment = new SpaceStatisticsDetailsFragment();
                Bundle bundle = new Bundle();

                bundle.putString(Constants.SPACE_ID, mSpaceId);
                spaceStatisticsDetailsFragment.setArguments(bundle);

                Utils.loadContentFragment(getFragmentManager().findFragmentByTag(FragmentKeys.SPACE_DETAIL_FRAGMENT),
                        spaceStatisticsDetailsFragment, FragmentKeys.SPACE_STATISTICS_FRAGMENT, true);
            }
        });

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = Utils.createDialog(mActivity, "Delete Space", "Do you want to delete this Space?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        spaceService.deleteSpace(mSpaceId);
                        mActivity.onBackPressed();
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

        spaceListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SpaceFB spaceFB = dataSnapshot.getValue(SpaceFB.class);
                spaceService.updateOrCreate(spaceFB);
                mSpace = spaceService.getSpaceById(spaceFB.get_id());
                initSpaceView(mSpace);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReference.child(mSpaceId).addValueEventListener(spaceListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        databaseReference.child(mSpaceId).removeEventListener(spaceListener);
    }

    private void getSpace() {
        mSpace = spaceService.getSpaceById(mSpaceId);
        initSpaceView(mSpace);
    }

    private void initSpaceView(Space space) {
        mNameTextView.setText(space.getName());
        List<Device> deviceList = deviceService.allActiveDevicesBySpace(space.get_id());
        if (deviceList!= null) {
            mDevicesTextView.setText(deviceList.size() + "");
            if (!deviceList.isEmpty()) {
                mDeleteButton.setClickable(false);
                mInfoTextView.setVisibility(View.VISIBLE);
                mDeleteButton.setBackgroundColor(ContextCompat.getColor(context, R.color.disabled));
            } else {
                mDeleteButton.setClickable(true);
                mInfoTextView.setVisibility(View.GONE);
                mDeleteButton.setBackgroundColor(ContextCompat.getColor(context, R.color.alert));
            }

            if (space.getBuilding() != null) {
                mBuildingTextView.setText(space.getBuilding().getName());
            }
        }
        mPowerTextView.setText(Utils.decimalFormat.format(space.getAverageConsumption()) + "W");
    }
}
