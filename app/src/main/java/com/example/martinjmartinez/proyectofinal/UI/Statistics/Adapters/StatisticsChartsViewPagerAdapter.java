package com.example.martinjmartinez.proyectofinal.UI.Statistics.Adapters;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.UI.Statistics.Charts.BuildingsLineChartFragment;
import com.example.martinjmartinez.proyectofinal.UI.Statistics.Charts.DevicesLineChartFragment;
import com.example.martinjmartinez.proyectofinal.UI.Statistics.Charts.SpacesLineChartFragment;

import java.util.Date;


public class StatisticsChartsViewPagerAdapter extends FragmentStatePagerAdapter {

    private Fragment[] mFragmentList;

    public static final int MOST_WANTED = 0;
    public static final int COST = 1;
    public static final int PEAK_HOURS = 2;

    private String[] mTabTitlesId;

    public StatisticsChartsViewPagerAdapter(FragmentManager fragmentManager, Context context, String buildingId, Date startDate, Date endDate) {
        super(fragmentManager);

        mFragmentList = new Fragment[]{
                BuildingsLineChartFragment.newInstance(buildingId, startDate, endDate),
                SpacesLineChartFragment.newInstance(buildingId, startDate, endDate),
                DevicesLineChartFragment.newInstance(buildingId, startDate, endDate)
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
