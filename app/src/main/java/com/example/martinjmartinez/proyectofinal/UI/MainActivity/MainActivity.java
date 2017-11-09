package com.example.martinjmartinez.proyectofinal.UI.MainActivity;

import android.app.Activity;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.martinjmartinez.proyectofinal.Entities.Building;
import com.example.martinjmartinez.proyectofinal.Entities.Historial;
import com.example.martinjmartinez.proyectofinal.Models.HistorialFB;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Services.BuildingService;
import com.example.martinjmartinez.proyectofinal.Services.HistorialService;
import com.example.martinjmartinez.proyectofinal.UI.Buildings.Adapters.BuildingSpinnerAdapter;
import com.example.martinjmartinez.proyectofinal.UI.Buildings.Fragments.BuildingListFragment;
import com.example.martinjmartinez.proyectofinal.UI.Devices.Fragments.DeviceListFragment;
import com.example.martinjmartinez.proyectofinal.UI.Home.HomeFragment;
import com.example.martinjmartinez.proyectofinal.UI.Spaces.Fragments.SpaceListFragment;
import com.example.martinjmartinez.proyectofinal.UI.Statistics.Charts.BuildingsLineChartFragment;
import com.example.martinjmartinez.proyectofinal.UI.Statistics.StatisticsFragment;
import com.example.martinjmartinez.proyectofinal.Utils.Constants;
import com.example.martinjmartinez.proyectofinal.Utils.FragmentKeys;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    private DatabaseReference historiesDatabaseReference;
    private ValueEventListener historiesListener;
    private Realm realm;

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

    }

    public void initVariables() {
        buildingService = new BuildingService(Realm.getDefaultInstance());
        mBuildingListFragment = new BuildingListFragment();
        mActivity = this;
        View headerView = mNavigationView.getHeaderView(0);
        mSelectBuildingSpinner = (Spinner) headerView.findViewById(R.id.buildings_spinner);
        realm = Realm.getDefaultInstance();
        historialService = new HistorialService(realm);
        historiesDatabaseReference = FirebaseDatabase.getInstance().getReference("Histories");
    }

    public void setUpBuildingSpinner(List<Building> items) {
        mBuildingSpinnerAdapter = new BuildingSpinnerAdapter(this, R.layout.building_item_spinner, items);
        mSelectBuildingSpinner.setAdapter(mBuildingSpinnerAdapter);
        mSelectBuildingSpinner.setSelection(mLastBuildingSelected);
    }

    public void initDrawerMenu() {

        mToolbar = (Toolbar) findViewById(R.id.nav_actionbar);
        mNavigationView = (NavigationView) findViewById(R.id.design_navigation_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
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

    public void initListeners() {
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

        historiesListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Log.e("MainActivity", "Historial Listener");
                    HistorialFB historialFB = dataSnapshot1.getValue(HistorialFB.class);

                    if (historialFB != null) {
                       historialService.updateOrCreate(historialFB);
                    }
                }
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
        historiesDatabaseReference.addValueEventListener(historiesListener);
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
            fragmentTransaction.addToBackStack(null);
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
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

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


