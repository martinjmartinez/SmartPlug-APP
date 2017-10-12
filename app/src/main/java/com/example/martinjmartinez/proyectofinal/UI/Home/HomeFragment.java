package com.example.martinjmartinez.proyectofinal.UI.Home;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.example.martinjmartinez.proyectofinal.Utils.Utils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.realm.Realm;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by MartinJMartinez on 7/19/2017.
 */

public class HomeFragment extends Fragment {

    private BarChart chart;
    private String mBuildingId;
    private Building mBuilding;
    private RecyclerView mGridView;
    private List<Space> mSpacesList;
    private List<Device> mDeviceList;
    private API mAPI;
    private Activity mActivity;
    private MainActivity mMainActivity;
    private BuildingService buildingService;
    private SpaceService spaceService;
    private DeviceService deviceService;

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
        mBuildingId = bundle != null ? bundle.getString(ArgumentsKeys.BUILDING_ID, "") : "";
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
        buildingService = new BuildingService(Realm.getDefaultInstance());
        chart = (BarChart) view.findViewById(R.id.chart);
        mDeviceList = new ArrayList<>();
        mSpacesList = new ArrayList<>();
        mAPI = new API();
        mGridView = (RecyclerView) view.findViewById(R.id.most_used_devices);


    }

    private void setAdapter(List<Device> devicesList) {
        DeviceListAdapter deviceListAdapter = new DeviceListAdapter(devicesList);
        RecyclerView.LayoutManager mLayoutmanager = new GridLayoutManager(mActivity, 3, GridLayoutManager.VERTICAL, false);
        mGridView.setLayoutManager(mLayoutmanager);
        mGridView.setItemAnimator(new DefaultItemAnimator());
        mGridView.setAdapter(deviceListAdapter);
    }

    private void generateFakeData() {

        Random r = new Random();
        final ArrayList<String> xVals = new ArrayList<>();
        ArrayList<BarEntry> yVals = new ArrayList<>();
        int counter = 0;

        for (Space space : mBuilding.getSpaces()) {
            spaceService.updateSpacePower(space.get_id(), 651.2);
            xVals.add(space.getName());
            yVals.add(new BarEntry(counter, ((float) space.getPower())));

            counter++;
        }

        BarDataSet newSet = new BarDataSet(yVals, "Spaces");
        newSet.setColors(new int[]{R.color.alert, R.color.colorAccent, R.color.color1, R.color.color2, R.color.color3}, getContext());
        BarData data = new BarData(newSet);
        data.setBarWidth(0.5f); // set custom bar width
        chart.setData(data);
        chart.setFitBars(true); // make the x-axis fit exactly all bars

        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xVals.get((int) value);
            }

            // we don't draw numbers, so no decimal digits needed
            public int getDecimalDigits() {
                return 0;
            }
        };

        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(formatter);
        chart.invalidate(); // refresh
    }

    private void getBuilding() {
        mBuilding = buildingService.getBuildingById(mBuildingId);
        if (!mBuilding.getSpaces().isEmpty()) {
            generateFakeData();
            //TODO get devices with more measures
        }

        mDeviceList = mBuilding.getDevices();
        setAdapter(mDeviceList);
    }
}
