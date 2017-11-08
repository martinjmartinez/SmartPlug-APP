package com.example.martinjmartinez.proyectofinal.UI.Statistics.Charts.Device;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.martinjmartinez.proyectofinal.Entities.Device;
import com.example.martinjmartinez.proyectofinal.Entities.Historial;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Services.DeviceService;
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

/**
 * Created by MartinJMartinez on 10/29/2017.
 */

public class DeviceTimeLinechartFragment extends Fragment {

    private Date mStartDate;
    private Date mEndDate;
    private String deviceId;
    private Realm realm;
    private Device mDevice;
    private DeviceService deviceService;
    private LineChart chart;
    private TextView title;

    public static DeviceTimeLinechartFragment newInstance(String deviceId, Date startDate, Date endDate) {
        Bundle args = new Bundle();
        args.putLong("startDate", startDate.getTime());
        args.putLong("endDate", endDate.getTime());
        args.putString("deviceId", deviceId);

        DeviceTimeLinechartFragment fragment = new DeviceTimeLinechartFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            mStartDate = new Date(getArguments().getLong("startDate"));
            mEndDate = new Date(getArguments().getLong("endDate"));
            deviceId = getArguments().getString("deviceId");

            realm = Realm.getDefaultInstance();
            deviceService = new DeviceService(realm);
            mDevice = deviceService.getDeviceById(deviceId);
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

        title.setText("Time On (Min)");
        fillChart();
    }

    public Map<String, Integer> getDates(Map<String, Integer> dates) {

        RealmResults<Historial> results = realm.where(Historial.class).equalTo("device._id", mDevice.get_id()).between("startDate", mStartDate, mEndDate).between("lastLogDate", mStartDate, mEndDate).findAll().sort("startDate", Sort.ASCENDING);

        if (!results.isEmpty()) {
            dates = ChartUtils.sortDates(results, dates);
        }


        return dates;
    }

    public void fillChart() {
        Map<String, Integer> dates = new TreeMap<>();
        Map<String, Entry> entriesResults;
        ArrayList<Entry> entries;
        List<ILineDataSet> dataSets = new ArrayList<>();

        dates = getDates(dates);

        RealmResults<Historial> results = realm.where(Historial.class).equalTo("device._id", mDevice.get_id()).between("startDate", mStartDate, mEndDate).between("lastLogDate", mStartDate, mEndDate).findAll().sort("startDate", Sort.ASCENDING);

        if (!results.isEmpty()) {
            entriesResults = ChartUtils.fetchTimeData(results, dates);
            entries = new ArrayList<>();

            entries.addAll(entriesResults.values());
            dataSets.add(new LineDataSet(entries, mDevice.getName()));
        }

        ChartUtils.makeLineChart(chart, dataSets, dates);
    }

}