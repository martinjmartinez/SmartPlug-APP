package com.example.martinjmartinez.proyectofinal.UI.Routines.Fragments;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.martinjmartinez.proyectofinal.Entities.Device;
import com.example.martinjmartinez.proyectofinal.Entities.Routine;
import com.example.martinjmartinez.proyectofinal.Models.RoutineFB;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Services.DeviceService;
import com.example.martinjmartinez.proyectofinal.Services.RoutineService;
import com.example.martinjmartinez.proyectofinal.UI.Devices.Adapters.DeviceSpinnerAdapter;
import com.example.martinjmartinez.proyectofinal.UI.MainActivity.MainActivity;
import com.example.martinjmartinez.proyectofinal.Utils.Constants;
import com.example.martinjmartinez.proyectofinal.Utils.DateUtils;
import com.example.martinjmartinez.proyectofinal.Utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;

public class RoutineCreationFragment extends Fragment {
    private Activity mActivity;
    private MainActivity mMainActivity;
    private EditText nameTextView;
    private TextView startTimeTextView, endTimeTextView;
    private CardView mondayCard, tuesdayCard, wednesdayCard, thursdayCard, fridayCard, saturdayCard, sundayCard, endTimeCard;
    private ToggleButton actionToggle;
    private LinearLayout turnOffInfo;
    private Button saveButton;
    private Switch shouldStop;
    private Integer startHour, startMin, endHour, endMinute;
    private String buildingid;
    private DeviceSpinnerAdapter deviceSpinnerAdapter;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private Routine routine;
    private DeviceService deviceService;
    private RoutineService routineService;
    private Device selectedDevice;
    private Spinner deviceSpinner;
    private HashMap<Integer, Boolean> weekDays;
    private List<Device> devices;
    private boolean mondayToggle, tuesdayToggle, wednesdayToggle, thursdayToggle, fridayToggle, saturdayToggle, sundayToggle;

