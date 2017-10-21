package com.example.martinjmartinez.proyectofinal.UI.Statistics;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;

import com.example.martinjmartinez.proyectofinal.Entities.Building;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Services.BuildingService;
import com.example.martinjmartinez.proyectofinal.Services.DeviceService;
import com.example.martinjmartinez.proyectofinal.Services.SpaceService;
import com.example.martinjmartinez.proyectofinal.UI.MainActivity.MainActivity;
import com.example.martinjmartinez.proyectofinal.Utils.Constants;
import com.example.martinjmartinez.proyectofinal.Utils.DateUtils;
import com.example.martinjmartinez.proyectofinal.Utils.Utils;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Realm;

import static com.example.martinjmartinez.proyectofinal.Utils.Constants.DATE_FORMAT_SHORT;

public class StatisticsFragment extends Fragment {

    private MainActivity mMainActivity;
    private Activity mActivity;
    private SpaceService spaceService;
    private DeviceService deviceService;
    private BuildingService buildingService;
    private Button mStartDateButton;
    private Button mEndDateButton;
    private Date mStartDate;
    private Date mEndDate;
    private ChartsViewPagerAdapter chartsViewPagerAdapter;
    static public String objectId;
    private Building mBuilding;
    private Spinner mDateSpinner;
    private Realm realm;
    private RecyclerView charts;
    private List<Building> chartData;
    private ViewPager chartsViewPager;

    public StatisticsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        objectId = bundle != null ? bundle.getString(Constants.BUILDING_ID, "") : "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.statistics_fragment, container, false);

        iniVariables(view);
        initListeners();

        Date today = DateUtils.getCurrentDate();
        setupDatePickerButtons(new Pair<>(today, today));
        setAdapters();
        return view;
    }

    private void iniVariables(View view) {
        realm = Realm.getDefaultInstance();
        spaceService = new SpaceService(realm);
        deviceService = new DeviceService(realm);
        buildingService = new BuildingService(realm);
        mStartDateButton = (Button) view.findViewById(R.id.start_date);
        mEndDateButton = (Button) view.findViewById(R.id.end_date);
        mDateSpinner = (Spinner) view.findViewById(R.id.spinnerFilters);
        mStartDate = DateUtils.getCurrentDate();
        mEndDate = DateUtils.getCurrentDate();
        chartData = new ArrayList<>();
        chartsViewPager = (ViewPager) view.findViewById(R.id.charts);
    }

    private void setAdapters() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.date_filters_array, R.layout.support_simple_spinner_dropdown_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDateSpinner.setAdapter(adapter);
        mDateSpinner.setSelection(0);

        setupViewPager(chartsViewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        chartsViewPagerAdapter = new ChartsViewPagerAdapter(getChildFragmentManager(), getContext(), objectId, mStartDate, mEndDate);

        viewPager.setAdapter(chartsViewPagerAdapter);
    }

    private void initListeners() {

        mStartDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog((Button) view, null, mEndDate, true);
            }
        });

        mEndDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date maxDate = DateUtils.getCurrentDate();
                showDatePickerDialog((Button) view, mStartDate, maxDate, false);
            }
        });

        mDateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Date today = DateUtils.getCurrentDate();
                Date startOfDate = DateUtils.getStartOfDate(today);
                ArrayMap<Integer, Pair<Date, Date>> spinnerDateMapping = new ArrayMap<>();

                spinnerDateMapping.put(0, new Pair<>(startOfDate, today));
                spinnerDateMapping.put(1, new Pair<>(Utils.firstDayOfCurrentWeek(), today));
                spinnerDateMapping.put(2, new Pair<>(Utils.firstDayOfPreviousWeek(), Utils.lastDayOfPreviousWeek()));
                spinnerDateMapping.put(3, new Pair<>(Utils.firstDayOfCurrentMonth(), today));
                spinnerDateMapping.put(4, new Pair<>(Utils.firstDayOfPreviousMonth(), Utils.lastDayOfPreviousMonth()));

                setupDatePickerButtons(spinnerDateMapping.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        mMainActivity.toggleDrawerIcon(false, R.drawable.ic_action_back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainActivity.onBackPressed();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mActivity = getActivity();
        mMainActivity = (MainActivity) mActivity;

    }

    @Override
    public void onResume() {
        super.onResume();

        mMainActivity.getSupportActionBar().setTitle("Statistics");
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (mMainActivity.getSupportFragmentManager().getBackStackEntryCount() <= 1) {
            mMainActivity.toggleDrawerIcon(true, 0, null);
        }

    }

    void showDatePickerDialog(final Button currentButton, Date minDate, Date maxDate, final boolean setStartDate) {
        final Calendar calendar = Calendar.getInstance();
        Date buttonDate;

        try {
            buttonDate = DateUtils.parse(DATE_FORMAT_SHORT, Utils.getText(currentButton));
            calendar.setTime(buttonDate);
        } catch (ParseException ex) {
            Log.e("showDatePickerDialog", "Error parsing the date");
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dateDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        String dateFormat = DATE_FORMAT_SHORT;
                        String formattedDate = Utils.formatDateFromString(selectedDate, dateFormat, dateFormat);

                        currentButton.setText(formattedDate);

                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        if (setStartDate) {
                            mStartDate = calendar.getTime();
                        } else {
                            Calendar newCalendar = calendar;
                            calendar.set(Calendar.HOUR_OF_DAY,23);
                            calendar.set(Calendar.MINUTE,59);
                            calendar.set(Calendar.SECOND,59);
                            mEndDate = calendar.getTime();

                            String endDateString = DateUtils.format(DATE_FORMAT_SHORT, mEndDate);
                            String today = DateUtils.format(DATE_FORMAT_SHORT, DateUtils.getCurrentDate());

                            if (endDateString.contains(today)) {
                                mEndDate = DateUtils.getCurrentDate();
                            }
                        }

                        setupViewPager(chartsViewPager);
                    }
                }, year, month, day);

        if (minDate != null) {
            dateDialog.getDatePicker().setMinDate(minDate.getTime());
        }

        if (maxDate != null) {
            dateDialog.getDatePicker().setMaxDate(maxDate.getTime());
        }

        dateDialog.show();
    }

    void setupDatePickerButtons(Pair<Date, Date> dates) {
        mStartDate = dates.first;
        mEndDate = dates.second;

        setupViewPager(chartsViewPager);

        String startDateString = DateUtils.format(DATE_FORMAT_SHORT, mStartDate);
        String endDateString = DateUtils.format(DATE_FORMAT_SHORT, mEndDate);

        mStartDateButton.setText(startDateString);
        mEndDateButton.setText(endDateString);
    }

}

