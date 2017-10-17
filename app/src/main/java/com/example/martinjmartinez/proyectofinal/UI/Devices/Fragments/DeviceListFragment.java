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

import com.example.martinjmartinez.proyectofinal.Entities.Building;
import com.example.martinjmartinez.proyectofinal.Entities.Device;
import com.example.martinjmartinez.proyectofinal.Entities.Space;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Services.BuildingService;
import com.example.martinjmartinez.proyectofinal.Services.DeviceService;
import com.example.martinjmartinez.proyectofinal.Services.SpaceService;
import com.example.martinjmartinez.proyectofinal.UI.Devices.Adapters.DeviceListAdapter;
import com.example.martinjmartinez.proyectofinal.UI.MainActivity.MainActivity;
import com.example.martinjmartinez.proyectofinal.Utils.API;
import com.example.martinjmartinez.proyectofinal.Utils.ArgumentsKeys;
import com.example.martinjmartinez.proyectofinal.Utils.FragmentKeys;
import com.example.martinjmartinez.proyectofinal.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

/**
 * Created by MartinJMartinez on 7/14/2017.
 */

public class DeviceListFragment extends Fragment {

    private RecyclerView mGridView;
    private API mAPI;
    private Building mBuilding;
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

    public DeviceListFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // getArgumentsBundle();

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
        mBuildingId = bundle != null ? bundle.getString(ArgumentsKeys.BUILDING_ID, "") : "";
        mSpaceId = bundle != null ? bundle.getString(ArgumentsKeys.SPACE_ID, "") : "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.device_list_fragment, container, false);

        iniVariables(view);

        if (!mBuildingId.isEmpty() && mSpaceId.isEmpty()) {
            Log.e("ALGO", "KLK");
            getDevicesByBuilding();
        } else if (mBuildingId.isEmpty() && !mSpaceId.isEmpty()) {
            Log.e("ALGO2", "KLK");
            getDevicesBySpace();
        }
        initListeners();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        mMainActivity.getSupportActionBar().setTitle("Devices");
    }

    private void iniVariables(View view) {
        deviceService = new DeviceService(Realm.getDefaultInstance());
        buildingService = new BuildingService(Realm.getDefaultInstance());
        spaceService = new SpaceService(Realm.getDefaultInstance());
        mDevicesList = new ArrayList<>();
        mGridView = (RecyclerView) view.findViewById(R.id.devices_grid);
        mAPI = new API();
        mAddDeviceButton = (FloatingActionButton) view.findViewById(R.id.add_device_button);
        mEmptyDeviceListLayout = (LinearLayout) view.findViewById(R.id.empty_device_list_layout);
    }

    private void initListeners() {
        mAddDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceCreateFragment deviceCreateFragment = new DeviceCreateFragment();
                Bundle bundle = new Bundle();
                bundle.putString(ArgumentsKeys.BUILDING_ID, mBuildingId);
                bundle.putString(ArgumentsKeys.SPACE_ID, mSpaceId);
                deviceCreateFragment.setArguments(bundle);
                Utils.loadContentFragment(getFragmentManager().findFragmentByTag(FragmentKeys.DEVICE_LIST_FRAGMENT), deviceCreateFragment, FragmentKeys.DEVICE_CREATION_FRAGMENT, true);
            }
        });

        mMainActivity.toggleDrawerIcon(false, R.drawable.ic_action_back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainActivity.onBackPressed();
            }
        });
    }

    private void getDevicesByBuilding() {
        mDevicesList = buildingService.getBuildingById(mBuildingId).getDevices();
        shouldEmptyMessageShow();
    }

    private void getDevicesBySpace() {
        mDevicesList = spaceService.getSpaceById(mSpaceId).getDevices();
       shouldEmptyMessageShow();
    }

    private void shouldEmptyMessageShow() {
        if (!mDevicesList.isEmpty()) {
            mEmptyDeviceListLayout.setVisibility(View.GONE);
            initDevicesList(mDevicesList);
        } else {
            mGridView.setVisibility(View.GONE);
            mEmptyDeviceListLayout.setVisibility(View.VISIBLE);
        }
    }

    private void initDevicesList(List<Device> devicesList) {
        mGridView.setLayoutManager(new GridLayoutManager(mActivity, 2));
        mDevicesListAdapter = new DeviceListAdapter(devicesList, getActivity(), this);
        mGridView.setAdapter(mDevicesListAdapter);
    }
}
