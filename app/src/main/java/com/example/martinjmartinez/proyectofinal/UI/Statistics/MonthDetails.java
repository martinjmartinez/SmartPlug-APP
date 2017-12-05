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

import com.example.martinjmartinez.proyectofinal.Models.MonthConsumed;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Utils.DateUtils;
import com.example.martinjmartinez.proyectofinal.Utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MonthDetails extends Fragment {
    private String objectId, monthId;
    private TextView limit, consumed;
    private TextView shouldPay, currentAmount, percentage, consumedProgressbar, limitProgressbar;
    private ValueEventListener monthLimitListener;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private DatabaseReference databaseReference;


    public static MonthDetails newInstance(String monthId, String objectId) {
        Bundle args = new Bundle();

        args.putString("objectId", objectId);
        args.putString("monthId", monthId);

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
        databaseReference = FirebaseDatabase.getInstance().getReference("Accounts/" + currentUser.getUid() + "/MonthlyConsumed/" + objectId + "/" + monthId);


        initListeners();

        return view;
    }


    public void initListeners() {
        monthLimitListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MonthConsumed monthConsumed = dataSnapshot.getValue(MonthConsumed.class);
                if (monthConsumed != null) {
                    if (monthConsumed.getLimit() == 0){
                        limitProgressbar.setText("Not set");
                    } else {
                        limitProgressbar.setText(monthConsumed.getLimit() + " W/h");
                        limit.setText(monthConsumed.getLimit() + " W/h");
                        consumedProgressbar.setText(Utils.decimalFormat.format(monthConsumed.getTotalConsumed()));
                        consumed.setText(Utils.decimalFormat.format(monthConsumed.getTotalConsumed()) + " W/h");
                        double percentage1 = (monthConsumed.getTotalConsumed() / monthConsumed.getLimit()) * 100;
                        if(percentage1 >= 100){
                            progressBar.setMax(Double.valueOf(monthConsumed.getTotalConsumed()).intValue());
                            progressBar.setProgress(Double.valueOf(monthConsumed.getLimit()).intValue());
                            progressBar.setProgressBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.alert)));
                            progressBar.setProgressBackgroundTintMode(PorterDuff.Mode.DARKEN);
                        }else {
                            progressBar.setMax(Double.valueOf(monthConsumed.getLimit()).intValue());
                            progressBar.setProgress(Double.valueOf(monthConsumed.getTotalConsumed()).intValue());
                            if (percentage1 < 100 && percentage1 >= 75) {
                                progressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.color4)));
                            }
                        }

                        percentage.setText(Utils.decimalFormat.format(percentage1) + "%");
                    }

                    //TODO ADD COST
                    shouldPay.setText("$" + Utils.decimalFormat.format(Utils.price(monthConsumed.getLimit(), currentUser.getUid())));
                    currentAmount.setText("$" + Utils.decimalFormat.format(Utils.price(monthConsumed.getTotalConsumed(), currentUser.getUid())));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReference.addValueEventListener(monthLimitListener);
    }
}
