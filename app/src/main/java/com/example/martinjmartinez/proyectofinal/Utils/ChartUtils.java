package com.example.martinjmartinez.proyectofinal.Utils;

import com.example.martinjmartinez.proyectofinal.Entities.Historial;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.realm.RealmResults;

public final class ChartUtils {

    public static void makeLineChart(LineChart chart, List<ILineDataSet> datasets, final Map<String, Integer> mapDates) {

        if (!datasets.isEmpty()) {
            for (int j = 0; j < datasets.size(); j++) {
                ((LineDataSet) datasets.get(j)).setColor(ColorTemplate.MATERIAL_COLORS[j]);
                ((LineDataSet) datasets.get(j)).setLineWidth(2.0f);
                datasets.get(j).setValueTextSize(10.0f);
            }

            LineData data = new LineData(datasets);
            chart.setData(data);

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

                public int getDecimalDigits() {
                    return 0;
                }
            };

            XAxis xAxis = chart.getXAxis();
            xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setValueFormatter(formatter);

            // refresh
            chart.invalidate();
        }
        chart.invalidate();
    }

    public static Map<String, Entry> fetchData(RealmResults<Historial> historials, Map<String, Integer> mapDates) {
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

    public static Map<String, Integer> sortDates(RealmResults<Historial> historials, Map<String, Integer> mapDates) {

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

    public static void makeBarChart(List<IBarDataSet> datasets, BarChart chart, final List<String> labels) {

        for (int j = 0; j < datasets.size(); j++) {
            ((BarDataSet) datasets.get(j)).setColors(ColorTemplate.MATERIAL_COLORS);
            datasets.get(j).setValueTextSize(10.0f);
        }

        BarData data = new BarData(datasets);

        chart.setData(data);
        chart.getLegend().setEnabled(false);
        chart.setFitBars(true); // make the x-axis fit exactly all bars

        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int intValue = (int) value;
                if (labels.size() > intValue && intValue >= 0) {

                    return labels.get(intValue);
                }
                return labels.get(intValue);
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
}
