package com.example.cql.imzhihudaily.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.cql.imzhihudaily.LazyLoadFragment;

import java.util.List;

/**
 * Created by CQL on 2016/10/14.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private List<LazyLoadFragment> fragments;

    public ViewPagerAdapter(FragmentManager fm, List<LazyLoadFragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
