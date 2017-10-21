package com.example.martinjmartinez.proyectofinal.UI.Statistics;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MostWantedFragment extends Fragment {

    private Date mStartDate;
    private Date mEndDate;

    public static MostWantedFragment newInstance(String startDate, String endDate) {
        Bundle args = new Bundle();
        args.putString("startDate", startDate);
        args.putString("endDate", endDate);

        MostWantedFragment fragment = new MostWantedFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            try {
                mStartDate = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa").parse(getArguments().getString("startDate"));
                mEndDate =  new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa").parse(getArguments().getString("endDate"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.most_wanted_layout, container, false);



        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}
