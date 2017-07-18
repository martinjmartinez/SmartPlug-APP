package com.example.martinjmartinez.proyectofinal.UI.MainActivity;

import android.app.Activity;
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

import com.example.martinjmartinez.proyectofinal.Entities.Building;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.UI.Buildings.Adapters.BuildingSpinnerAdapter;
import com.example.martinjmartinez.proyectofinal.UI.Buildings.Fragments.BuildingCreateFragment;
import com.example.martinjmartinez.proyectofinal.UI.Buildings.Fragments.BuildingListFragment;
import com.example.martinjmartinez.proyectofinal.UI.Devices.Fragments.DeviceListFragment;
import com.example.martinjmartinez.proyectofinal.UI.Spaces.Fragments.SpaceListFragment;
import com.example.martinjmartinez.proyectofinal.Utils.API;
import com.example.martinjmartinez.proyectofinal.Utils.ArgumentsKeys;
import com.example.martinjmartinez.proyectofinal.Utils.FragmentKeys;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {


    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private DeviceListFragment mDeviceListFragment;
    private BuildingListFragment mBuildingListFragment;
    private BuildingCreateFragment mBuildingCreateFragment;
    private Spinner mSelectBuildingSpinner;
    private BuildingSpinnerAdapter mBuildingSpinnerAdapter;
    private API mAPI;
    private List<Building> mBuildingList;
    private Building mSelectedBuilding;
    private Activity mActivity;
    private int mLastBuildingSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDrawerMenu();
        initVariables();
        initListeners();

        getBuildings(mAPI.getClient());

    }


    public void initVariables() {
        mDeviceListFragment = new DeviceListFragment();
        mBuildingListFragment = new BuildingListFragment();
        mBuildingCreateFragment = new BuildingCreateFragment();
        mAPI = new API();
        mActivity = this;
        View headerView = mNavigationView.getHeaderView(0);
        mSelectBuildingSpinner = (Spinner) headerView.findViewById(R.id.buildings_spinner);
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
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close);

        setSupportActionBar(mToolbar);
        mNavigationView.setItemIconTintList(null);
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mActionBarDrawerToggle.onOptionsItemSelected(item)) {
            getBuildings(mAPI.getClient());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void initListeners() {
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case (R.id.nav_spaces):
                        prepareSpaceListFragment();
                        mDrawerLayout.closeDrawer(Gravity.START);
                        break;

                    case (R.id.nav_devices):
                        loadContentFragment(mDeviceListFragment, FragmentKeys.DEVICE_LIST_FRAGMENT);
                        mDrawerLayout.closeDrawer(Gravity.START);
                        break;

                    case (R.id.nav_buildings):
                        loadContentFragment(mBuildingListFragment, FragmentKeys.BUILDING_LIST_FRAGMENT);
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
                    if (spaceListFragment != null && spaceListFragment.isVisible()) {
                        prepareSpaceListFragment();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void prepareSpaceListFragment() {
        SpaceListFragment spaceListFragment = new SpaceListFragment();
        Bundle bundle = new Bundle();

        bundle.putString(ArgumentsKeys.BUILDING_ID, mSelectedBuilding.get_id());
        spaceListFragment.setArguments(bundle);

        loadContentFragment(spaceListFragment, FragmentKeys.SPACE_LIST_FRAGMENT);
    }

    public void loadContentFragment(final Fragment fragment, String fragment_key) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.frame_layout, fragment, fragment_key);

        fragmentTransaction.commitAllowingStateLoss();
    }

    public void getBuildings(OkHttpClient client) {
        Request request = new Request.Builder()
                .url(ArgumentsKeys.BUILDING_QUERY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Error", e.getMessage());
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    mBuildingList = mAPI.getBuildingList(response);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setUpBuildingSpinner(mBuildingList);
                        }
                    });
                }
            }
        });
    }

}


