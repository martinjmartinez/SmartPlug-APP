package com.example.martinjmartinez.proyectofinal.UI.Statistics;


import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.martinjmartinez.proyectofinal.Models.DevicesMonthConsumed;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MonthDetails extends Fragment {
    private String objectId, monthId, type;
    private TextView limit, consumed;
    private TextView shouldPay, currentAmount, percentage, consumedProgressbar, limitProgressbar;
    private ValueEventListener monthLimitListener;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private DatabaseReference databaseReference;


    public static MonthDetails newInstance(String monthId, String objectId, String type) {
        Bundle args = new Bundle();

        args.putString("objectId", objectId);
        args.putString("monthId", monthId);
        args.putString("type", type);

        MonthDetails fragment = new MonthDetails();
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
        View view = inflater.inflate(R.layout.month_details, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        limit = view.findViewById(R.id.limit);
        consumed = view.findViewById(R.id.consumed);
        shouldPay = view.findViewById(R.id.cost);
        limitProgressbar = view.findViewById(R.id.detail_limit);
        consumedProgressbar = view.findViewById(R.id.actual_limit);
        progressBar = view.findViewById(R.id.limit_progressbas);
        percentage = view.findViewById(R.id.limit_percentage);
        currentAmount = view.findViewById(R.id.actualCost);

        if (type.equals("Device")) {
            databaseReference = FirebaseDatabase.getInstance().getReference("Accounts/" + currentUser.getUid() + "/MonthlyConsumed/" + objectId + "/" + monthId);
        } else if (type.equals("Space")) {
            databaseReference = FirebaseDatabase.getInstance().getReference("Accounts/" + currentUser.getUid() + "/SpacesMonthlyConsumed/" + objectId + "/" + monthId);
        } else if (type.equals("Building")){
            databaseReference = FirebaseDatabase.getInstance().getReference("Accounts/" + currentUser.getUid() + "/BuildingMonthlyConsumed/" + objectId + "/" + monthId);
        }

        initListeners();

        return view;
    }


    public void initListeners() {
        monthLimitListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DevicesMonthConsumed devicesMonthConsumed = dataSnapshot.getValue(DevicesMonthConsumed.class);
                if (devicesMonthConsumed != null) {
                    if (devicesMonthConsumed.getLimit() == 0){
                        limitProgressbar.setText(R.string.Not_set);
                    } else {
                        limitProgressbar.setText(devicesMonthConsumed.getLimit() + " W/h");
                        limit.setText(devicesMonthConsumed.getLimit() + " W/h");
                        consumedProgressbar.setText(Utils.decimalFormat.format(devicesMonthConsumed.getTotalConsumed()));
                        consumed.setText(Utils.decimalFormat.format(devicesMonthConsumed.getTotalConsumed()) + " W/h");
                        double percentage1 = (devicesMonthConsumed.getTotalConsumed() / devicesMonthConsumed.getLimit()) * 100;
                        if(percentage1 >= 100){
                            progressBar.setMax(Double.valueOf(devicesMonthConsumed.getTotalConsumed()).intValue());
                            progressBar.setProgress(Double.valueOf(devicesMonthConsumed.getLimit()).intValue());
                            progressBar.setProgressBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.alert)));
                            progressBar.setProgressBackgroundTintMode(PorterDuff.Mode.DARKEN);
                        }else {
                            progressBar.setMax(Double.valueOf(devicesMonthConsumed.getLimit()).intValue());
                            progressBar.setProgress(Double.valueOf(devicesMonthConsumed.getTotalConsumed()).intValue());
                            if (percentage1 < 100 && percentage1 >= 75) {
                                progressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.color4)));
                            }
                        }

                        percentage.setText(Utils.decimalFormat.format(percentage1) + "%");
                    }

                    //TODO ADD COST
                    shouldPay.setText("$" + Utils.decimalFormat.format(Utils.price(devicesMonthConsumed.getLimit(), currentUser.getUid())));
                    currentAmount.setText("$" + Utils.decimalFormat.format(Utils.price(devicesMonthConsumed.getTotalConsumed(), currentUser.getUid())));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReference.addValueEventListener(monthLimitListener);
    }
}
