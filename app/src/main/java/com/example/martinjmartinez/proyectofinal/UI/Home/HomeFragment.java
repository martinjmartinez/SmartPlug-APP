package com.example.martinjmartinez.proyectofinal.UI.Home;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.martinjmartinez.proyectofinal.Entities.Building;
import com.example.martinjmartinez.proyectofinal.Entities.Device;
import com.example.martinjmartinez.proyectofinal.Entities.Space;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Services.BuildingService;
import com.example.martinjmartinez.proyectofinal.Services.DeviceService;
import com.example.martinjmartinez.proyectofinal.Services.HistorialService;
import com.example.martinjmartinez.proyectofinal.Services.SpaceService;
import com.example.martinjmartinez.proyectofinal.UI.Devices.Adapters.DeviceListAdapter;
import com.example.martinjmartinez.proyectofinal.UI.MainActivity.MainActivity;
import com.example.martinjmartinez.proyectofinal.Utils.API;
import com.example.martinjmartinez.proyectofinal.Utils.Constants;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

/**
 * Created by MartinJMartinez on 7/19/2017.
 */

public class HomeFragment extends Fragment {

    private String mBuildingId;
    private Building mBuilding;
    private RecyclerView mMostUsedDevicesRecycleView, mGraphsRecycleView;
    private List<Space> mSpacesList;
    private List<Device> mDeviceList;
    private List<Building> mChartList;
    private API mAPI;
    private Activity mActivity;
    private MainActivity mMainActivity;
    private BuildingService buildingService;
    private SpaceService spaceService;
    private DeviceService deviceService;
    private HistorialService historialService;

    public HomeFragment() {
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
    }

    @Override
    public void onResume() {
        super.onResume();

        mMainActivity.getSupportActionBar().setTitle("Home");
    }

    public void getArgumentsBundle() {
        Bundle bundle = this.getArguments();
        mBuildingId = bundle != null ? bundle.getString(Constants.BUILDING_ID, "") : "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);

        iniVariables(view);
        getBuilding();

        return view;
    }

    private void initListeners() {

    }

    private void iniVariables(View view) {
        spaceService = new SpaceService(Realm.getDefaultInstance());
        deviceService = new DeviceService(Realm.getDefaultInstance());
        historialService = new HistorialService(Realm.getDefaultInstance());
        buildingService = new BuildingService(Realm.getDefaultInstance());
        mDeviceList = new ArrayList<>();
        mSpacesList = new ArrayList<>();
        mChartList = new ArrayList<>();
        mAPI = new API();
        mMostUsedDevicesRecycleView = (RecyclerView) view.findViewById(R.id.most_used_devices);
        mGraphsRecycleView = (RecyclerView) view.findViewById(R.id.chart_list);
    }

    private void setAdapters(List<Device> devicesList, List<Building> charts) {
        ChartListAdapter chartListAdapter = new ChartListAdapter(charts, mActivity);
        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(mActivity, GridLayoutManager.HORIZONTAL, false);

        mGraphsRecycleView.setLayoutManager(mLayoutManager1);
        mGraphsRecycleView.setItemAnimator(new DefaultItemAnimator());
        mGraphsRecycleView.setAdapter(chartListAdapter);

        DeviceListAdapter deviceListAdapter = new DeviceListAdapter(devicesList, getActivity(), this);
        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(mActivity, GridLayoutManager.HORIZONTAL, false);

        mMostUsedDevicesRecycleView.setLayoutManager(mLayoutManager2);
        mMostUsedDevicesRecycleView.setItemAnimator(new DefaultItemAnimator());
        mMostUsedDevicesRecycleView.setAdapter(deviceListAdapter);


    }


    public void getBuilding() {
        mBuilding = buildingService.getBuildingById(mBuildingId);
        mChartList.clear();
        mChartList.add(mBuilding);
        mChartList.add(mBuilding);

        mDeviceList = mBuilding.getDevices();
        setAdapters(mDeviceList, mChartList);
    }
}
