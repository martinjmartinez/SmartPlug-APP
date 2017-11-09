package com.example.martinjmartinez.proyectofinal.UI.Home;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.martinjmartinez.proyectofinal.Entities.Device;;
import com.example.martinjmartinez.proyectofinal.Models.DeviceFB;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Services.BuildingService;
import com.example.martinjmartinez.proyectofinal.Services.DeviceService;
import com.example.martinjmartinez.proyectofinal.Services.HistorialService;
import com.example.martinjmartinez.proyectofinal.Services.SpaceService;
import com.example.martinjmartinez.proyectofinal.UI.Devices.Adapters.DeviceListAdapter;
import com.example.martinjmartinez.proyectofinal.UI.MainActivity.MainActivity;
import com.example.martinjmartinez.proyectofinal.Utils.Constants;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import io.realm.Realm;

public class HomeFragment extends Fragment {

    private String mBuildingId;
    private RecyclerView mMostUsedDevicesRecycleView;
    private ViewPager chartsViewPager;
    private HomeChartViewPagerAdapter homeChartViewPagerAdapter;
    private Activity mActivity;
    private MainActivity mMainActivity;
    private BuildingService buildingService;
    private TabLayout tabLayout;
    private SpaceService spaceService;
    private DeviceService deviceService;
    private HistorialService historialService;
    private Realm realm;
    private DatabaseReference databaseReference;
    private DeviceListAdapter deviceListAdapter;
    private ValueEventListener devicesListener;

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
        initListeners();
        getBuilding();

        return view;
    }

    private void initListeners() {
        devicesListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    DeviceFB deviceFB = dataSnapshot1.getValue(DeviceFB.class);

                    deviceService.updateOrCreate(deviceFB);
                }
                deviceListAdapter.notifyDataSetChanged();

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

    private void iniVariables(View view) {
        realm = Realm.getDefaultInstance();
        spaceService = new SpaceService(realm);
        deviceService = new DeviceService(realm);
        historialService = new HistorialService(realm);
        buildingService = new BuildingService(realm);
        databaseReference = FirebaseDatabase.getInstance().getReference("Devices");
        mMostUsedDevicesRecycleView = (RecyclerView) view.findViewById(R.id.most_used_devices);
        chartsViewPager = (ViewPager) view.findViewById(R.id.bar_chart_pager);
        tabLayout = (TabLayout) view.findViewById(R.id.tabDots);
    }

    private void setAdapters(List<Device> devicesList) {
        refreshChart();

        deviceListAdapter = new DeviceListAdapter(devicesList, getActivity(), this);
        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(mActivity, GridLayoutManager.HORIZONTAL, false);

        mMostUsedDevicesRecycleView.setLayoutManager(mLayoutManager2);
        mMostUsedDevicesRecycleView.setItemAnimator(new DefaultItemAnimator());
        mMostUsedDevicesRecycleView.setAdapter(deviceListAdapter);
    }

    public void getBuilding() {
        setAdapters(deviceService.allActiveDevicesByBuilding(mBuildingId));
    }

    public void refreshChart() {
        setupViewPager(chartsViewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        homeChartViewPagerAdapter = new HomeChartViewPagerAdapter(getChildFragmentManager(), getContext(), mBuildingId, false);

        tabLayout.setupWithViewPager(viewPager, true);
        viewPager.setAdapter(homeChartViewPagerAdapter);
    }
}
