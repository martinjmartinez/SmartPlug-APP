package com.example.martinjmartinez.proyectofinal.UI.Statistics.ChartDetails;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.martinjmartinez.proyectofinal.Entities.Building;
import com.example.martinjmartinez.proyectofinal.Entities.Device;
import com.example.martinjmartinez.proyectofinal.Entities.Historial;
import com.example.martinjmartinez.proyectofinal.Entities.Space;
import com.example.martinjmartinez.proyectofinal.Models.HistorialReview;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Services.BuildingService;
import com.example.martinjmartinez.proyectofinal.Services.DeviceService;
import com.example.martinjmartinez.proyectofinal.UI.Devices.Adapters.DeviceSpinnerAdapter;
import com.example.martinjmartinez.proyectofinal.UI.Spaces.Adapters.SpaceSpinnerAdapter;
import com.example.martinjmartinez.proyectofinal.Utils.Chart.ChartUtils;
import com.example.martinjmartinez.proyectofinal.Utils.DateUtils;
import com.example.martinjmartinez.proyectofinal.Utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class DevicesChartDetailsFragment extends Fragment {

    private Date mStartDate;
    private Date mEndDate;
    private Building mBuilding;
    private String buildingId;
    private BuildingService buildingService;
    private DeviceService deviceService;
    private Realm realm;
    private TextView maxPower, minPower;
    private TextView maxDate, minDate;
    private TextView maxTime, minTime;
    private Spinner devicesSpinner;
    private DeviceSpinnerAdapter deviceSpinnerAdapter;
    private HistorialReview maxDay;
    private HistorialReview minDay;
    private Device device;
    private List<Device> devices;

    public static DevicesChartDetailsFragment newInstance(String buildingId, Date startDate, Date endDate) {
        Bundle args = new Bundle();
        args.putLong("startDate", startDate.getTime());
        args.putLong("endDate", endDate.getTime());
        args.putString("buildingId", buildingId);

        DevicesChartDetailsFragment fragment = new DevicesChartDetailsFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments() != null) {
                mStartDate = new Date(getArguments().getLong("startDate"));
                mEndDate = new Date(getArguments().getLong("endDate"));
                buildingId = getArguments().getString("buildingId");

                realm = Realm.getDefaultInstance();
                buildingService = new BuildingService(realm);
                deviceService = new DeviceService(realm);
                mBuilding = buildingService.getBuildingById(buildingId);
                devices = new ArrayList<>();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chart_details_fragment, container, false);

        maxDate = (TextView) view.findViewById(R.id.max_date);
        minDate = (TextView) view.findViewById(R.id.min_date);
        maxTime = (TextView) view.findViewById(R.id.max_time);
        minTime = (TextView) view.findViewById(R.id.min_time);
        maxPower = (TextView) view.findViewById(R.id.max_power);
        minPower = (TextView) view.findViewById(R.id.min_power);
        devicesSpinner = (Spinner) view.findViewById(R.id.itemsSpinner);

        initListenners();
        devicesSpinner.setVisibility(View.VISIBLE);
        devices = getDevices();

        if(!devices.isEmpty()) {
            device = devices.get(0);
            setUpDevicesSpinner(devices);
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fetchResults();

    }

    public void fetchResults() {
        if (device != null) {
            RealmResults<Historial> historials = realm.where(Historial.class).between("startDate", mStartDate, mEndDate).between("endDate", mStartDate, mEndDate).equalTo("device._id", device.get_id()).findAll().sort("startDate", Sort.ASCENDING);
            List<HistorialReview> results = ChartUtils.fetchDataDetails(historials);

            getMaxAndMinDays(results);
        }
    }
    public void initListenners() {
        devicesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                device = devices.get(position);
                fetchResults();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                device = devices.get(0);
            }
        });
    }

    public void getMaxAndMinDays(List<HistorialReview> results) {
        if (!results.isEmpty()) {

            maxDay = results.get(0);
            minDay = results.get(0);

            for (HistorialReview historialReview : results) {

                if (historialReview.getPowerAverage() < minDay.getPowerAverage())
                    minDay = historialReview;
                if (historialReview.getPowerAverage() > maxDay.getPowerAverage())
                    maxDay = historialReview;
            }

            maxDate.setText(maxDay.getDate());
            minDate.setText(minDay.getDate());
            maxPower.setText(Utils.decimalFormat.format(maxDay.getPowerAverage()) + " W");
            minPower.setText(Utils.decimalFormat.format(minDay.getPowerAverage()) + " W");
            maxTime.setText(DateUtils.timeFormatter(maxDay.getTotalTimeInSeconds()));
            minTime.setText(DateUtils.timeFormatter(minDay.getTotalTimeInSeconds()));
        }
    }

    public List<Device> getDevices() {
        List<Device> devicesInHistorials = new ArrayList<>();

        for(Device device: mBuilding.getDevices()) {
            RealmResults<Historial> historials = realm.where(Historial.class).between("startDate", mStartDate, mEndDate).between("endDate", mStartDate, mEndDate).equalTo("device._id", device.get_id()).findAll().sort("startDate", Sort.ASCENDING);

            if (!historials.isEmpty()) {
                devicesInHistorials.add(device);
            }

        }
        return devicesInHistorials;
    }

    public void setUpDevicesSpinner(List<Device> items) {
        if (items.size() != 0) {
            deviceSpinnerAdapter = new DeviceSpinnerAdapter(getContext(), R.layout.devices_item_spinner, items);
            devicesSpinner.setAdapter(deviceSpinnerAdapter);
        } else {
            devicesSpinner.setEnabled(false);
        }
    }
}
