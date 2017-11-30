package com.example.martinjmartinez.proyectofinal.UI.Statistics.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.martinjmartinez.proyectofinal.UI.Statistics.ChartDetails.BuildingChartDetailsFragment;
import com.example.martinjmartinez.proyectofinal.UI.Statistics.ChartDetails.DevicesChartDetailsFragment;
import com.example.martinjmartinez.proyectofinal.UI.Statistics.ChartDetails.SpacesChartDetailsFragment;

import java.util.Date;

public class StatisticsChartDetailsViewPagerAdapter extends FragmentStatePagerAdapter {

    private Fragment[] mFragmentList;

    public static final int MOST_WANTED = 0;
    public static final int COST = 1;
    public static final int PEAK_HOURS = 2;

    private String[] mTabTitlesId;

    public StatisticsChartDetailsViewPagerAdapter(FragmentManager fragmentManager, Context context, String buildingId, Date startDate, Date endDate) {
        super(fragmentManager);

        mFragmentList = new Fragment[]{
                BuildingChartDetailsFragment.newInstance(buildingId, startDate, endDate),
                SpacesChartDetailsFragment.newInstance(buildingId, startDate, endDate),
                DevicesChartDetailsFragment.newInstance(buildingId, startDate, endDate)
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