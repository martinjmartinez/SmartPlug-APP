package com.example.martinjmartinez.proyectofinal.UI.Spaces.Statistics;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.martinjmartinez.proyectofinal.UI.Statistics.Charts.Space.SpaceConsumptionLineChartFragment;
import com.example.martinjmartinez.proyectofinal.UI.Statistics.Charts.Space.SpacePowerLineChartFragment;

import java.util.Date;

public class SpaceChartsStatisticsViewPager extends FragmentStatePagerAdapter {

    private Fragment[] mFragmentList;

    private String[] mTabTitlesId;

    public SpaceChartsStatisticsViewPager(FragmentManager fragmentManager, String spaceId, Date startDate, Date endDate) {
        super(fragmentManager);

        mFragmentList = new Fragment[]{
                SpaceConsumptionLineChartFragment.newInstance(spaceId, startDate, endDate),
                SpacePowerLineChartFragment.newInstance(spaceId, startDate, endDate)
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
