package com.example.martinjmartinez.proyectofinal.UI.Home.Charts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.martinjmartinez.proyectofinal.Entities.Device;
import com.example.martinjmartinez.proyectofinal.Models.DeviceFB;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Services.DeviceService;
import com.example.martinjmartinez.proyectofinal.Utils.Chart.ChartUtils;
import com.example.martinjmartinez.proyectofinal.Utils.Constants;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class DevicesBarChartFragment extends Fragment {

    private Realm realm;
    private String buildingId, userId;
    private BarChart chart;
    private TextView title;
    private DeviceService deviceService;
    private DatabaseReference databaseReference;
    private List<Device> devices;
    private ChildEventListener devicesListener;

    public static DevicesBarChartFragment newInstance(String buildingId) {
        Bundle args = new Bundle();

        args.putString("buildingId", buildingId);

        DevicesBarChartFragment fragment = new DevicesBarChartFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            buildingId = getArguments().getString("buildingId");
            realm = Realm.getDefaultInstance();
            deviceService = new DeviceService(realm);
            userId = getActivity().getSharedPreferences(Constants.USER_INFO, 0).getString(Constants.USER_ID, FirebaseAuth.getInstance().getCurrentUser().getUid());
            databaseReference = FirebaseDatabase.getInstance().getReference("Accounts/"+ userId + "/Devices");
        }
    }

    private void initListeners() {
        devicesListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                DeviceFB deviceFB = dataSnapshot.getValue(DeviceFB.class);
                deviceService.updateDeviceLocal(deviceFB);
                fillChart();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                DeviceFB deviceFB = dataSnapshot.getValue(DeviceFB.class);
                deviceService.updateDeviceLocal(deviceFB);
                fillChart();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReference.addChildEventListener(devicesListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_chart_bar, container, false);

        chart = (BarChart) view.findViewById(R.id.chart);
        title = (TextView) view.findViewById(R.id.chart_title_home);

        chart.getDescription().setEnabled(false);
        devices = new ArrayList<>();

        initListeners();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        title.setText("Devices");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        databaseReference.removeEventListener(devicesListener);
    }

    public void fillChart() {
        final ArrayList<String> xVals = new ArrayList<>();
        ArrayList<BarEntry> yVals = new ArrayList<>();
        List<IBarDataSet> datasets = new ArrayList<>();
        int counter = 0;
        devices = deviceService.allActiveDevicesByBuilding(buildingId);

        if (devices != null) {
            for (Device device : devices) {
                xVals.add(device.getName());
                yVals.add(new BarEntry(counter, (float) device.getAverageConsumption()));
                datasets.add(new BarDataSet(yVals, device.getName()));
                counter++;
            }

            ChartUtils.makeBarChart(datasets, chart, xVals);
        }
    }
}
