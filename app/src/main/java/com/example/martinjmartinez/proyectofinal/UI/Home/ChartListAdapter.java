package com.example.martinjmartinez.proyectofinal.UI.Home;

import android.app.Activity;
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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;


import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

import static java.security.AccessController.getContext;

public class ChartListAdapter extends RecyclerView.Adapter<ChartListAdapter.ViewHolder> {

    private List<Building> buildings;
    private Activity activity;
    private BuildingService buildingService;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        BarChart chart;

        ViewHolder(View view) {
            super(view);

            chart = (BarChart) view.findViewById(R.id.chart);
        }
    }

    public ChartListAdapter(List<Building> buildingList, Activity activity) {
        this.buildings = buildingList;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_graph_list, parent, false);

        buildingService = new BuildingService(Realm.getDefaultInstance());

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Building building = buildingService.getBuildingById(buildings.get(position).get_id());
        // refreshPower(device, holder);

        fillDevicesChart(building, holder, position);
    }

    @Override
    public int getItemCount() {
        return buildings.size();
    }


    public void fillDevicesChart(Building building, ViewHolder holder, int position) {

        final ArrayList<String> xVals = new ArrayList<>();
        ArrayList<BarEntry> yVals = new ArrayList<>();
        int counter = 0;
        String chartName = "";
        if (position ==0) {
            chartName = "Devices";
            if (building.getDevices() != null) {
                for (Device device : building.getDevices()) {
                    xVals.add(device.getName());
                    yVals.add(new BarEntry(counter, (float) device.getAverageConsumption()));
                    Log.e("AVERAGE", device.getAverageConsumption() + "");
                    counter++;
                }
            }
        } else {
            chartName = "Spaces";
            if (building.getSpaces() != null) {
                for (Space space : building.getSpaces()) {
                    xVals.add(space.getName());
                    yVals.add(new BarEntry(counter, (float) space.getAverageConsumption()));
                    Log.e("AVERAGE", space.getAverageConsumption() + "");
                    counter++;
                }
            }
        }

        BarDataSet newSet = new BarDataSet(yVals, chartName);
        newSet.setColors(new int[]{R.color.alert, R.color.colorAccent, R.color.color1, R.color.color2, R.color.color3}, activity);
        BarData data = new BarData(newSet);
        data.setBarWidth(0.5f); // set custom bar width
        holder.chart.setData(data);
        holder.chart.setFitBars(true); // make the x-axis fit exactly all bars

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

        XAxis xAxis = holder.chart.getXAxis();
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(formatter);
        holder.chart.invalidate(); // refresh
    }

}
