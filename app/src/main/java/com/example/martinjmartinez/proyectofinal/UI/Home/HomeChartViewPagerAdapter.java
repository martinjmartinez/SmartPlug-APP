package com.example.martinjmartinez.proyectofinal.UI.Home;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.martinjmartinez.proyectofinal.UI.Home.Charts.DevicesBarChartFragment;
import com.example.martinjmartinez.proyectofinal.UI.Home.Charts.SpacesBarChartFragment;

public class HomeChartViewPagerAdapter  extends FragmentStatePagerAdapter {

    private Fragment[] mFragmentList;

    public static final int MOST_WANTED = 0;
    public static final int COST = 1;
    public static final int PEAK_HOURS = 2;

    private String[] mTabTitlesId;

    public HomeChartViewPagerAdapter(FragmentManager fragmentManager, Context context, String buildingId) {
        super(fragmentManager);

        mFragmentList = new Fragment[]{
                SpacesBarChartFragment.newInstance(buildingId),
                DevicesBarChartFragment.newInstance(buildingId)
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

}