package com.example.martinjmartinez.proyectofinal.UI.Statistics;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.UI.Statistics.Charts.BuildingsChartFragment;
import com.example.martinjmartinez.proyectofinal.UI.Statistics.Charts.DevicesChartFragment;
import com.example.martinjmartinez.proyectofinal.UI.Statistics.Charts.SpacesChartFragment;

import java.util.Date;


public class ChartsViewPagerAdapter extends FragmentStatePagerAdapter {

    private Fragment[] mFragmentList;

    public static final int MOST_WANTED = 0;
    public static final int COST = 1;
    public static final int PEAK_HOURS = 2;

    private String[] mTabTitlesId;

    public ChartsViewPagerAdapter(FragmentManager fragmentManager, Context context, String buildingId, Date startDate, Date endDate) {
        super(fragmentManager);

        mTabTitlesId = context.getResources().getStringArray(R.array.date_filters_array);
        mFragmentList = new Fragment[]{
                BuildingsChartFragment.newInstance(buildingId, startDate, endDate),
                SpacesChartFragment.newInstance(buildingId, startDate, endDate),
                DevicesChartFragment.newInstance(buildingId, startDate, endDate)
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

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabTitlesId[position];
    }
}
