package com.example.martinjmartinez.proyectofinal.UI.MainActivity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.martinjmartinez.proyectofinal.Entities.Building;
import com.example.martinjmartinez.proyectofinal.Models.HistorialFB;
import com.example.martinjmartinez.proyectofinal.Models.UserFB;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Services.BuildingService;
import com.example.martinjmartinez.proyectofinal.Services.HistorialService;
import com.example.martinjmartinez.proyectofinal.Services.UserService;
import com.example.martinjmartinez.proyectofinal.UI.Buildings.Adapters.BuildingSpinnerAdapter;
import com.example.martinjmartinez.proyectofinal.UI.Buildings.Fragments.BuildingListFragment;
import com.example.martinjmartinez.proyectofinal.UI.Devices.Fragments.DeviceDetailFragment;
import com.example.martinjmartinez.proyectofinal.UI.Devices.Fragments.DeviceListFragment;
import com.example.martinjmartinez.proyectofinal.UI.Home.HomeFragment;
import com.example.martinjmartinez.proyectofinal.UI.LoginActivity.LogInActivity;
import com.example.martinjmartinez.proyectofinal.UI.Routines.Fragments.RoutinesFragment;
import com.example.martinjmartinez.proyectofinal.UI.Settings.SettingsFragment;
import com.example.martinjmartinez.proyectofinal.UI.Spaces.Fragments.SpaceDetailFragment;
import com.example.martinjmartinez.proyectofinal.UI.Spaces.Fragments.SpaceListFragment;
import com.example.martinjmartinez.proyectofinal.UI.Statistics.StatisticsFragment;
import com.example.martinjmartinez.proyectofinal.Utils.Constants;
import com.example.martinjmartinez.proyectofinal.Utils.FragmentKeys;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import io.realm.Realm;


