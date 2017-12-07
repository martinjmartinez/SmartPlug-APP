package com.example.martinjmartinez.proyectofinal.UI.Spaces.Statistics;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.example.martinjmartinez.proyectofinal.Entities.MonthlyLimit;
import com.example.martinjmartinez.proyectofinal.Entities.Space;
import com.example.martinjmartinez.proyectofinal.Models.GroupMonthConsumed;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Services.SpaceService;
import com.example.martinjmartinez.proyectofinal.Services.SpacesLimitsService;
import com.example.martinjmartinez.proyectofinal.UI.Devices.Statistics.MonthSpinnerAdapter;
import com.example.martinjmartinez.proyectofinal.Utils.Chart.ChartUtils;
import com.example.martinjmartinez.proyectofinal.Utils.Utils;
import com.github.mikephil.charting.charts.BarChart;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class SpaceMonthlyLimits extends Fragment{

    private Realm realm;
    private String monthSelected;
    private SpaceService spaceService;
    private SpacesLimitsService spacesLimitsService;
    private Space space;
    private String spaceId;
    private BarChart groupBarChart;
    private Spinner monthSpinner;
    private ViewPager monthDetailsViewPager;
    private MonthSpinnerAdapter monthSpinnerAdapter;
    private SpaceMonthDetailsViewPagerAdapter spaceMonthDetailsViewPagerAdapter;
    private TabLayout detailsTabLayout;
    private ArrayList<String> monthsId;
    private FirebaseAuth mAuth;
    private List<Double> limits;
    private List<Double> consumed;
    private ValueEventListener limitsListener;
    private DatabaseReference databaseReference;


    public static SpaceMonthlyLimits newInstance(String spaceId) {
        Bundle args = new Bundle();
        args.putString("spaceId", spaceId);

        SpaceMonthlyLimits fragment = new SpaceMonthlyLimits();
        fragment.setArguments(args);

        return fragment;
    }

    private void iniVariables(View view) {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Accounts/" + currentUser.getUid() + "/SpacesMonthlyConsumed/" + spaceId);

        limits = new ArrayList<>();
        consumed = new ArrayList<>();
        monthsId = new ArrayList<>();
        groupBarChart = view.findViewById(R.id.groupBarChart);
        monthSpinner = view.findViewById(R.id.monthsSpinner);
        monthDetailsViewPager = view.findViewById(R.id.viewpagerMonthDetails);
        detailsTabLayout = view.findViewById(R.id.monthDetailsTabDots);
    }

    private void setAdapters() {
        monthSpinnerAdapter = new MonthSpinnerAdapter(getContext(), R.layout.months_spinner, monthsId);
        monthSpinner.setAdapter(monthSpinnerAdapter);
        monthSpinner.setSelection(0);
        monthSelected = monthsId.get(0);
        setupViewPager(monthDetailsViewPager);
    }

    private void setupViewPager(ViewPager fragmentsViewpager) {
        spaceMonthDetailsViewPagerAdapter = new SpaceMonthDetailsViewPagerAdapter(getChildFragmentManager(), getContext(), spaceId, Utils.monthStringToId(monthSelected));
        detailsTabLayout.setupWithViewPager(fragmentsViewpager, true);

        fragmentsViewpager.setAdapter(spaceMonthDetailsViewPagerAdapter);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            spaceId = getArguments().getString("spaceId");

            realm = Realm.getDefaultInstance();
            spaceService = new SpaceService(realm);
            spacesLimitsService = new SpacesLimitsService(realm);
            space = spaceService.getSpaceById(spaceId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.device_monthly_limit_fragment, container, false);

        iniVariables(view);

        initListeners();


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    public void fetchResults() {
        RealmResults<MonthlyLimit> monthlyLimits = realm.where(MonthlyLimit.class).equalTo("space._id", spaceId).findAll().sort("date", Sort.ASCENDING);
        ArrayList<String> labels = new ArrayList<>();
        limits.clear();
        consumed.clear();
        for(MonthlyLimit monthlyLimit : monthlyLimits) {
            labels.add(Utils.monthIdToString(monthlyLimit.getMonth()));
            limits.add(monthlyLimit.getLimit());
            consumed.add(monthlyLimit.getTotalConsumed());
        }

        ChartUtils.makeGroupBarChart(limits, consumed, groupBarChart, labels);

    }

    private void initListeners() {
        limitsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    GroupMonthConsumed groupMonthConsumed = dataSnapshot1.getValue(GroupMonthConsumed.class);
                    if(groupMonthConsumed != null) {
                        String[] parts = groupMonthConsumed.get_id().split("_");
                        monthsId.add(parts[0] + " " + parts[1]);
                        setAdapters();
                        spacesLimitsService.updateOrCreateLocal(groupMonthConsumed);
                    }
                }
                fetchResults();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        databaseReference.addListenerForSingleValueEvent(limitsListener);

        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                monthSelected = monthsId.get(position);
                setupViewPager(monthDetailsViewPager);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                monthSelected = monthsId.get(0);
            }
        });
    }
}
