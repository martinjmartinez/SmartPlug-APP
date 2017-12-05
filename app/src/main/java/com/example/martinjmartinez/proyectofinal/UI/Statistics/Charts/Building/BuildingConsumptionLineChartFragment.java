package com.example.martinjmartinez.proyectofinal.UI.Statistics.Charts.Building;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.martinjmartinez.proyectofinal.Entities.Building;
import com.example.martinjmartinez.proyectofinal.Entities.Historial;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Services.BuildingService;
import com.example.martinjmartinez.proyectofinal.Services.SpaceService;
import com.example.martinjmartinez.proyectofinal.Utils.Chart.ChartUtils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class BuildingConsumptionLineChartFragment extends Fragment {

    private Date mStartDate;
    private Date mEndDate;
    private String buildingId;
    private Realm realm;
    private Building mBuilding;
    private BuildingService buildingService;
    private LineChart chart;
    private TextView title;

    public static BuildingConsumptionLineChartFragment newInstance(String buildingId, Date startDate, Date endDate) {
        Bundle args = new Bundle();
        args.putLong("startDate", startDate.getTime());
        args.putLong("endDate", endDate.getTime());
        args.putString("buildingId", buildingId);

        BuildingConsumptionLineChartFragment fragment = new BuildingConsumptionLineChartFragment();
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

            realm = Realm.getDefaultInstance();
            buildingService = new BuildingService(realm);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_chart_line, container, false);

        chart =  view.findViewById(R.id.chart);
        title =  view.findViewById(R.id.chart_title_statistics);

        chart.getDescription().setEnabled(false);

        mBuilding = buildingService.getBuildingById(buildingId);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        title.setText("Power Consumption  (W/h)");
        fillChart();
    }

    public Map<String, Integer> getDates(Map<String, Integer> dates) {

        RealmResults<Historial> results = realm.where(Historial.class).equalTo("building._id", mBuilding.get_id()).between("startDate", mStartDate, mEndDate).between("lastLogDate", mStartDate, mEndDate).findAll().sort("startDate", Sort.ASCENDING);

        if (!results.isEmpty()) {
            dates = ChartUtils.sortDates(results, dates);
        }


        return dates;
    }

    public void fillChart() {
        Map<String, Integer> dates = new TreeMap<>();
        ArrayList<Entry> entries;
        List<ILineDataSet> dataSets = new ArrayList<>();

        dates = getDates(dates);

        RealmResults<Historial> results = realm.where(Historial.class).equalTo("building._id", mBuilding.get_id()).between("startDate", mStartDate, mEndDate).between("lastLogDate", mStartDate, mEndDate).findAll().sort("startDate", Sort.ASCENDING);

        if (!results.isEmpty()) {
            entries = ChartUtils.fetchConsumptionData(results, dates);
            dataSets.add(new LineDataSet(entries, mBuilding.getName()));
        }

        ChartUtils.makeLineChart(chart, dataSets, dates);
    }

}
