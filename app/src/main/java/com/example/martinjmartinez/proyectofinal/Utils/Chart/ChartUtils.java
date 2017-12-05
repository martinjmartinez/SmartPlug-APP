package com.example.martinjmartinez.proyectofinal.Utils.Chart;

import android.util.Log;

import com.example.martinjmartinez.proyectofinal.Entities.Historial;
import com.example.martinjmartinez.proyectofinal.Models.HistorialReview;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Utils.DateUtils;
import com.example.martinjmartinez.proyectofinal.Utils.Utils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.realm.RealmResults;

import static com.github.mikephil.charting.utils.ColorTemplate.rgb;

public final class ChartUtils {

    public static void makeLineChart(LineChart chart, List<ILineDataSet> datasets, final Map<String, Integer> mapDates) {

        if (!datasets.isEmpty()) {
            for (int j = 0; j < datasets.size(); j++) {
                Log.e("Dataset", datasets.get(j).getLabel() + "   " + datasets.get(j).toString());
                ((LineDataSet) datasets.get(j)).setColor(ColorTemplate.MATERIAL_COLORS[j % 4]);
                ((LineDataSet) datasets.get(j)).setLineWidth(2.0f);
                datasets.get(j).setValueTextSize(10.0f);
            }

            LineData data = new LineData(datasets);
            chart.setData(data);
            chart.setXAxisRenderer(new CustomXAxisRenderer(chart.getViewPortHandler(), chart.getXAxis(), chart.getTransformer(YAxis.AxisDependency.LEFT)));
            IndexAxisValueFormatter formatter = new IndexAxisValueFormatter() {

                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    int intValue = (int) value;
                    String key = "";
                    if (mapDates.size() > intValue && intValue >= 0) {

                        for (Map.Entry item : mapDates.entrySet()) {
                            if (item.getValue().equals(intValue)) {
                                key = item.getKey().toString();
                                key = DateUtils.multiLineMediumFormat(key);
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
            Log.e("finish", "making chartLine");
        }
        chart.invalidate();
    }

    public static ArrayList<Entry> fetchConsumptionData(RealmResults<Historial> historials, Map<String, Integer> mapDates) {
        ArrayList<Entry> entries = new ArrayList<>();

        if (!historials.isEmpty()) {
            int count = 0;
            int index;
            double sum = 0;

            Date lasteDate = historials.first().getStartDate();

            for (Historial historial : historials) {
                if (Utils.formatSimpleDate(lasteDate).contains(Utils.formatSimpleDate(historial.getStartDate()))) {
                    sum = sum + historial.getPowerConsumed();
                    count++;

                    if (historials.size() == count) {
                        index = mapDates.get(Utils.formatSimpleDate(historial.getStartDate()));
                        entries.add(new Entry(index, (float) sum));

                    }
                } else {

                    index = mapDates.get(Utils.formatSimpleDate(lasteDate));
                    entries.add(new Entry(index, (float) sum));


                    lasteDate = historial.getStartDate();
                    count++;
                    if (historials.size() == count) {

                        index = mapDates.get(Utils.formatSimpleDate(historial.getStartDate()));
                        entries.add(new Entry(index, (float) historial.getPowerConsumed()));


                    } else {
                        sum = 0;
                        sum = sum + historial.getPowerConsumed();
                    }
                }
            }

            return entries;
        }
        return entries;
    }

    public static  ArrayList<Entry> fetchPowerData(RealmResults<Historial> historials, Map<String, Integer> mapDates) {
        ArrayList<Entry> entries = new ArrayList<>();

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
                        entries.add(new Entry(index, (float) sum / i));

                    }
                } else {

                    index = mapDates.get(Utils.formatSimpleDate(lasteDate));
                    entries.add(new Entry(index, (float) sum / i));

                    lasteDate = historial.getStartDate();
                    count++;
                    if (historials.size() == count) {

                        index = mapDates.get(Utils.formatSimpleDate(historial.getStartDate()));

                        entries.add(new Entry(index, (float) historial.getPowerAverage()));

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

    public static ArrayList<Entry> fetchTimeData(RealmResults<Historial> historials, Map<String, Integer> mapDates) {
        ArrayList<Entry> entries = new ArrayList<>();

        if (!historials.isEmpty()) {
            int count = 0;
            int index;
            int i = 0;
            double sum = 0;

            Date lasteDate = historials.first().getStartDate();

            for (Historial historial : historials) {
                if (Utils.formatSimpleDate(lasteDate).contains(Utils.formatSimpleDate(historial.getStartDate()))) {
                    sum = sum + historial.getTotalTimeInSeconds();
                    i++;
                    count++;

                    if (historials.size() == count) {
                        index = mapDates.get(Utils.formatSimpleDate(historial.getStartDate()));
                        entries.add(new Entry(index, (float) sum / 60));

                    }
                } else {

                    index = mapDates.get(Utils.formatSimpleDate(lasteDate));
                    entries.add(new Entry(index, (float) sum / 60));

                    lasteDate = historial.getStartDate();
                    count++;
                    if (historials.size() == count) {

                        index = mapDates.get(Utils.formatSimpleDate(historial.getStartDate()));

                        entries.add(new Entry(index, (float) historial.getTotalTimeInSeconds()));

                    } else {
                        sum = 0;
                        i = 0;
                        sum = sum + historial.getTotalTimeInSeconds();
                        i++;
                    }
                }
            }
            return entries;
        }
        return entries;
    }

    public static List<HistorialReview> fetchDataDetails(RealmResults<Historial> historials) {
        List<HistorialReview> resutls = new ArrayList<>();

        if (!historials.isEmpty()) {
            int count = 0;
            double time = 0;
            int i = 0;
            double sum = 0;
            double sum2 = 0;
            Date lasteDate = historials.first().getStartDate();

            for (Historial historial : historials) {
                if (Utils.formatSimpleDate(lasteDate).contains(Utils.formatSimpleDate(historial.getStartDate()))) {
                    sum = sum + historial.getPowerConsumed();
                    sum2 = sum2 + historial.getPowerAverage();
                    time = (long) (time + historial.getTotalTimeInSeconds());
                    i++;
                    count++;

                    if (historials.size() == count) {
                        resutls.add(new HistorialReview(Utils.formatSimpleDate(historial.getStartDate()), sum, time, sum2 / i));

                    }
                } else {
                    resutls.add(new HistorialReview(Utils.formatSimpleDate(lasteDate), sum, time, sum2 / i));

                    lasteDate = historial.getStartDate();
                    count++;
                    if (historials.size() == count) {
                        resutls.add(new HistorialReview(Utils.formatSimpleDate(historial.getStartDate()), historial.getPowerConsumed(), historial.getTotalTimeInSeconds(), historial.getPowerAverage()));

                    } else {
                        sum = 0;
                        sum2 = 0;
                        time = 0;
                        i = 0;
                        sum = sum + historial.getPowerConsumed();
                        sum2 = sum2 + historial.getPowerAverage();
                        time = time + historial.getTotalTimeInSeconds();
                        i++;
                    }
                }
            }
            return resutls;
        }
        return resutls;
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
            ArrayList<Long> dateInMillis = new ArrayList<>();
            for (String date : dates) {

                dateInMillis.add(DateUtils.fromStringToMillis(date));
            }
            Collections.sort(dateInMillis);

            Map<String, Integer> newMapDates = new TreeMap<>();
            for (int i = 0; i < mapDates.size(); i++) {
                newMapDates.put(Utils.formatSimpleDate(new Date(dateInMillis.get(i))), i);

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
                return null;
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

    public static void makeGroupBarChart(List<Double> limits, List<Double> consumed , BarChart chart, final List<String> labels) {

        List<BarEntry> limitsEntries = new ArrayList<>();
        List<BarEntry> consumedEntries = new ArrayList<>();


        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);

        // fill the lists
        for(int i = 0; i < limits.size(); i++) {
            limitsEntries.add(new BarEntry(i, limits.get(i).intValue()));
            consumedEntries.add(new BarEntry(i, consumed.get(i).intValue()));
        }

        float groupSpace = 0.4f;
        float barSpace = 0f; // x2 dataset
        float barWidth = 0.3f; // x2 dataset
// (0.02 + 0.45) * 2 + 0.06 = 1.00 -> interval per "group"

        BarDataSet set1 = new BarDataSet(limitsEntries, "Limits");
        set1.setColor(rgb("#2aa9ff"));
        BarDataSet set2 = new BarDataSet(consumedEntries, "Consumed");
        set2.setColor(rgb("#128fe4"));

        BarData data = new BarData(set1, set2);

        chart.setData(data);
        chart.getBarData().setBarWidth(barWidth);
        chart.getXAxis().setAxisMinimum(0);
        chart.getXAxis().setAxisMaximum(0 + chart.getBarData().getGroupWidth(groupSpace,barSpace) * limitsEntries.size());
        chart.groupBars(0, groupSpace, barSpace); // perform the "explicit" grouping

        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int intValue = (int) value;
                if (labels.size() > intValue && intValue >= 0) {
                    return labels.get(intValue);
                }
                return null;
            }

            // we don't draw numbers, so no decimal digits needed
            public int getDecimalDigits() {
                return 0;
            }
        };

        XAxis xAxis = chart.getXAxis();

        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setGranularityEnabled(true);
        xAxis.setCenterAxisLabels(true);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMaximum(limitsEntries.size());
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(formatter);

        chart.invalidate(); // refresh
    }
}
