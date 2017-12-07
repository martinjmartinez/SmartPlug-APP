package com.example.martinjmartinez.proyectofinal.UI.Buildings.Statistics;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.martinjmartinez.proyectofinal.UI.Statistics.MonthDetails;
import com.example.martinjmartinez.proyectofinal.UI.Statistics.MonthVsMonth;

public class BuildingMonthDetailsViewPager extends FragmentStatePagerAdapter {

    private Fragment[] mFragmentList;

    public static final int GENERAL_STATISTICS = 0;
    public static final int MONTH_STATISTICS = 1;

    private String[] mTabTitlesId;

    public BuildingMonthDetailsViewPager(FragmentManager fragmentManager, Context context, String buildingId, String monthId) {
        super(fragmentManager);

        mFragmentList = new Fragment[]{
                //todo add the other one

                MonthDetails.newInstance(monthId, buildingId, "Building"),
                MonthVsMonth.newInstance(monthId, buildingId, "Building")
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
//        String title = null;
//        if (position == 0)
//        {
//            title = "General Stats";
//        }
//        else if (position == 1)
//        {
//            title = "Monthly Report";
//        }
//        return title;
//    }
}
