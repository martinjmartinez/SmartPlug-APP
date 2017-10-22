package com.example.martinjmartinez.proyectofinal.UI.Home.Charts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.martinjmartinez.proyectofinal.Entities.Building;
import com.example.martinjmartinez.proyectofinal.Entities.Space;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Services.BuildingService;
import com.example.martinjmartinez.proyectofinal.Utils.ChartUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class SpacesBarChartFragment extends Fragment {

    private Realm realm;
    private String buildingId;
    private BuildingService buildingService;
    private Building mBuilding;
    private BarChart chart;
    private TextView title;

    public static SpacesBarChartFragment newInstance(String buildingId) {
        Bundle args = new Bundle();

        args.putString("buildingId", buildingId);

        SpacesBarChartFragment fragment = new SpacesBarChartFragment();
        fragment.setArguments(args);

        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            buildingId = getArguments().getString("buildingId");

            realm = Realm.getDefaultInstance();
            buildingService = new BuildingService(realm);
            mBuilding = buildingService.getBuildingById(buildingId);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_chart_bar, container, false);

        chart = (BarChart) view.findViewById(R.id.chart);
        title = (TextView) view.findViewById(R.id.chart_title_home);

        chart.getDescription().setEnabled(false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        title.setText("Spaces");
        fillChart();
    }

    public void fillChart() {
        final ArrayList<String> xVals = new ArrayList<>();
        ArrayList<BarEntry> yVals = new ArrayList<>();
        List<IBarDataSet> datasets = new ArrayList<>();
        int counter = 0;

        if (mBuilding.getSpaces() != null) {
            for (Space space : mBuilding.getSpaces()) {
                xVals.add(space.getName());
                yVals.add(new BarEntry(counter, (float) space.getAverageConsumption()));
                datasets.add(new BarDataSet(yVals, space.getName()));
                counter++;
            }

            ChartUtils.makeBarChart(datasets,chart,xVals);
        }
    }
}