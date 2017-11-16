package com.example.martinjmartinez.proyectofinal.UI.Statistics.Charts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.martinjmartinez.proyectofinal.Entities.Building;
import com.example.martinjmartinez.proyectofinal.Entities.Historial;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Services.BuildingService;
import com.example.martinjmartinez.proyectofinal.Services.DeviceService;
import com.example.martinjmartinez.proyectofinal.Services.HistorialService;
import com.example.martinjmartinez.proyectofinal.Utils.Chart.ChartUtils;
import com.example.martinjmartinez.proyectofinal.Utils.Constants;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;

import com.github.mikephil.charting.data.LineDataSet;

import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class BuildingsLineChartFragment extends Fragment{

    static private Date mStartDate;
    static private Date mEndDate;
    static private Building mBuilding;
    static private String buildingId;
    static private List<Building> buildings;
    private HistorialService historialService;
    private DeviceService deviceService;
    static private BuildingService buildingService;
    static private Realm realm;
    static private LineChart chart;
    private TextView title;
    static private boolean singleBuilding;
    private DatabaseReference databaseReference;
    private ValueEventListener listener;
    private String userId;

    public static BuildingsLineChartFragment newInstance(String buildingId, Date startDate, Date endDate, boolean singleBuilding) {
        Bundle args = new Bundle();

        args.putLong("startDate", startDate.getTime());
        args.putLong("endDate", endDate.getTime());
        args.putString("buildingId", buildingId);
        args.putBoolean("singleBuilding", singleBuilding);

        BuildingsLineChartFragment fragment = new BuildingsLineChartFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mStartDate = new Date(getArguments().getLong("startDate"));
            mEndDate = new Date(getArguments().getLong("endDate"));
            buildingId = getArguments().getString("buildingId");
            singleBuilding = getArguments().getBoolean("singleBuilding");
//            userId = getArguments().getString(Constants.USER_ID);
//            databaseReference = FirebaseDatabase.getInstance().getReference("Accounts/"+ userId + "/Histories");
            realm = Realm.getDefaultInstance();
            buildingService = new BuildingService(realm);
            historialService = new HistorialService(realm);
            deviceService = new DeviceService(realm);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_chart_line, container, false);

        chart =  view.findViewById(R.id.chart);
        title =  view.findViewById(R.id.chart_title_statistics);

        chart.getDescription().setEnabled(false);

        refreshData();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        title.setText("Buildings");
    }

    static public Map<String, Integer> getDates(List<Building> buildings, Map<String, Integer> dates) {
        if(singleBuilding){
            RealmResults<Historial> results = realm.where(Historial.class).equalTo("building._id", mBuilding.get_id()).between("startDate", mStartDate, mEndDate).between("lastLogDate", mStartDate, mEndDate).findAll().sort("startDate", Sort.ASCENDING);

            if (!results.isEmpty()) {
                dates = ChartUtils.sortDates(results, dates);
            }
        }else {
            for (Building building : buildings) {
                RealmResults<Historial> results = realm.where(Historial.class).equalTo("building._id", building.get_id()).between("startDate", mStartDate, mEndDate).between("lastLogDate", mStartDate, mEndDate).findAll().sort("startDate", Sort.ASCENDING);

                if (!results.isEmpty()) {
                    dates = ChartUtils.sortDates(results, dates);
                }
            }
        }

        return dates;
    }

    public static void refreshData() {
        if (singleBuilding) {
            mBuilding = buildingService.getBuildingById(buildingId);
            RealmResults<Historial> results = realm.where(Historial.class).equalTo("building._id", mBuilding.get_id()).between("startDate", mStartDate, mEndDate).between("lastLogDate", mStartDate, mEndDate).findAll().sort("startDate", Sort.ASCENDING);

            fillSingleBuildingChart(results, chart);
        } else {
            buildings = buildingService.allActiveBuildings();
            fillBuildingsChart();
        }
    }

    static public void fillBuildingsChart() {
        Log.e("fillBuildingsChart", "yolo");
        Map<String, Integer> dates = new TreeMap<>();
        Map<String, Entry> entriesResults;
        ArrayList<Entry> entries;
        List<ILineDataSet> dataSets = new ArrayList<>();

        dates = getDates(buildings, dates);

        for (Building building : buildings) {
            RealmResults<Historial> results = realm.where(Historial.class).equalTo("building._id", building.get_id()).between("startDate", mStartDate, mEndDate).between("lastLogDate", mStartDate, mEndDate).findAll().sort("startDate", Sort.ASCENDING);

            if (!results.isEmpty()) {
                entriesResults = ChartUtils.fetchConsumptionData(results, dates);
                entries = new ArrayList<>();

                entries.addAll(entriesResults.values());
                dataSets.add(new LineDataSet(entries, building.getName()));
            }
        }

        ChartUtils.makeLineChart(chart, dataSets, dates);
    }

    static public void fillSingleBuildingChart(RealmResults<Historial> historials, LineChart chart) {
        List<ILineDataSet> dataSets = new ArrayList<>();
        Map<String, Entry> results;
        ArrayList<Entry> entries;
        Map<String, Integer> dates = new TreeMap<>();

        chart.clear();

        dates = ChartUtils.sortDates(historials, dates);
        results = ChartUtils.fetchConsumptionData(historials, dates);
        entries = new ArrayList<>();
        entries.addAll(results.values());
        dataSets.add(new LineDataSet(entries, mBuilding.getName()));

        if (!results.isEmpty()) {
            ChartUtils.makeLineChart(chart, dataSets, dates);
            chart.invalidate();
        }
    }
}
