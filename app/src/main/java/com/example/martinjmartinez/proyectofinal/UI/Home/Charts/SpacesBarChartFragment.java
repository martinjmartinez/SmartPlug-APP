package com.example.martinjmartinez.proyectofinal.UI.Home.Charts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.martinjmartinez.proyectofinal.Entities.Building;
import com.example.martinjmartinez.proyectofinal.Entities.Space;
import com.example.martinjmartinez.proyectofinal.Models.DeviceFB;
import com.example.martinjmartinez.proyectofinal.Models.SpaceFB;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Services.BuildingService;
import com.example.martinjmartinez.proyectofinal.Services.SpaceService;
import com.example.martinjmartinez.proyectofinal.Utils.Chart.ChartUtils;
import com.example.martinjmartinez.proyectofinal.Utils.Constants;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class SpacesBarChartFragment extends Fragment {

    private Realm realm;
    private String buildingId, userId;
    private SpaceService spaceService;
    private Building mBuilding;
    private BarChart chart;
    private TextView title;
    private DatabaseReference databaseReference;
    private List<Space> spaces;
    private ValueEventListener spacesListener;
    private FirebaseAuth mAuth;

    public static SpacesBarChartFragment newInstance(String buildingId) {
        Bundle args = new Bundle();

        args.putString("buildingId", buildingId);

        SpacesBarChartFragment fragment = new SpacesBarChartFragment();
        fragment.setArguments(args);

        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            buildingId = getArguments().getString("buildingId");

            realm = Realm.getDefaultInstance();
            spaceService = new SpaceService(realm);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_chart_bar, container, false);

        chart = view.findViewById(R.id.chart);
        title =  view.findViewById(R.id.chart_title_home);
        mAuth = FirebaseAuth.getInstance();
        userId = getActivity().getSharedPreferences(Constants.USER_INFO, 0).getString(Constants.USER_ID, FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference = FirebaseDatabase.getInstance().getReference("Accounts/"+ userId + "/Spaces");
        spaces = new ArrayList<>();
        chart.getDescription().setEnabled(false);

        return view;
    }

    private void initListeners() {
        spacesListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    SpaceFB spaceFB = dataSnapshot1.getValue(SpaceFB.class);
                    spaceService.updateOrCreate(spaceFB);
                }
                fillChart();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReference.addValueEventListener(spacesListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        databaseReference.removeEventListener(spacesListener);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initListeners();
        title.setText("Spaces");
    }

    public void fillChart() {
        ArrayList<String> xVals = new ArrayList<>();
        ArrayList<BarEntry> yVals = new ArrayList<>();
        List<IBarDataSet> datasets = new ArrayList<>();
        int counter = 0;
        spaces = spaceService.allActiveSpacesByBuilding(buildingId);

        if (spaces != null) {
            for (Space space : spaces) {
                Log.e("Name", space.getName());
                xVals.add(space.getName());
                yVals.add(new BarEntry(counter, (float) space.getAverageConsumption()));
                datasets.add(new BarDataSet(yVals, space.getName()));
                counter++;
            }

            ChartUtils.makeBarChart(datasets,chart, xVals);
        }
    }
}