package com.example.martinjmartinez.proyectofinal.UI.Devices.Statistics;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.martinjmartinez.proyectofinal.UI.Statistics.Charts.Device.DeviceConsumptionLineChartFragment;
import com.example.martinjmartinez.proyectofinal.UI.Statistics.Charts.Device.DevicePowerLineChartFragment;
import com.example.martinjmartinez.proyectofinal.UI.Statistics.Charts.Device.DeviceTimeLinechartFragment;

import java.util.Date;

public class DeviceChartsStatisticsViewPager extends FragmentStatePagerAdapter {

    private Fragment[] mFragmentList;

    private String[] mTabTitlesId;

    public DeviceChartsStatisticsViewPager(FragmentManager fragmentManager, Context context, String deviceId, Date startDate, Date endDate) {
        super(fragmentManager);

        mFragmentList = new Fragment[]{
                DeviceConsumptionLineChartFragment.newInstance(deviceId, startDate, endDate),
                DevicePowerLineChartFragment.newInstance(deviceId, startDate, endDate),
                DeviceTimeLinechartFragment.newInstance(deviceId, startDate, endDate)
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