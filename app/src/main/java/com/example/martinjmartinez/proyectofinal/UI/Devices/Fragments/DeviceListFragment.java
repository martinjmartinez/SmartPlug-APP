package com.example.martinjmartinez.proyectofinal.UI.Devices.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.martinjmartinez.proyectofinal.Entities.Device;
import com.example.martinjmartinez.proyectofinal.Entities.Space;
import com.example.martinjmartinez.proyectofinal.Models.DeviceFB;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Services.BuildingService;
import com.example.martinjmartinez.proyectofinal.Services.DeviceService;
import com.example.martinjmartinez.proyectofinal.Services.SpaceService;
import com.example.martinjmartinez.proyectofinal.UI.Devices.Adapters.DeviceListAdapter;
import com.example.martinjmartinez.proyectofinal.UI.MainActivity.MainActivity;
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

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class DeviceListFragment extends Fragment {

    private RecyclerView mGridView;

    private String mBuildingId, mSpaceId;
    private FloatingActionButton mAddDeviceButton;
    private Activity mActivity;
    private Space mSpace;
    private List<Device> mDevicesList;
    private MainActivity mMainActivity;
    private DeviceListAdapter mDevicesListAdapter;
    private DeviceService deviceService;
    private BuildingService buildingService;
    private SpaceService spaceService;
    private LinearLayout mEmptyDeviceListLayout;
    private Realm realm;
    private DatabaseReference databaseReference;
    private ValueEventListener devicesListener;
    private String userId;

    public DeviceListFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getArgumentsBundle();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mActivity = getActivity();
        mMainActivity = (MainActivity) mActivity;
        getArgumentsBundle();

    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (mMainActivity.getSupportFragmentManager().getBackStackEntryCount() <= 1) {
            mMainActivity.toggleDrawerIcon(true, 0, null);
        }

    }

    public void getArgumentsBundle() {
        Bundle bundle = this.getArguments();
        mBuildingId = bundle != null ? bundle.getString(Constants.BUILDING_ID, "") : "";
        mSpaceId = bundle != null ? bundle.getString(Constants.SPACE_ID, "") : "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.device_list_fragment, container, false);

        iniVariables(view);

        initListeners();
        if (!mBuildingId.isEmpty() && mSpaceId.isEmpty()) {
            getDevicesByBuilding();
        } else if (mBuildingId.isEmpty() && !mSpaceId.isEmpty()) {
            getDevicesBySpace();
        }


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        mMainActivity.getSupportActionBar().setTitle("Devices");
    }

    private void iniVariables(View view) {
        realm = Realm.getDefaultInstance();
        deviceService = new DeviceService(realm);
        buildingService = new BuildingService(realm);
        spaceService = new SpaceService(realm);
        mDevicesList = new ArrayList<>();
        mGridView = view.findViewById(R.id.devices_grid);
        mAddDeviceButton =  view.findViewById(R.id.add_device_button);
        mEmptyDeviceListLayout =  view.findViewById(R.id.empty_device_list_layout);
        userId = mActivity.getSharedPreferences(Constants.USER_INFO, 0).getString(Constants.USER_ID, FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference = FirebaseDatabase.getInstance().getReference("Accounts/"+ userId + "/Devices");
    }

    private void initListeners() {
        mAddDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DevicePairFragment devicePairFragment = new DevicePairFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.BUILDING_ID, mBuildingId);
                bundle.putString(Constants.SPACE_ID, mSpaceId);
                bundle.putBoolean(Constants.IS_NEW_DEVICE, true);

                devicePairFragment.setArguments(bundle);
                Utils.loadContentFragment(getFragmentManager().findFragmentByTag(FragmentKeys.DEVICE_LIST_FRAGMENT), devicePairFragment, FragmentKeys.DEVICE_PAIR_FRAGMENT, true);
            }
        });

        mMainActivity.toggleDrawerIcon(false, R.drawable.ic_action_back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainActivity.onBackPressed();
            }
        });

        devicesListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    DeviceFB deviceFB = dataSnapshot1.getValue(DeviceFB.class);

                    deviceService.updateDeviceLocal(deviceFB);
                }

                if(mDevicesListAdapter != null) {
                    mDevicesListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReference.addValueEventListener(devicesListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        databaseReference.removeEventListener(devicesListener);
    }

    private void getDevicesByBuilding() {
        mDevicesList = deviceService.allActiveDevicesByBuilding(mBuildingId);

        shouldEmptyMessageShow();
    }


    private void getDevicesBySpace() {
        mDevicesList = deviceService.allActiveDevicesBySpace(mSpaceId);

        shouldEmptyMessageShow();
    }

    private void shouldEmptyMessageShow() {
        if (!mDevicesList.isEmpty()) {
            mEmptyDeviceListLayout.setVisibility(View.GONE);
            initDevicesList(mDevicesList);
        } else {
            mGridView.setVisibility(View.GONE);
            mEmptyDeviceListLayout.setVisibility(View.VISIBLE);
            mDevicesListAdapter = new DeviceListAdapter(mDevicesList, getActivity(), this);
        }
    }

    private void initDevicesList(List<Device> devicesList) {
        mGridView.setLayoutManager(new GridLayoutManager(mActivity, 2));
        mDevicesListAdapter = new DeviceListAdapter(devicesList, getActivity(), this);
        mGridView.setAdapter(mDevicesListAdapter);
    }
}
