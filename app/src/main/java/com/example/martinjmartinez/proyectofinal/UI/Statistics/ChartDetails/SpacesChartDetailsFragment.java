package com.example.martinjmartinez.proyectofinal.UI.Statistics.ChartDetails;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.martinjmartinez.proyectofinal.Entities.Building;
import com.example.martinjmartinez.proyectofinal.Entities.Historial;
import com.example.martinjmartinez.proyectofinal.Entities.Space;
import com.example.martinjmartinez.proyectofinal.Models.HistorialReview;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Services.BuildingService;
import com.example.martinjmartinez.proyectofinal.UI.Spaces.Adapters.SpaceSpinnerAdapter;
import com.example.martinjmartinez.proyectofinal.Utils.Chart.ChartUtils;
import com.example.martinjmartinez.proyectofinal.Utils.DateUtils;
import com.example.martinjmartinez.proyectofinal.Utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class SpacesChartDetailsFragment extends Fragment {

    private Date mStartDate;
    private Date mEndDate;
    private Building mBuilding;
    private String buildingId;
    private BuildingService buildingService;
    private Realm realm;
    private TextView maxPower, minPower;
    private TextView maxDate, minDate;
    private TextView maxTime, minTime;
    private Spinner spacesSpinner;
    private SpaceSpinnerAdapter mSpaceSpinnerAdapter;
    private HistorialReview maxDay;
    private HistorialReview minDay;
    private Space space;
    private List<Space> spaces;

    public static SpacesChartDetailsFragment newInstance(String buildingId, Date startDate, Date endDate) {
        Bundle args = new Bundle();
        args.putLong("startDate", startDate.getTime());
        args.putLong("endDate", endDate.getTime());
        args.putString("buildingId", buildingId);

        SpacesChartDetailsFragment fragment = new SpacesChartDetailsFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments() != null) {
                mStartDate = new Date(getArguments().getLong("startDate"));
                mEndDate = new Date(getArguments().getLong("endDate"));
                buildingId = getArguments().getString("buildingId");

                realm = Realm.getDefaultInstance();
                buildingService = new BuildingService(realm);
                mBuilding = buildingService.getBuildingById(buildingId);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chart_details_fragment, container, false);

        maxDate = (TextView) view.findViewById(R.id.max_date);
        minDate = (TextView) view.findViewById(R.id.min_date);
        maxTime = (TextView) view.findViewById(R.id.max_time);
        minTime = (TextView) view.findViewById(R.id.min_time);
        maxPower = (TextView) view.findViewById(R.id.max_power);
        minPower = (TextView) view.findViewById(R.id.min_power);
        spacesSpinner = (Spinner) view.findViewById(R.id.itemsSpinner);
        spaces = new ArrayList<>();

        initListenners();
        spaces = getSpaces();

        spacesSpinner.setVisibility(View.VISIBLE);
        if(!spaces.isEmpty()) {
            space = spaces.get(0);
            setUpSpacesSpinner(spaces);
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public void initListenners() {
        spacesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                space = spaces.get(position);
                fetchResults();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                space = spaces.get(0);
            }
        });
    }

    public void fetchResults() {
        RealmResults<Historial> historials = realm.where(Historial.class).equalTo("space._id", space.get_id()).between("startDate", mStartDate, mEndDate).between("lastLogDate", mStartDate, mEndDate).findAll().sort("startDate", Sort.ASCENDING);
        List<HistorialReview> results = ChartUtils.fetchDataDetails(historials);

        getMaxAndMinDays(results);
    }

    public void getMaxAndMinDays(List<HistorialReview> results) {
        if (!results.isEmpty()) {
            maxDay = results.get(0);
            minDay = results.get(0);

            for (HistorialReview historialReview : results) {
                if (historialReview.getPowerConsumed() < minDay.getPowerConsumed())
                    minDay = historialReview;
                if (historialReview.getPowerConsumed() > maxDay.getPowerConsumed())
                    maxDay = historialReview;
            }

            maxDate.setText(maxDay.getDate());
            minDate.setText(minDay.getDate());
            maxPower.setText(Utils.decimalFormat.format(maxDay.getPowerConsumed()) + " W/h");
            minPower.setText(Utils.decimalFormat.format(minDay.getPowerConsumed()) + " W/h");
            maxTime.setText(DateUtils.timeFormatter(maxDay.getTotalTimeInSeconds()));
            minTime.setText(DateUtils.timeFormatter(minDay.getTotalTimeInSeconds()));
        }
    }

    public List<Space> getSpaces() {
        List<Space> spacesInHistorials = new ArrayList<>();

        for(Space space: mBuilding.getSpaces()) {
            RealmResults<Historial> historials = realm.where(Historial.class).equalTo("space._id", space.get_id()).between("startDate", mStartDate, mEndDate).between("lastLogDate", mStartDate, mEndDate).findAll().sort("startDate", Sort.ASCENDING);

            if (!historials.isEmpty()) {
                spacesInHistorials.add(space);
            }

        }
        return spacesInHistorials;
    }
    public void setUpSpacesSpinner(List<Space> items) {
        if (items.size() != 0) {
            mSpaceSpinnerAdapter = new SpaceSpinnerAdapter(getContext(), R.layout.spaces_item_spinner, items);
            spacesSpinner.setAdapter(mSpaceSpinnerAdapter);
        } else {
            spacesSpinner.setEnabled(false);
        }

    }
}