package com.example.martinjmartinez.proyectofinal.UI;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;

import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.UI.Buildings.BuildingListFragment;
import com.example.martinjmartinez.proyectofinal.UI.Devices.DeviceListFragment;
import com.example.martinjmartinez.proyectofinal.UI.Spaces.SpaceListFragment;
import com.example.martinjmartinez.proyectofinal.Utils.API;
import com.example.martinjmartinez.proyectofinal.Utils.FragmentKeys;


public class MainActivity extends AppCompatActivity {


    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private SpaceListFragment mSpaceListFragment;
    private DeviceListFragment mDeviceListFragment;
    private BuildingListFragment mBuildingListFragment;
    private API api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initDrawerMenu();

        mSpaceListFragment = new SpaceListFragment();
        mDeviceListFragment = new DeviceListFragment();
        mBuildingListFragment = new BuildingListFragment();
        api = new API();

        initListeners();

    }

    public void initDrawerMenu () {

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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void initListeners() {
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case (R.id.nav_spaces):
                        loadContentFragment(mSpaceListFragment, FragmentKeys.SPACE_LIST_FRAGMENT);
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
    }

    public void loadContentFragment(final Fragment fragment, String fragment_key) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.frame_layout, fragment, fragment_key);

        fragmentTransaction.commitAllowingStateLoss();
    }








}
