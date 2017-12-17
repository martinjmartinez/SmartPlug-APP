package com.example.martinjmartinez.proyectofinal.UI.Devices.Statistics;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.martinjmartinez.proyectofinal.Entities.Device;
import com.example.martinjmartinez.proyectofinal.Entities.Historial;
import com.example.martinjmartinez.proyectofinal.Models.HistorialReview;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Services.DeviceService;
import com.example.martinjmartinez.proyectofinal.UI.MainActivity.MainActivity;
import com.example.martinjmartinez.proyectofinal.Utils.Chart.ChartUtils;
import com.example.martinjmartinez.proyectofinal.Utils.Constants;
import com.example.martinjmartinez.proyectofinal.Utils.DateUtils;
import com.example.martinjmartinez.proyectofinal.Utils.Utils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

import static com.example.martinjmartinez.proyectofinal.Utils.Constants.DATE_FORMAT_SHORT;

public class DeviceStatisticsDetailsFragment  extends Fragment{

    private Activity mActivity;
    private MainActivity mMainActivity;
    private Realm realm;
    private String mDeviceId;
    private Button mStartDateButton;
    private Button mEndDateButton;
    private Date mStartDate;
    private Device mDevice;
    private HistorialReview maxDay;
    private HistorialReview minDay;
    private Date mEndDate;
    private Spinner mDateSpinner;
    private DeviceService deviceService;
    private TabLayout chartTabLayout;
    private DeviceChartsStatisticsViewPager deviceChartsStatisticsViewPager;
    private ViewPager chartsViewPager;
    private TextView actualConsumed, averageConsumed, actualPower, averagePower, actualTime, averageTime;
    private TextView maxDate, minDate, maxTime, minTime, maxPower, minPower;

    public DeviceStatisticsDetailsFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mDeviceId = getArguments().getString("deviceId");
        }
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

        mMainActivity.getSupportActionBar().setTitle(R.string.statistics);
    }

    public static DeviceStatisticsDetailsFragment newInstance(String deviceId) {
        Bundle args = new Bundle();
        args.putString("deviceId", deviceId);

        DeviceStatisticsDetailsFragment fragment = new DeviceStatisticsDetailsFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.statistics_details, container, false);

        iniVariables(view);
        initListeners();

        mDevice = deviceService.getDeviceById(mDeviceId);
        Date today = DateUtils.getCurrentDate();

        setupDatePickerButtons(new Pair<>(today, today));
        setAdapters();
        return view;
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
    }

    private void setAdapters() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.date_filters_array, R.layout.support_simple_spinner_dropdown_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDateSpinner.setAdapter(adapter);
        mDateSpinner.setSelection(0);

        setupViewPager(chartsViewPager);
    }

    private void setupViewPager(ViewPager chartViewPager) {
        deviceChartsStatisticsViewPager = new DeviceChartsStatisticsViewPager(getChildFragmentManager(), getContext(), mDeviceId, mStartDate, mEndDate);

        chartTabLayout.setupWithViewPager(chartViewPager, true);
        chartViewPager.setAdapter(deviceChartsStatisticsViewPager);
    }

    private void iniVariables(View view) {
        realm = Realm.getDefaultInstance();

        mDateSpinner =  view.findViewById(R.id.spinnerFiltersGeneral);
        mStartDateButton =  view.findViewById(R.id.start_date_general);
        mEndDateButton = view.findViewById(R.id.end_date_general);
        chartTabLayout =  view.findViewById(R.id.tabDotsGeneral);
        chartsViewPager =  view.findViewById(R.id.chartsGeneral);
        actualConsumed =  view.findViewById(R.id.actual_consumed_general);
        averageConsumed = view.findViewById(R.id.average_consumed_general);
        actualPower =  view.findViewById(R.id.actual_power_general);
        averagePower =  view.findViewById(R.id.average_power_general);
        actualTime =  view.findViewById(R.id.actual_time_general);
        averageTime =  view.findViewById(R.id.average_time_general);
        maxDate =  view.findViewById(R.id.max_date_general);
        minDate =  view.findViewById(R.id.min_date_general);
        maxTime =  view.findViewById(R.id.max_time_general);
        minTime =  view.findViewById(R.id.min_time_general);
        maxPower =  view.findViewById(R.id.max_power_general);
        minPower =  view.findViewById(R.id.min_power_general);

        deviceService = new DeviceService(realm);
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
                        fetchResults();

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
        fetchResults();

        String startDateString = DateUtils.format(DATE_FORMAT_SHORT, mStartDate);
        String endDateString = DateUtils.format(DATE_FORMAT_SHORT, mEndDate);

        mStartDateButton.setText(startDateString);
        mEndDateButton.setText(endDateString);
    }

    public void fetchResults() {
        if (!mDeviceId.isEmpty()) {
            RealmResults<Historial> historials = realm.where(Historial.class).equalTo("device._id", mDeviceId).between("startDate", mStartDate, mEndDate).between("lastLogDate", mStartDate, mEndDate).findAll().sort("startDate", Sort.ASCENDING);
            List<HistorialReview> results = ChartUtils.fetchDataDetails(historials);

            getMaxAndMinDays(results);
        }
    }

    public void getMaxAndMinDays(List<HistorialReview> results) {
        double totalTime = 0;
        double avgeTime = 0;
        double totalConsumed = 0;
        double avgConsumed = 0;
        double actPower = 0;
        double averageP = 0;
        int resultsSize =0;

        if (!results.isEmpty()) {
            resultsSize = results.size();
            maxDay = results.get(0);
            minDay = results.get(0);

            for (HistorialReview historialReview : results) {
                totalTime = totalTime + historialReview.getTotalTimeInSeconds();
                totalConsumed = totalConsumed + historialReview.getPowerConsumed();
                actPower = actPower + historialReview.getAveragePower();

                if (historialReview.getPowerConsumed() < minDay.getPowerConsumed())
                    minDay = historialReview;
                if (historialReview.getPowerConsumed() > maxDay.getPowerConsumed())
                    maxDay = historialReview;
            }

            avgeTime = totalTime/ resultsSize;
            avgConsumed = totalConsumed/ resultsSize;
            averageP = actPower / resultsSize;

            actualConsumed.setText(Utils.decimalFormat.format(totalConsumed) + " W/h");
            averageConsumed.setText(Utils.decimalFormat.format(avgConsumed) + " W/h");
            actualTime.setText(DateUtils.timeFormatter(totalTime));
            averageTime.setText(DateUtils.timeFormatter(avgeTime));
            averagePower.setText(Utils.decimalFormat.format(mDevice.getAverageConsumption()) + " W");
            actualPower.setText(Utils.decimalFormat.format(averageP) + " W");
            maxDate.setText(maxDay.getDate());
            minDate.setText(minDay.getDate());
            maxPower.setText(Utils.decimalFormat.format(maxDay.getPowerConsumed()) + " W/h");
            minPower.setText(Utils.decimalFormat.format(minDay.getPowerConsumed()) + " W/h");
            maxTime.setText(DateUtils.timeFormatter(maxDay.getTotalTimeInSeconds()));
            minTime.setText(DateUtils.timeFormatter(minDay.getTotalTimeInSeconds()));
        }
    }
}