public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private BuildingListFragment mBuildingListFragment;
    private Spinner mSelectBuildingSpinner;
    private HistorialService historialService;
    private BuildingSpinnerAdapter mBuildingSpinnerAdapter;
    private List<Building> mBuildingList;
    private Building mSelectedBuilding;
    private Activity mActivity;
    private int mLastBuildingSelected;
    private boolean doubleBackToExitPressedOnce;
    private BuildingService buildingService;
    private UserService userService;
    private DatabaseReference historiesDatabaseReference;
    private ChildEventListener historiesListener;
    private Realm realm;
    private String userId;
    private TextView userName;
    private TextView userEmail;
    private TextView logoutButton;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private BroadcastReceiver mMessageReceiver;

    public ActionBarDrawerToggle getActionBarDrawerToggle() {
        return mActionBarDrawerToggle;
    }

    public Activity getActivity() {
        return mActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initDrawerMenu();
        initVariables();
        initListeners();

        getBuildings(false);

        Intent startingIntent = getIntent();
        if (startingIntent != null) {
            String deviceId = startingIntent.getStringExtra("deviceId"); // Retrieve the id
            String spaceId = startingIntent.getStringExtra("spaceId"); // Retrieve the id
            if (deviceId != null && !deviceId.isEmpty()) {
                openDeviceInfoFragment(deviceId);
            } else if (spaceId != null && !spaceId.isEmpty()) {
                openSpaceInfoFragment(spaceId);
            }
        }
    }

    public void openDeviceInfoFragment(String deviceId) {
        DeviceDetailFragment deviceDetailFragment = (DeviceDetailFragment) getSupportFragmentManager().findFragmentByTag(FragmentKeys.DEVICE_DETAIL_FRAGMENT);
        if (deviceDetailFragment != null) {
            if (!deviceDetailFragment.isVisible()) {
                Bundle bundle = new Bundle();

                bundle.putString(Constants.DEVICE_ID, deviceId);
                deviceDetailFragment.setArguments(bundle);

                loadContentFragment(deviceDetailFragment, FragmentKeys.DEVICE_DETAIL_FRAGMENT, true);
            }
        } else {
            DeviceDetailFragment newDeviceDetailFragment = new DeviceDetailFragment();
            Bundle bundle = new Bundle();

            bundle.putString(Constants.DEVICE_ID, deviceId);
            newDeviceDetailFragment.setArguments(bundle);

            loadContentFragment(newDeviceDetailFragment, FragmentKeys.DEVICE_DETAIL_FRAGMENT, true);
        }
    }

    public void openSpaceInfoFragment(String spaceId) {
        SpaceDetailFragment spaceDetailFragment = (SpaceDetailFragment) getSupportFragmentManager().findFragmentByTag(FragmentKeys.SPACE_DETAIL_FRAGMENT);
        if (spaceDetailFragment != null) {
            if (!spaceDetailFragment.isVisible()) {
                Bundle bundle = new Bundle();

                if (mSelectedBuilding == null) {
                    mSelectedBuilding = mBuildingList.get(0);
                }
                bundle.putString(Constants.SPACE_ID, spaceId);
                spaceDetailFragment.setArguments(bundle);

                loadContentFragment(spaceDetailFragment, FragmentKeys.SPACE_DETAIL_FRAGMENT, true);
            }
        } else {
            SpaceDetailFragment spaceDetailFragment1 = new SpaceDetailFragment();
            Bundle bundle = new Bundle();

            if (mSelectedBuilding == null) {
                mSelectedBuilding = mBuildingList.get(0);
            }
            bundle.putString(Constants.SPACE_ID, spaceId);
            spaceDetailFragment1.setArguments(bundle);

            loadContentFragment(spaceDetailFragment1, FragmentKeys.SPACE_DETAIL_FRAGMENT, true);
        }
    }

    public void initVariables() {
        buildingService = new BuildingService(Realm.getDefaultInstance());
        mBuildingListFragment = new BuildingListFragment();
        mActivity = this;
        View headerView = mNavigationView.getHeaderView(0);
        mSelectBuildingSpinner = headerView.findViewById(R.id.buildings_spinner);
        realm = Realm.getDefaultInstance();
        historialService = new HistorialService(realm);
        userName = headerView.findViewById(R.id.userName);
        userEmail = headerView.findViewById(R.id.userEmail);
        logoutButton = findViewById(R.id.logout);
        userService = new UserService();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userId = getSharedPreferences(Constants.USER_INFO, 0).getString(Constants.USER_ID, user.getUid());
        userName.setText(user.getDisplayName());
        userEmail.setText(user.getEmail());
        historiesDatabaseReference = FirebaseDatabase.getInstance().getReference("Accounts/" + userId + "/Histories");

        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Extract data included in the Intent
                String message = intent.getStringExtra("message");
                String title = intent.getStringExtra("title");
                String tag = intent.getStringExtra("tag");
                final String deviceId = intent.getStringExtra("deviceId");
                final String spaceId = intent.getStringExtra("spaceId");

                //alert data here
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(MainActivity.this, R.style.Theme_AppCompat_Light_Dialog_Alert);

                builder.setTitle(title)
                        .setMessage(message)
                        .setPositiveButton(R.string.Keep_going, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(R.string.Change_settings, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete

                                if(deviceId != null && !deviceId.isEmpty()) {
                                    openDeviceInfoFragment(deviceId);
                                }else if (spaceId != null && !spaceId.isEmpty()){
                                    openSpaceInfoFragment(spaceId);
                                }
                                dialog.dismiss();
                            }
                        })
                        .setIcon(R.drawable.ic_alert)
                        .show();

            }
        };
    }

    public void setUpBuildingSpinner(List<Building> items) {
        mBuildingSpinnerAdapter = new BuildingSpinnerAdapter(this, R.layout.building_item_spinner, items);
        mSelectBuildingSpinner.setAdapter(mBuildingSpinnerAdapter);
        mSelectBuildingSpinner.setSelection(mLastBuildingSelected);
    }

    public void initDrawerMenu() {

        mToolbar = findViewById(R.id.nav_actionbar);
        mNavigationView = findViewById(R.id.design_navigation_view);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);

        setSupportActionBar(mToolbar);
        mNavigationView.setItemIconTintList(null);
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mActionBarDrawerToggle.onOptionsItemSelected(item)) {
            getBuildings(true);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(mActivity).registerReceiver(mMessageReceiver,
                new IntentFilter("notificationDialog"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(mMessageReceiver);
    }

    public void initListeners() {
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserFB userFB = new UserFB(user.getUid(), user.getDisplayName(), user.getEmail());
                userService.deleteUserFCMToken(userFB);
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(MainActivity.this, LogInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case (R.id.nav_home):
                        HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag(FragmentKeys.HOME_FRAGMENT);
                        if (homeFragment != null && !homeFragment.isVisible()) {
                            prepareHomeFragment(false);
                        }
                        mDrawerLayout.closeDrawer(Gravity.START);
                        break;

                    case (R.id.nav_spaces):
                        SpaceListFragment spaceListFragment = (SpaceListFragment) getSupportFragmentManager().findFragmentByTag(FragmentKeys.SPACE_LIST_FRAGMENT);
                        if (spaceListFragment != null) {
                            if (!spaceListFragment.isVisible()) {
                                prepareSpaceListFragment();
                            }
                        } else {
                            prepareSpaceListFragment();
                        }
                        mDrawerLayout.closeDrawer(Gravity.START);
                        break;

                    case (R.id.nav_devices):
                        DeviceListFragment deviceListFragment = (DeviceListFragment) getSupportFragmentManager().findFragmentByTag(FragmentKeys.DEVICE_LIST_FRAGMENT);
                        if (deviceListFragment != null) {
                            if (!deviceListFragment.isVisible()) {
                                prepareDeviceFragment(false);
                            }
                        } else {
                            prepareDeviceFragment(true);
                        }
                        mDrawerLayout.closeDrawer(Gravity.START);
                        break;

                    case (R.id.nav_buildings):
                        BuildingListFragment buildingListFragment = (BuildingListFragment) getSupportFragmentManager().findFragmentByTag(FragmentKeys.BUILDING_LIST_FRAGMENT);
                        if (buildingListFragment != null) {
                            if (!buildingListFragment.isVisible()) {
                                loadContentFragment(buildingListFragment, FragmentKeys.BUILDING_LIST_FRAGMENT, false);
                            }
                        } else {
                            loadContentFragment(mBuildingListFragment, FragmentKeys.BUILDING_LIST_FRAGMENT, true);
                        }

                        mDrawerLayout.closeDrawer(Gravity.START);
                        break;

                    case (R.id.nav_statistics):
                        StatisticsFragment statisticsFragment = (StatisticsFragment) getSupportFragmentManager().findFragmentByTag(FragmentKeys.STATISTICS_FRAGMENT);
                        if (statisticsFragment != null) {
                            if (!statisticsFragment.isVisible()) {
                                prepareStatisticsFragment(false);
                            }
                        } else {
                            prepareStatisticsFragment(true);
                        }

                        mDrawerLayout.closeDrawer(Gravity.START);
                        break;

                    case (R.id.nav_settings):
                        SettingsFragment settingsFragment= (SettingsFragment) getSupportFragmentManager().findFragmentByTag(FragmentKeys.SETTINGS_FRAGMENT);
                        if (settingsFragment != null) {
                            if (!settingsFragment.isVisible()) {
                                prepareSettingsFragment(false);
                            }
                        } else {
                            prepareSettingsFragment(true);
                        }

                        mDrawerLayout.closeDrawer(Gravity.START);
                        break;

                    case (R.id.nav_routines):
                        RoutinesFragment routinesFragment= (RoutinesFragment) getSupportFragmentManager().findFragmentByTag(FragmentKeys.ROUTINES_FRAGMENT);
                        if (routinesFragment != null) {
                            if (!routinesFragment.isVisible()) {
                                prepareRoutinesFragment(false);
                            }
                        } else {
                            prepareRoutinesFragment(true);
                        }

                        mDrawerLayout.closeDrawer(Gravity.START);
                        break;
                }
                return false;
            }
        });


        mSelectBuildingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mSelectedBuilding == null) {
                    mSelectedBuilding = mBuildingList.get(position);
                    mLastBuildingSelected = position;
                }

                if (!mSelectedBuilding.get_id().equals(mBuildingList.get(position).get_id())) {
                    mLastBuildingSelected = position;
                    mSelectedBuilding = mBuildingList.get(position);
                    SpaceListFragment spaceListFragment = (SpaceListFragment) getSupportFragmentManager().findFragmentByTag(FragmentKeys.SPACE_LIST_FRAGMENT);
                    HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag(FragmentKeys.HOME_FRAGMENT);

                    if (spaceListFragment != null && spaceListFragment.isVisible()) {
                        prepareSpaceListFragment();
                    } else if (homeFragment != null && !homeFragment.isDetached()) {
                        prepareHomeFragment(false);
                    }
                }
                mBuildingSpinnerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        historiesListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                HistorialFB historialFB = dataSnapshot.getValue(HistorialFB.class);

                if (historialFB != null) {
                    historialService.updateOrCreate(historialFB);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                HistorialFB historialFB = dataSnapshot.getValue(HistorialFB.class);

                if (historialFB != null) {
                    historialService.updateOrCreate(historialFB);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("MainActivity", "Historial Listener set");
        historiesDatabaseReference.addChildEventListener(historiesListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("MainActivity", "Historial Listener remove");
        historiesDatabaseReference.removeEventListener(historiesListener);
    }

    public void prepareSpaceListFragment() {
        SpaceListFragment spaceListFragment = new SpaceListFragment();
        Bundle bundle = new Bundle();

        bundle.putString(Constants.BUILDING_ID, mSelectedBuilding.get_id());
        spaceListFragment.setArguments(bundle);

        loadContentFragment(spaceListFragment, FragmentKeys.SPACE_LIST_FRAGMENT, true);
    }

    public void prepareHomeFragment(boolean addToBackStack) {
        HomeFragment homeFragment = new HomeFragment();
        Bundle bundle = new Bundle();

        if (mSelectedBuilding == null) {
            mSelectedBuilding = mBuildingList.get(0);
        }
        bundle.putString(Constants.BUILDING_ID, mSelectedBuilding.get_id());
        homeFragment.setArguments(bundle);

        loadContentFragment(homeFragment, FragmentKeys.HOME_FRAGMENT, addToBackStack);
    }

    public void prepareStatisticsFragment(boolean addToBackStack) {
        StatisticsFragment statisticsFragment = new StatisticsFragment();
        Bundle bundle = new Bundle();

        if (mSelectedBuilding == null) {
            mSelectedBuilding = mBuildingList.get(0);
        }
        bundle.putString(Constants.BUILDING_ID, mSelectedBuilding.get_id());
        statisticsFragment.setArguments(bundle);

        loadContentFragment(statisticsFragment, FragmentKeys.STATISTICS_FRAGMENT, addToBackStack);
    }

    public void prepareSettingsFragment(boolean addToBackStack) {
        SettingsFragment settingsFragment = new SettingsFragment();
        Bundle bundle = new Bundle();

        if (mSelectedBuilding == null) {
            mSelectedBuilding = mBuildingList.get(0);
        }
        bundle.putString(Constants.BUILDING_ID, mSelectedBuilding.get_id());
        settingsFragment.setArguments(bundle);

        loadContentFragment(settingsFragment, FragmentKeys.SETTINGS_FRAGMENT, addToBackStack);
    }

    public void prepareRoutinesFragment(boolean addToBackStack) {
        RoutinesFragment routinesFragment = new RoutinesFragment();
        Bundle bundle = new Bundle();

        if (mSelectedBuilding == null) {
            mSelectedBuilding = mBuildingList.get(0);
        }
        bundle.putString(Constants.BUILDING_ID, mSelectedBuilding.get_id());
        routinesFragment.setArguments(bundle);

        loadContentFragment(routinesFragment, FragmentKeys.ROUTINES_FRAGMENT, addToBackStack);
    }

    public void prepareDeviceFragment(boolean addToBackStack) {
        DeviceListFragment deviceListFragment = new DeviceListFragment();
        Bundle bundle = new Bundle();

        bundle.putString(Constants.BUILDING_ID, mSelectedBuilding.get_id());
        deviceListFragment.setArguments(bundle);

        loadContentFragment(deviceListFragment, FragmentKeys.DEVICE_LIST_FRAGMENT, addToBackStack);
    }

    public void loadContentFragment(final Fragment fragment, String fragment_key, boolean addToStack) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.frame_layout, fragment, fragment_key);

        if (addToStack) {
            fragmentTransaction.addToBackStack(fragment_key);
        } else {
            fragment.getFragmentManager().popBackStack();
            fragmentTransaction.addToBackStack(fragment_key);
        }

        fragmentTransaction.commitAllowingStateLoss();
    }

    public void getBuildings(final boolean refreshSpinner) {
        mBuildingList = buildingService.allActiveBuildings();

        if (!mBuildingList.isEmpty()) {
            if (refreshSpinner) {
                setUpBuildingSpinner(mBuildingList);
            } else {
                prepareHomeFragment(false);
            }
        } else {
            loadContentFragment(mBuildingListFragment, FragmentKeys.BUILDING_LIST_FRAGMENT, false);
        }

    }

    public void toggleDrawerIcon(boolean status, int icon, View.OnClickListener onClickListener) {
        if (status) {
            initDrawerMenu();

            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        } else {
            mActionBarDrawerToggle.setHomeAsUpIndicator(icon);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            mToolbar.setNavigationOnClickListener(onClickListener);
            mActionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, R.string.press_back_message, Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            super.onBackPressed();
        }
    }
}


