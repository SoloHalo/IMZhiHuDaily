package com.example.cql.imzhihudaily.view.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.cql.imzhihudaily.BaseApplication;
import com.example.cql.imzhihudaily.BaseFragment;
import com.example.cql.imzhihudaily.R;
import com.example.cql.imzhihudaily.adapter.PartitionRecycleViewAdapter;
import com.example.cql.imzhihudaily.data.RetrofitManager;
import com.example.cql.imzhihudaily.data.bean.ThemeInfo;
import com.example.cql.imzhihudaily.view.activity.IDrawerLayoutActivity;
import com.example.cql.imzhihudaily.view.activity.ParNewsDetailActivity;
import com.example.cql.imzhihudaily.widget.LoadMoreRecyclerView;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by CQL on 2016/10/16.
 */

public class PartitionFragment extends BaseFragment implements LoadMoreRecyclerView.OnLoadMoreListener,
        SwipeRefreshLayout.OnRefreshListener, LoadMoreRecyclerView.OnLoadMoreViewClickListener, PartitionRecycleViewAdapter.OnItemClickListener {
    public static final String TAG = "PartitionFragment";
    private static final String EXTRA_ID = "id";
    private LoadMoreRecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Toolbar toolbar;
    private PartitionRecycleViewAdapter adapter;
    private ThemeInfo mThemeInfo;

    private int lastId;

    public static PartitionFragment newInstance(int id) {
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_ID,id);
        PartitionFragment fragment = new PartitionFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initView(View view) {
        toolbar = (Toolbar) view.findViewById(R.id.daily_psychology_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof IDrawerLayoutActivity) {
                    ((IDrawerLayoutActivity) getActivity()).openDrawer();
                }
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.daily_psychology_swipe);
        swipeRefreshLayout.setOnRefreshListener(this);

        adapter = new PartitionRecycleViewAdapter(getContext());
        adapter.setOnItemClickListener(this);
        recyclerView = (LoadMoreRecyclerView) view.findViewById(R.id.daily_psychology_recycle);
        recyclerView.setEnableLoadMore(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(BaseApplication.getApplication());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setOnLoadMoreListener(this);
        recyclerView.setShowLoadingView(false);
        recyclerView.setOnLoadMoreViewClickListener(this);

        loadData();
    }

    private void loadData(){
        int id = getArguments().getInt(EXTRA_ID);
        RetrofitManager.builder().getThemeNews(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ThemeInfo>() {
                    @Override
                    public void call(ThemeInfo themeInfo) {
                        swipeRefreshLayout.setRefreshing(false);
                        adapter.clearData();
                        mThemeInfo = themeInfo;
                        toolbar.setTitle(themeInfo.getName());
                        adapter.addData(themeInfo);
                        adapter.notifyDataSetChanged();
                        lastId = themeInfo.getStories().get(themeInfo.getStories().size() - 1).getId();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        recyclerView.setLoadView("加载失败,点击重试", false);
                        recyclerView.setEnableLoadMore(false);
                        recyclerView.setShowLoadingView(true);
                    }
                });
//        NewsService.getInstance().getThemeNews(id, new JsonCallback<ThemeInfo>() {
//            @Override
//            protected void onResponse(ThemeInfo response) {
//                swipeRefreshLayout.setRefreshing(false);
//                adapter.clearData();
//                mThemeInfo = response;
//                toolbar.setTitle(mThemeInfo.getName());
//                adapter.addData(mThemeInfo);
//                adapter.notifyDataSetChanged();
//                lastId = mThemeInfo.getStories().get(mThemeInfo.getStories().size() - 1).getId();
//            }
//
//            @Override
//            protected void onFailure(Throwable t) {
//                recyclerView.setLoadView("加载失败,点击重试", false);
//                recyclerView.setEnableLoadMore(false);
//                recyclerView.setShowLoadingView(true);
//            }
//        });
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_partition_page;
    }

    @Override
    public void onLoadMore() {
        int  id = getArguments().getInt(EXTRA_ID);
        RetrofitManager.builder().getThemeOldNews(id,lastId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ThemeInfo>() {
                    @Override
                    public void call(ThemeInfo themeInfo) {
                        mThemeInfo = themeInfo;
                        recyclerView.setLoading(false);
                        int newItemSize = mThemeInfo.getStories().size();
                        if (newItemSize == 0) {
                            recyclerView.setLoadView("没有更多了", false);
                            recyclerView.setEnableLoadMore(false);
                            recyclerView.setShowLoadingView(true);
                        } else {
                            adapter.addData(mThemeInfo);
                            adapter.notifyDataSetChanged();
                            lastId = mThemeInfo.getStories().get(mThemeInfo.getStories().size() - 1).getId();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        recyclerView.setLoading(false);
                        recyclerView.setLoadView("加载失败,点击重试", false);
                        recyclerView.setEnableLoadMore(false);
                        recyclerView.setShowLoadingView(true);
                    }
                });
//        NewsService.getInstance().getThemeOldNews(id, lastId, new JsonCallback<ThemeInfo>() {
//            @Override
//            protected void onResponse(ThemeInfo response) {
//                mThemeInfo = response;
//                recyclerView.setLoading(false);
//                int newItemSize = mThemeInfo.getStories().size();
//                if (newItemSize == 0){
//                    recyclerView.setLoadView("没有更多了",false);
//                    recyclerView.setEnableLoadMore(false);
//                    recyclerView.setShowLoadingView(true);
//                }else {
//                    adapter.addData(mThemeInfo);
//                    adapter.notifyDataSetChanged();
//                    lastId = mThemeInfo.getStories().get(mThemeInfo.getStories().size() - 1).getId();
//                }
//            }
//
//            @Override
//            protected void onFailure(Throwable t) {
//                recyclerView.setLoading(false);
//                recyclerView.setLoadView("加载失败,点击重试", false);
//                recyclerView.setEnableLoadMore(false);
//                recyclerView.setShowLoadingView(true);
//            }
//        });
    }

    @Override
    public void onRefresh() {
        recyclerView.setEnableLoadMore(true);
        recyclerView.setShowLoadingView(false);
        loadData();
    }

    @Override
    public void onLoadMoreViewClick() {

    }

    @Override
    public void onItemClick(int id) {
        ParNewsDetailActivity.startActivity(id,getContext());
    }

}