    public RoutineCreationFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            buildingid = bundle.getString(Constants.BUILDING_ID, "");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.routine_create_update_fragment, container, false);

        initWeekDays();
        iniVariables(view);
        initListeners();
        initView();
        return view;
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

        mMainActivity.getSupportActionBar().setTitle("Create Routine");
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (mMainActivity.getSupportFragmentManager().getBackStackEntryCount() <= 1) {
            mMainActivity.toggleDrawerIcon(true, 0, null);
        }
    }

    private void iniVariables(View view) {
        routineService = new RoutineService(Realm.getDefaultInstance());
        deviceService = new DeviceService(Realm.getDefaultInstance());
       nameTextView = view.findViewById(R.id.routine_name_editText);
        startTimeTextView = view.findViewById(R.id.start_routine);
        endTimeTextView = view.findViewById(R.id.end_routine);
        endTimeCard = view.findViewById(R.id.endTimeContainer);
        mondayCard = view.findViewById(R.id.monday);
        tuesdayCard = view.findViewById(R.id.tuesday);
        wednesdayCard = view.findViewById(R.id.wednesday);
        thursdayCard = view.findViewById(R.id.thursday);
        fridayCard = view.findViewById(R.id.friday);
        saturdayCard = view.findViewById(R.id.saturday);
        sundayCard = view.findViewById(R.id.sunday);
        actionToggle = view.findViewById(R.id.action);
        turnOffInfo = view.findViewById(R.id.turn_off_info);
        saveButton = view.findViewById(R.id.routine_save_button);
        shouldStop = view.findViewById(R.id.should_stop);
        deviceSpinner = view.findViewById(R.id.routine_device_select);
    }

    public void setUpDevicesSpinner(List<Device> items) {
        if (items.size() != 0) {
            deviceSpinnerAdapter = new DeviceSpinnerAdapter(getContext(), R.layout.devices_item_spinner, items);
            deviceSpinner.setAdapter(deviceSpinnerAdapter);
            saveButton.setBackground(getResources().getDrawable(R.color.colorPrimary));
            saveButton.setEnabled(true);
        } else {
            deviceSpinner.setEnabled(false);
            saveButton.setBackground(getResources().getDrawable(R.color.disabled));
            saveButton.setEnabled(false);
        }
    }

    private void initView() {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        startTimeTextView.setText(DateUtils.getTime(hour,minute));
        endTimeTextView.setText(DateUtils.getTime(hour,minute));
        devices = deviceService.allActiveDevicesByBuilding(buildingid);
        setUpDevicesSpinner(devices);
    }
    private void initWeekDays() {
        weekDays = new HashMap<>();
        weekDays.put(1,false);
        weekDays.put(2,false);
        weekDays.put(3,false);
        weekDays.put(4,false);
        weekDays.put(5,false);
        weekDays.put(6,false);
        weekDays.put(7,false);
    }

    private void initListeners() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mActivity.onBackPressed();
            }
        });

        deviceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDevice = devices.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedDevice = devices.get(0);
            }
        });

        mMainActivity.toggleDrawerIcon(false, R.drawable.ic_action_back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainActivity.onBackPressed();
            }
        });

        actionToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(checked) {
                    turnOffInfo.setVisibility(View.VISIBLE);
                } else {
                  turnOffInfo.setVisibility(View.GONE);
                }
            }
        });

        shouldStop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(checked) {
                    endTimeCard.setVisibility(View.VISIBLE);
                } else {
                    endTimeCard.setVisibility(View.GONE);
                }
            }
        });

        startTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        startHour = selectedHour;
                        startMin = selectedMinute;
                        startTimeTextView.setText( DateUtils.getTime(selectedHour, selectedMinute));
                    }
                }, hour, minute, false);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        endTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        endHour = selectedHour;
                        endMinute = selectedMinute;
                        endTimeTextView.setText( DateUtils.getTime(selectedHour, selectedMinute));
                    }
                }, hour, minute, false);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
        mondayCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mondayToggle) {
                    mondayToggle = false;
                    mondayCard.setBackground(getResources().getDrawable(R.color.cardBackground));
                    ((TextView)mondayCard.getChildAt(0)).setTextColor(getResources().getColor(R.color.disabled));
                } else {
                    mondayCard.setBackground(getResources().getDrawable(R.color.colorPrimaryDark));
                    ((TextView)mondayCard.getChildAt(0)).setTextColor(getResources().getColor(R.color.cardBackground));
                    mondayToggle = true;
                }
                weekDays.put(2, mondayToggle);
            }
        });
        tuesdayCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tuesdayToggle) {
                    tuesdayToggle = false;
                    tuesdayCard.setBackground(getResources().getDrawable(R.color.cardBackground));
                    ((TextView)tuesdayCard.getChildAt(0)).setTextColor(getResources().getColor(R.color.disabled));
                } else {
                    tuesdayCard.setBackground(getResources().getDrawable(R.color.colorPrimaryDark));
                    ((TextView)tuesdayCard.getChildAt(0)).setTextColor(getResources().getColor(R.color.cardBackground));
                    tuesdayToggle = true;
                }
                weekDays.put(3, tuesdayToggle);
            }
        });

        wednesdayCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wednesdayToggle) {
                    wednesdayToggle = false;
                    wednesdayCard.setBackground(getResources().getDrawable(R.color.cardBackground));
                    ((TextView)wednesdayCard.getChildAt(0)).setTextColor(getResources().getColor(R.color.disabled));
                } else {
                    wednesdayCard.setBackground(getResources().getDrawable(R.color.colorPrimaryDark));
                    ((TextView)wednesdayCard.getChildAt(0)).setTextColor(getResources().getColor(R.color.cardBackground));
                    wednesdayToggle = true;
                }
                weekDays.put(4, wednesdayToggle);
            }
        });

        thursdayCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (thursdayToggle) {
                    thursdayToggle = false;
                    thursdayCard.setBackground(getResources().getDrawable(R.color.cardBackground));
                    ((TextView)thursdayCard.getChildAt(0)).setTextColor(getResources().getColor(R.color.disabled));
                } else {
                    thursdayCard.setBackground(getResources().getDrawable(R.color.colorPrimaryDark));
                    ((TextView)thursdayCard.getChildAt(0)).setTextColor(getResources().getColor(R.color.cardBackground));
                    thursdayToggle = true;
                }
                weekDays.put(5, thursdayToggle);
            }
        });
        fridayCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fridayToggle) {
                    fridayToggle = false;
                    fridayCard.setBackground(getResources().getDrawable(R.color.cardBackground));
                    ((TextView)fridayCard.getChildAt(0)).setTextColor(getResources().getColor(R.color.disabled));
                } else {
                    fridayCard.setBackground(getResources().getDrawable(R.color.colorPrimaryDark));
                    ((TextView)fridayCard.getChildAt(0)).setTextColor(getResources().getColor(R.color.cardBackground));
                    fridayToggle = true;
                }
                weekDays.put(6, fridayToggle);
            }
        });
        saturdayCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (saturdayToggle) {
                    saturdayToggle = false;
                    saturdayCard.setBackground(getResources().getDrawable(R.color.cardBackground));
                    ((TextView)saturdayCard.getChildAt(0)).setTextColor(getResources().getColor(R.color.disabled));
                } else {
                    saturdayCard.setBackground(getResources().getDrawable(R.color.colorPrimaryDark));
                    ((TextView)saturdayCard.getChildAt(0)).setTextColor(getResources().getColor(R.color.cardBackground));
                    saturdayToggle = true;
                }
                weekDays.put(7, saturdayToggle);
            }
        });
        sundayCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sundayToggle) {
                    sundayToggle = false;
                    sundayCard.setBackground(getResources().getDrawable(R.color.cardBackground));
                    ((TextView)sundayCard.getChildAt(0)).setTextColor(getResources().getColor(R.color.disabled));
                } else {
                    sundayCard.setBackground(getResources().getDrawable(R.color.colorPrimaryDark));
                    ((TextView)sundayCard.getChildAt(0)).setTextColor(getResources().getColor(R.color.cardBackground));
                    sundayToggle = true;
                }
                weekDays.put(1, sundayToggle);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mondayToggle || tuesdayToggle || wednesdayToggle || thursdayToggle || fridayToggle || saturdayToggle || sundayToggle ) {
                    if (!Utils.isEditTextEmpty(nameTextView) && !devices.isEmpty() ) {
                        RoutineFB routineFB = new RoutineFB();
                        routineFB.setName(nameTextView.getText().toString());
                        routineFB.setDeviceId(selectedDevice.get_id());
                        routineFB.setBuildingId(buildingid);
                        routineFB.setAction(actionToggle.isChecked());
                        routineFB.setEnabled(true);
                        routineFB.setStartTriggered(false);
                        routineFB.setEndTriggered(false);
                        routineFB.setStartTime(startHour + ":" + startMin);
                        if (shouldStop.isChecked()) {
                            routineFB.setEndTime(endHour + ":" + endMinute);
                        }

                        routineService.createRoutineCloud(routineFB, weekDays);
                        mActivity.onBackPressed();
                    } else {
                        Toast.makeText(getContext(), "Please enter a name and make sure to choose a device", Toast.LENGTH_SHORT);
                    }
                } else {
                    Toast.makeText(getContext(), "Select at least one weekday", Toast.LENGTH_SHORT);
                }

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();


    }

}
