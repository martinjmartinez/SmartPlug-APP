package com.example.martinjmartinez.proyectofinal.UI.Buildings.Statistics;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.martinjmartinez.proyectofinal.R;

public class BuildingStatisticsViewPagerAdapter extends FragmentStatePagerAdapter {
    private Fragment[] mFragmentList;

    public static final int GENERAL_STATISTICS = 0;
    public static final int MONTH_STATISTICS = 1;
    private Context context;
    private String[] mTabTitlesId;

    public BuildingStatisticsViewPagerAdapter(FragmentManager fragmentManager, Context context, String buildingId) {
        super(fragmentManager);

        this.context = context;

        mFragmentList = new Fragment[]{
                BuildingStatisticsDetailsFragment.newInstance(buildingId),
                BuildingMonthlyLimits.newInstance(buildingId)
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
        String title = null;
        if (position == 0)
        {
            title = context.getResources().getString(R.string.general_stats);
        }
        else if (position == 1)
        {
            title = context.getString(R.string.monthly_report);
        }
        return title;
    }
}
