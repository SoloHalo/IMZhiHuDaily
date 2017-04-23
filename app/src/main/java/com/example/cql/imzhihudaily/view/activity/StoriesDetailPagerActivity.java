package com.example.cql.imzhihudaily.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.cql.imzhihudaily.LazyLoadFragment;
import com.example.cql.imzhihudaily.R;
import com.example.cql.imzhihudaily.adapter.ViewPagerAdapter;
import com.example.cql.imzhihudaily.view.fragment.StoriesFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CQL on 2016/10/11.
 */

public class StoriesDetailPagerActivity extends AppCompatActivity {
    public static final String EXTRA_ID = "ids";
    public static final String EXTRA_CURRENT_ID ="currentId";
    private ViewPager mViewPager;
    private List<String> mIds;
    private List<LazyLoadFragment> fragments;
    private String mCurrentId;
    private ViewPagerAdapter adapter;

    public static void startActivity(Context context,ArrayList<String> ids,String currentId){
        Intent intent = new Intent(context, StoriesDetailPagerActivity.class);
        intent.putExtra(EXTRA_ID, ids);
        intent.putExtra(EXTRA_CURRENT_ID, currentId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mIds = getIntent().getStringArrayListExtra(EXTRA_ID);
        mViewPager.setOffscreenPageLimit(mIds.size());
        mCurrentId = getIntent().getStringExtra(EXTRA_CURRENT_ID);

        fragments = new ArrayList<>();
        for (int i = 0; i< mIds.size(); i++){
            fragments.add(StoriesFragment.newInstance(mIds.get(i)));
        }
        adapter = new ViewPagerAdapter(getSupportFragmentManager(),fragments);
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(mIds.indexOf(mCurrentId));
    }
}
