package com.example.martinjmartinez.proyectofinal.UI.Statistics.Charts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.martinjmartinez.proyectofinal.Entities.Building;
import com.example.martinjmartinez.proyectofinal.Entities.Device;
import com.example.martinjmartinez.proyectofinal.Entities.Historial;
import com.example.martinjmartinez.proyectofinal.Entities.Space;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Services.BuildingService;
import com.example.martinjmartinez.proyectofinal.Utils.ChartUtils;
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

public class SpacesChartFragment extends Fragment {

    private Date mStartDate;
    private Date mEndDate;
    private Building mBuilding;
    private String buildingId;
    private BuildingService buildingService;
    private Realm realm;
    private LineChart chart;
    private TextView title;

    public static SpacesChartFragment newInstance(String buildingId, Date startDate, Date endDate) {
        Bundle args = new Bundle();
        args.putLong("startDate", startDate.getTime());
        args.putLong("endDate", endDate.getTime());
        args.putString("buildingId", buildingId);

        SpacesChartFragment fragment = new SpacesChartFragment();
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
            mBuilding = buildingService.getBuildingById(buildingId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_chart_line, container, false);

        chart = (LineChart) view.findViewById(R.id.chart);
        title = (TextView) view.findViewById(R.id.chart_title_statistics);

        chart.getDescription().setEnabled(false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        title.setText("Spaces");
        fillChart();
    }

    public Map<String, Integer> getDates(List<Space> spaces, Map<String, Integer> dates) {
        for (Space space : spaces) {
            RealmResults<Historial> results = realm.where(Historial.class).between("startDate", mStartDate, mEndDate).between("endDate", mStartDate, mEndDate).equalTo("space._id", space.get_id()).findAll().sort("startDate", Sort.ASCENDING);

            if (!results.isEmpty()) {
                dates = ChartUtils.sortDates(results, dates);
            }
        }

        return dates;
    }

    public void fillChart() {
        Map<String, Integer> dates = new TreeMap<>();
        List<Space> spaces = mBuilding.getSpaces().sort("_id", Sort.ASCENDING);
        Map<String, Entry> entriesResults;
        ArrayList<Entry> entries;
        List<ILineDataSet> dataSets = new ArrayList<>();

        dates = getDates(spaces,dates);

        for (Space space : spaces) {
            RealmResults<Historial> results = realm.where(Historial.class).between("startDate", mStartDate, mEndDate).between("endDate", mStartDate, mEndDate).equalTo("space._id", space.get_id()).findAll().sort("startDate", Sort.ASCENDING);

            if (!results.isEmpty()) {
                entriesResults = ChartUtils.fetchData(results, dates);
                entries = new ArrayList<>();

                entries.addAll(entriesResults.values());
                dataSets.add(new LineDataSet(entries, space.getName()));
            }
        }

        ChartUtils.makeChart(chart, dataSets, dates);
    }
}
