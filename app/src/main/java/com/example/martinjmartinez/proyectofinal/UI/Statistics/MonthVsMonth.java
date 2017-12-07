package com.example.martinjmartinez.proyectofinal.UI.Statistics;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.martinjmartinez.proyectofinal.Entities.Historial;
import com.example.martinjmartinez.proyectofinal.Models.HistorialReview;
import com.example.martinjmartinez.proyectofinal.Models.DevicesMonthConsumed;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Services.HistorialService;
import com.example.martinjmartinez.proyectofinal.Utils.Chart.ChartUtils;
import com.example.martinjmartinez.proyectofinal.Utils.DateUtils;
import com.example.martinjmartinez.proyectofinal.Utils.Utils;
import com.github.lzyzsd.circleprogress.ArcProgress;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class MonthVsMonth extends Fragment {
    private String monthId, objectId, type;
    private TextView lastMonth;
    private TextView actualMonth;
    private ArcProgress chart1, chart2, chart3;
    private double prevLimitValue, actualLimitValur, prevConsuptionValue, actualConsumtionValue, prevAvgPower, actualAvgPower, prevCostValue, actualCostValue;
    private TextView previusCost, currentCost, previousLimit, actualLimit, previousConsumtion, actualConsumption, prevPower, actualPower, prevTime, actualTime;
    private ValueEventListener monthLimitListener;
    private HistorialService historialService;
    private LinearLayout timeThisMonthLayout, timeLastMonthLayout;
    private Realm realm;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private DatabaseReference databaseReference;


    public static MonthVsMonth newInstance(String monthId, String objectId, String type) {
        Bundle args = new Bundle();

        args.putString("objectId", objectId);
        args.putString("monthId", monthId);
        args.putString("type", type);

        MonthVsMonth fragment = new MonthVsMonth();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onPause() {
        super.onPause();
        databaseReference.removeEventListener(monthLimitListener);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            objectId = getArguments().getString("objectId");
            monthId = getArguments().getString("monthId");
            type = getArguments().getString("type");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.month_compare_fragment, container, false);

         mAuth = FirebaseAuth.getInstance();
         currentUser = mAuth.getCurrentUser();
        realm = Realm.getDefaultInstance();
        historialService = new HistorialService(realm);
        previusCost = view.findViewById(R.id.prev_cost);
        currentCost = view.findViewById(R.id.actual_cost);
        timeLastMonthLayout = view.findViewById(R.id.timeOn_last_month);
        timeThisMonthLayout = view.findViewById(R.id.timeOn_this_month);
        previousConsumtion = view.findViewById(R.id.prev_consumed);
        previousLimit = view.findViewById(R.id.prev_limit);
        prevTime = view.findViewById(R.id.prev_time);
        actualTime = view.findViewById(R.id.actual_time);
        prevPower = view.findViewById(R.id.prev_power);
        actualPower = view.findViewById(R.id.actual_power);
        actualConsumption = view.findViewById(R.id.actual_consumed);
        actualLimit = view.findViewById(R.id.actual_limit);
        actualMonth = view.findViewById(R.id.actual_month);
        chart1 = view.findViewById(R.id.chart1);
        chart2 = view.findViewById(R.id.chart2);
        chart3 = view.findViewById(R.id.chart3);
        lastMonth = view.findViewById(R.id.prev_month);

        if(type.equals("Device")) {
            databaseReference = FirebaseDatabase.getInstance().getReference("Accounts/" + currentUser.getUid() + "/MonthlyConsumed/" + objectId);
            timeThisMonthLayout.setVisibility(View.VISIBLE);
            timeLastMonthLayout.setVisibility(View.VISIBLE);
        } else if (type.equals("Space")) {
            databaseReference = FirebaseDatabase.getInstance().getReference("Accounts/" + currentUser.getUid() + "/SpacesMonthlyConsumed/" + objectId);
            timeThisMonthLayout.setVisibility(View.GONE);
            timeLastMonthLayout.setVisibility(View.GONE);
        } else if (type.equals("Building")) {
            databaseReference = FirebaseDatabase.getInstance().getReference("Accounts/" + currentUser.getUid() + "/BuildingMonthlyConsumed/" + objectId);
            timeThisMonthLayout.setVisibility(View.GONE);
            timeLastMonthLayout.setVisibility(View.GONE);
        }


        initListeners();

        fetchResults();
        return view;
    }


    public void fetchResults() {

        RealmResults<Historial> currentMonthDate = realm.where(Historial.class).equalTo(type.toLowerCase() +"._id", objectId).between("startDate", Utils.firstDayOfCurrentMonth(), DateUtils.getCurrentDate()).between("lastLogDate", Utils.firstDayOfCurrentMonth(), DateUtils.getCurrentDate()).findAll().sort("startDate", Sort.ASCENDING);
        List<HistorialReview> resultsCurrentMonth = ChartUtils.fetchDataDetails(currentMonthDate);

        RealmResults<Historial> prevMonthDate = realm.where(Historial.class).equalTo(type.toLowerCase()+"._id", objectId).between("startDate", Utils.firstDayOfPreviousMonth(), Utils.lastDayOfPreviousMonth()).between("lastLogDate", Utils.firstDayOfPreviousMonth(), Utils.lastDayOfPreviousMonth()).findAll().sort("startDate", Sort.ASCENDING);
        List<HistorialReview> resultsPrevMonth = ChartUtils.fetchDataDetails(prevMonthDate);

        getDataFromHistory(resultsPrevMonth, resultsCurrentMonth);

    }

    public void getDataFromHistory(List<HistorialReview> results1, List<HistorialReview> results2) {
        double prevTotalTime = 0;
        double actualTotalTime = 0;
        double prevPower1 = 0;
        double actualPower1 = 0;
        boolean hasPrevMonth = false;

        if (!results1.isEmpty()) {
            hasPrevMonth = true;
            for (HistorialReview historialReview : results1) {
                prevTotalTime = prevTotalTime + historialReview.getTotalTimeInSeconds();
                prevPower1 = prevPower1 + historialReview.getAveragePower();
            }

            prevAvgPower = prevPower1 / results1.size();
            prevTime.setText(DateUtils.timeFormatter(prevTotalTime));
            prevPower.setText(Utils.decimalFormat.format(prevAvgPower) + " W");

        }

        if (!results2.isEmpty()) {
            for (HistorialReview historialReview : results2) {
                actualTotalTime = actualTotalTime + historialReview.getTotalTimeInSeconds();
                actualPower1 = actualPower1 + historialReview.getAveragePower();
            }

            actualAvgPower = actualPower1 / results2.size();
            actualTime.setText(DateUtils.timeFormatter(actualTotalTime));
            actualPower.setText(Utils.decimalFormat.format(actualAvgPower) + " W");
        }

        //todo what if both are empty?
        if (!hasPrevMonth) {
            prevAvgPower = 0;
            prevPower.setText(0 + "W");
            prevLimitValue = 0;
            prevTime.setText("00:00" + " H");
            previusCost.setText("$0.0");
            chart1.setMax(100);
            chart1.setProgress(0);
            chart1.setBottomText("No Data");
        }


        Double monthsDifferences = (actualAvgPower - prevAvgPower);
        if (monthsDifferences < 0) {
            chart1.setFinishedStrokeColor(getResources().getColor(R.color.color3));
            chart1.setTextColor(getResources().getColor(R.color.color3));
            chart1.setBottomText("Under");
            monthsDifferences = monthsDifferences * -1;
        } else if (monthsDifferences > 0 && hasPrevMonth) {
            chart1.setFinishedStrokeColor(getResources().getColor(R.color.alert));
            chart1.setTextColor(getResources().getColor(R.color.alert));
            chart1.setBottomText("Over");
        }
        if (hasPrevMonth) {
            Double monthsDifferencesPercentage = (monthsDifferences / prevAvgPower) * 100;

            chart1.setMax(monthsDifferencesPercentage.intValue() + 100);
            chart1.setProgress(monthsDifferencesPercentage.intValue());
        }
    }

    public void initListeners() {
        monthLimitListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean hasPrevMonth = false;
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    DevicesMonthConsumed devicesMonthConsumed = dataSnapshot1.getValue(DevicesMonthConsumed.class);
                    if (devicesMonthConsumed != null && monthId != null) {
                        if (devicesMonthConsumed.get_id().equals(monthId)) {
                            actualConsumtionValue = devicesMonthConsumed.getTotalConsumed();
                            actualConsumption.setText(Utils.decimalFormat.format(actualConsumtionValue) + "W/h");
                            actualCostValue = Utils.price(actualConsumtionValue, currentUser.getUid());
                            currentCost.setText("$" + Utils.decimalFormat.format(actualCostValue));
                            actualLimitValur = devicesMonthConsumed.getLimit();
                            actualLimit.setText(actualLimitValur + "W/h");
                            actualMonth.setText(Utils.monthIdToString(devicesMonthConsumed.get_id()));
                        } else if (devicesMonthConsumed.get_id().equals(Utils.getPreviousMonthId(monthId))) {
                            hasPrevMonth = true;
                            prevConsuptionValue = devicesMonthConsumed.getTotalConsumed();
                            previousConsumtion.setText(Utils.decimalFormat.format(prevConsuptionValue) + "W/h");
                            prevCostValue = Utils.price(prevConsuptionValue, currentUser.getUid());
                            previusCost.setText("$" + Utils.decimalFormat.format(prevCostValue));
                            prevLimitValue = devicesMonthConsumed.getLimit();
                            previousLimit.setText(prevLimitValue + "W/h");
                            lastMonth.setText(Utils.monthIdToString(Utils.getPreviousMonthId(monthId)));
                        }
                    }
                }

                if (!hasPrevMonth) {
                    prevConsuptionValue = 0;
                    previousConsumtion.setText(0 + "W/h");
                    prevLimitValue = 0;
                    previousLimit.setText(0 + "W/h");
                    chart2.setMax(100);
                    chart2.setProgress(0);
                    chart2.setBottomText("No Data");
                    chart3.setMax(100);
                    chart3.setProgress(0);
                    chart3.setBottomText("No Data");
                    lastMonth.setText(Utils.monthIdToString(Utils.getPreviousMonthId(monthId)));
                }

                Double monthsDifferences = (actualConsumtionValue - prevConsuptionValue);
                if (monthsDifferences < 0) {
                    chart2.setFinishedStrokeColor(getResources().getColor(R.color.color3));
                    chart2.setTextColor(getResources().getColor(R.color.color3));
                    chart2.setBottomText("Under");
                    monthsDifferences = monthsDifferences * -1;
                } else if (monthsDifferences > 0 && hasPrevMonth) {
                    chart2.setFinishedStrokeColor(getResources().getColor(R.color.alert));
                    chart2.setTextColor(getResources().getColor(R.color.alert));
                    chart2.setBottomText("Over");
                }
                if (hasPrevMonth) {
                    Double monthsDifferencesPercentage = (monthsDifferences / prevConsuptionValue) * 100;

                    chart2.setMax(monthsDifferencesPercentage.intValue() + 100);
                    chart2.setProgress(monthsDifferencesPercentage.intValue());
                }

                Double monthsDifferencesCost = (actualCostValue - prevCostValue);
                if (monthsDifferencesCost < 0) {
                    chart3.setFinishedStrokeColor(getResources().getColor(R.color.color3));
                    chart3.setTextColor(getResources().getColor(R.color.color3));
                    chart3.setBottomText("Under");
                    monthsDifferencesCost = monthsDifferencesCost * -1;
                } else if (monthsDifferencesCost > 0 && hasPrevMonth) {
                    chart3.setFinishedStrokeColor(getResources().getColor(R.color.alert));
                    chart3.setTextColor(getResources().getColor(R.color.alert));
                    chart3.setBottomText("Over");
                }
                if (hasPrevMonth) {
                    Double monthsDifferencesPercentage = (monthsDifferencesCost / prevCostValue) * 100;

                    chart3.setMax(monthsDifferencesPercentage.intValue() + 100);
                    chart3.setProgress(monthsDifferencesPercentage.intValue());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReference.addValueEventListener(monthLimitListener);
    }
}
