package com.example.martinjmartinez.proyectofinal.UI.Buildings.Statistics;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.martinjmartinez.proyectofinal.UI.Statistics.Charts.Building.BuildingConsumptionLineChartFragment;
import com.example.martinjmartinez.proyectofinal.UI.Statistics.Charts.Building.BuildingPowerLineChartFragment;
import com.example.martinjmartinez.proyectofinal.UI.Statistics.Charts.Device.DeviceConsumptionLineChartFragment;
import com.example.martinjmartinez.proyectofinal.UI.Statistics.Charts.Device.DevicePowerLineChartFragment;
import com.example.martinjmartinez.proyectofinal.UI.Statistics.Charts.Device.DeviceTimeLinechartFragment;

import java.util.Date;

public class BuildingChartsStatisticsViewPager extends FragmentStatePagerAdapter {

    private Fragment[] mFragmentList;

    private String[] mTabTitlesId;

    public BuildingChartsStatisticsViewPager(FragmentManager fragmentManager, Context context, String deviceId, Date startDate, Date endDate) {
        super(fragmentManager);

        mFragmentList = new Fragment[]{
                BuildingConsumptionLineChartFragment.newInstance(deviceId, startDate, endDate),
                BuildingPowerLineChartFragment.newInstance(deviceId, startDate, endDate)
        };
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList[position];
    }

    @Override
    public int getCount() {
        return mFragmentList.length;
    }

//    @Override
//    public CharSequence getPageTitle(int position) {
//        return mTabTitlesId[position];
//    }
}