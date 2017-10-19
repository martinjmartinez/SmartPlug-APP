package com.example.martinjmartinez.proyectofinal.UI.Statistics;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.martinjmartinez.proyectofinal.Entities.Building;
import com.example.martinjmartinez.proyectofinal.Entities.Device;
import com.example.martinjmartinez.proyectofinal.Entities.Historial;
import com.example.martinjmartinez.proyectofinal.Entities.Space;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Utils.Utils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;


public class StatisticsListAdapter extends RecyclerView.Adapter<StatisticsListAdapter.ViewHolder> {

    private List<Building> mBuildings;
    private Date mStartDate;
    private Date mEndDate;
    private Realm realm;
    private Map<String, Integer> mapDatesBuilding;
    private Map<String, Integer> mapDatesSpaces;
    private Map<String, Integer> mapDatesDevices;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        LineChart chart;

        ViewHolder(View view) {
            super(view);

            chart = (LineChart) view.findViewById(R.id.chart);
        }

    }

    public StatisticsListAdapter(List<Building> building, Date startDate, Date endDate) {
        this.mBuildings = building;
        this.mStartDate = startDate;
        this.mEndDate = endDate;
        realm = Realm.getDefaultInstance();

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chart_line, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        fillDevicesChart(holder, position);
    }

    @Override
    public int getItemCount() {
        return mBuildings.size();
    }


    public void fillDevicesChart(ViewHolder holder, int position) {


        RealmResults<Historial> historials;
        List<ILineDataSet> dataSets = new ArrayList<>();
        Map<String, Entry> results;
        ArrayList<Entry> entries;
        ArrayList<String> dates;


        switch (position) {
            case 0:
                holder.chart.clear();

                mapDatesBuilding = new TreeMap<>();
                historials = realm.where(Historial.class).between("startDate", mStartDate, mEndDate).between("endDate", mStartDate, mEndDate).equalTo("building._id", mBuildings.get(position).get_id()).findAll().sort("startDate", Sort.ASCENDING);
                mapDatesBuilding = sortDates(historials, mapDatesBuilding);
                results = fetchData(historials, mapDatesBuilding);
                entries = new ArrayList<>();
                entries.addAll(results.values());
                dataSets.add(new LineDataSet(entries, mBuildings.get(position).getName()));

                if (!results.isEmpty()) {
                    makeChart(holder, dataSets, mapDatesBuilding);
                    holder.chart.invalidate();
                }

                break;

            case 1:
                holder.chart.clear();

                mapDatesDevices = new TreeMap<>();
                RealmResults<Device> devices = mBuildings.get(position).getDevices();

                for (Device device : devices.sort("_id", Sort.ASCENDING)) {
                    historials = realm.where(Historial.class).between("startDate", mStartDate, mEndDate).between("endDate", mStartDate, mEndDate).equalTo("device._id", device.get_id()).findAll().sort("startDate", Sort.ASCENDING);
                    if (!historials.isEmpty()) {
                        mapDatesDevices = sortDates(historials, mapDatesDevices);
                    }
                }

                for (Device device : devices.sort("_id", Sort.ASCENDING)) {
                    historials = realm.where(Historial.class).between("startDate", mStartDate, mEndDate).between("endDate", mStartDate, mEndDate).equalTo("device._id", device.get_id()).findAll().sort("startDate", Sort.ASCENDING);
                    if (!historials.isEmpty()) {
                        results = fetchData(historials, mapDatesDevices);

                        entries = new ArrayList<>();
                        entries.addAll(results.values());
                        dataSets.add(new LineDataSet(entries, device.getName()));
                    }
                }

                makeChart(holder, dataSets, mapDatesDevices);
                break;

            case 2:
                holder.chart.clear();

                mapDatesSpaces = new TreeMap<>();
                RealmResults<Space> spaces = mBuildings.get(position).getSpaces();

                for (Space space : spaces.sort("_id", Sort.ASCENDING)) {
                    historials = realm.where(Historial.class).between("startDate", mStartDate, mEndDate).between("endDate", mStartDate, mEndDate).equalTo("space._id", space.get_id()).findAll().sort("startDate", Sort.ASCENDING);
                    if (!historials.isEmpty()) {
                        mapDatesSpaces = sortDates(historials, mapDatesSpaces);
                    }
                }

                for (Space space : spaces.sort("_id", Sort.ASCENDING)) {
                    historials = realm.where(Historial.class).between("startDate", mStartDate, mEndDate).between("endDate", mStartDate, mEndDate).equalTo("space._id", space.get_id()).findAll().sort("startDate", Sort.ASCENDING);
                    if (!historials.isEmpty()) {
                        mapDatesSpaces = sortDates(historials, mapDatesSpaces);
                        results = fetchData(historials, mapDatesSpaces);
                        entries = new ArrayList<>(results.values());
                        dataSets.add(new LineDataSet(entries, space.getName()));
                    }
                }

                makeChart(holder, dataSets, mapDatesSpaces);
                break;
        }
    }


    Map<String, Entry> fetchData(RealmResults<Historial> historials, Map<String, Integer> mapDates) {
        Map<String, Entry> entries = new TreeMap<>();

        if (!historials.isEmpty()) {
            int count = 0;
            int index;
            int i = 0;
            double sum = 0;

            Date lasteDate = historials.first().getStartDate();

            for (Historial historial : historials) {
                if (Utils.formatSimpleDate(lasteDate).contains(Utils.formatSimpleDate(historial.getStartDate()))) {
                    sum = sum + historial.getPowerAverage();
                    i++;
                    count++;

                    if (historials.size() == count) {
                        index = mapDates.get(Utils.formatSimpleDate(historial.getStartDate()));
                        entries.put(Utils.formatSimpleDate(historial.getStartDate()), new Entry(index, (float) sum / i));

                    }
                } else {

                    index = mapDates.get(Utils.formatSimpleDate(lasteDate));
                    entries.put(Utils.formatSimpleDate(lasteDate), new Entry(index, (float) sum / i));

                    lasteDate = historial.getStartDate();
                    count++;
                    if (historials.size() == count) {

                        index = mapDates.get(Utils.formatSimpleDate(historial.getStartDate()));

                        entries.put(Utils.formatSimpleDate(historial.getStartDate()), new Entry(index, (float) historial.getPowerAverage()));

                    } else {
                        sum = 0;
                        i = 0;
                        sum = sum + historial.getPowerAverage();
                        i++;
                    }
                }
            }
            return entries;
        }
        return entries;
    }

    Map<String, Integer> sortDates(RealmResults<Historial> historials, Map<String, Integer> mapDates) {

        if (!historials.isEmpty()) {
            int count = 0;
            int index;

            Date lasteDate = historials.first().getStartDate();

            for (Historial historial : historials) {
                if (Utils.formatSimpleDate(lasteDate).contains(Utils.formatSimpleDate(historial.getStartDate()))) {
                    count++;

                    if (historials.size() == count) {
                        if (!mapDates.containsKey(Utils.formatSimpleDate(historial.getStartDate()))) {
                            index = mapDates.size();
                            mapDates.put(Utils.formatSimpleDate(historial.getStartDate()), index);
                        }
                    }
                } else {
                    if (!mapDates.containsKey(Utils.formatSimpleDate(lasteDate))) {
                        index = mapDates.size();
                        mapDates.put(Utils.formatSimpleDate(lasteDate), index);

                    } else {
                        index = mapDates.get(Utils.formatSimpleDate(lasteDate));
                    }

                    lasteDate = historial.getStartDate();
                    count++;
                    if (historials.size() == count) {
                        if (!mapDates.containsKey(Utils.formatSimpleDate(historial.getStartDate()))) {
                            index = mapDates.size();
                            mapDates.put(Utils.formatSimpleDate(historial.getStartDate()), index);
                        }
                    }
                }
            }

            ArrayList<String> dates = new ArrayList<>(mapDates.keySet());
            Collections.sort(dates);

            Map<String, Integer> newMapDates = new TreeMap<>();
            for (int i = 0; i < mapDates.size(); i++) {
                newMapDates.put(dates.get(i), i);
            }
            return newMapDates;
        }
        return mapDates;
    }

    void makeChart(ViewHolder viewHolder, List<ILineDataSet> datasets, final Map<String, Integer> mapDates) {

        if (!datasets.isEmpty()) {

            for (int j = 0; j < datasets.size(); j++) {
                ((LineDataSet) datasets.get(j)).setColor(ColorTemplate.MATERIAL_COLORS[j]);
                ((LineDataSet) datasets.get(j)).setLineWidth(2.0f);
            }

            LineData data = new LineData(datasets);
            Log.e("Data", data.getDataSets().toString() + "   " + mapDates.toString() + " DATES" + mStartDate + "----" + mEndDate);
            viewHolder.chart.setData(data);

            IndexAxisValueFormatter formatter = new IndexAxisValueFormatter() {

                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    int intValue = (int) value;
                    String key = "";
                    if (mapDates.size() > intValue && intValue >= 0) {

                        for (Map.Entry item : mapDates.entrySet()) {
                            if (item.getValue().equals(intValue)) {
                                key = item.getKey().toString();
                                break; //breaking because its one to one map
                            }
                        }
                    }
                    return key;
                }

                // we don't draw numbers, so no decimal digits needed
                public int getDecimalDigits() {
                    return 0;
                }
            };

            XAxis xAxis = viewHolder.chart.getXAxis();
            xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setValueFormatter(formatter);

            // refresh
            viewHolder.chart.invalidate();
        }
        viewHolder.chart.invalidate();
    }
}
