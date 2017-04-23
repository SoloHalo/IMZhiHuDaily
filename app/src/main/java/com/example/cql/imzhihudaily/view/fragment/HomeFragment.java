package com.example.cql.imzhihudaily.view.fragment;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import com.example.cql.imzhihudaily.BaseApplication;
import com.example.cql.imzhihudaily.BaseFragment;
import com.example.cql.imzhihudaily.R;
import com.example.cql.imzhihudaily.adapter.HomeRecycleViewAdapter;
import com.example.cql.imzhihudaily.data.RetrofitManager;
import com.example.cql.imzhihudaily.data.bean.HomeInfo;
import com.example.cql.imzhihudaily.util.StatusBarUtils;
import com.example.cql.imzhihudaily.view.activity.IDrawerLayoutActivity;
import com.example.cql.imzhihudaily.view.activity.StoriesDetailPagerActivity;
import com.example.cql.imzhihudaily.widget.LoadMoreRecyclerView;

import java.util.ArrayList;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by CQL on 2016/10/15.
 */

public class HomeFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, HomeRecycleViewAdapter.onItemClickListener, LoadMoreRecyclerView.OnLoadMoreListener, LoadMoreRecyclerView.OnLoadMoreViewClickListener {
    private static final int INTERVAL_DAY = 24 * 60 * 60 * 1000;
    public  static final String TAG = "HomeFragment";

    LoadMoreRecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Toolbar toolbar;
    private HomeRecycleViewAdapter adapter;
    private HomeInfo mHomeInfo;

    private long mLastLoadTime = -1;

    public static HomeFragment newInstance() {

        Bundle args = new Bundle();
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    protected void initView(View view) {
        initToolbar(view);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.home_swipe);
        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView = (LoadMoreRecyclerView) view.findViewById(R.id.recycle_view);
        recyclerView.setEnableLoadMore(true);
        adapter = new HomeRecycleViewAdapter(getContext());
        adapter.setOnItemClickListener(this);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(BaseApplication.getApplication());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setOnLoadMoreListener(this);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setClickable(true);
        recyclerView.setOnLoadMoreViewClickListener(this);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int position = layoutManager.findFirstCompletelyVisibleItemPosition();
                String str = adapter.findLatestDateViewString(position - 1);
                if (!toolbar.getTitle().equals(str)) {//此处判断是避免重复的设置标题
                    if (str != null) {
                        toolbar.setTitle(str);
                    } else {
                        toolbar.setTitle("首页");
                    }
                }
            }
        });

        loadData();
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_home_page;
    }

    private void initToolbar(View view) {
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        final NavigationView mDrawer = (NavigationView) getActivity().findViewById(R.id.drawer);
        ViewGroup mContainer = (ViewGroup) view.findViewById(R.id.container);
        StatusBarUtils.setDrawerToolbarLayout(getActivity(), mContainer, toolbar, mDrawer);
        toolbar.setTitle(R.string.current_title);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof IDrawerLayoutActivity) {
                    ((IDrawerLayoutActivity) getActivity()).openDrawer();
                }
            }
        });
        toolbar.inflateMenu(R.menu.home_page_menu);
    }

//    private void loadData() {
//        NewsService.getInstance().getLatestNews(new JsonCallback<HomeInfo>() {
//            @Override
//            protected void onResponse(HomeInfo response) {
//                swipeRefreshLayout.setRefreshing(false);
//                adapter.clearData();
//                mHomeInfo = response;
//                adapter.addData(mHomeInfo);
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            protected void onFailure(Throwable t) {
//                swipeRefreshLayout.setRefreshing(false);
//                recyclerView.setLoadView("加载失败,点击重试", false);
//                recyclerView.setEnableLoadMore(false);
//            }
//        });
//    }

    private void loadData(){
        RetrofitManager.builder().getLatestNews()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<HomeInfo>() {
                    @Override
                    public void call(HomeInfo homeInfo) {
                        swipeRefreshLayout.setRefreshing(false);
                        adapter.clearData();
                        mHomeInfo = homeInfo;
                        adapter.addData(mHomeInfo);
                        adapter.notifyDataSetChanged();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        swipeRefreshLayout.setRefreshing(false);
                        recyclerView.setLoadView("加载失败,点击重试", false);
                        recyclerView.setEnableLoadMore(false);
                    }
                });
    }

    @Override
    public void onRefresh() {
        loadData();
    }

    @Override
    public void onItemClick(ArrayList<String> mIds, String currentId) {
        StoriesDetailPagerActivity.startActivity(getContext(),mIds,currentId);
    }

    @Override
    public void onLoadMore() {
        if (mLastLoadTime < 0) {
            mLastLoadTime = System.currentTimeMillis();
        }
        RetrofitManager.builder().getOldNews(mLastLoadTime)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HomeInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        recyclerView.setLoading(false);
                        recyclerView.setLoadView("加载失败,点击重试", false);
                        recyclerView.setEnableLoadMore(false);
                        recyclerView.setShowLoadingView(true);
                    }

                    @Override
                    public void onNext(HomeInfo homeInfo) {
                        mHomeInfo = homeInfo;
                        adapter.addData(mHomeInfo);
                        adapter.notifyDataSetChanged();
                        recyclerView.setShowLoadingView(false);
                        recyclerView.setLoading(false);
                        mLastLoadTime -= INTERVAL_DAY;
                    }
                });
//        NewsService.getInstance().getOlderNews(mLastLoadTime, new JsonCallback<HomeInfo>() {
//            @Override
//            protected void onResponse(HomeInfo response) {
//                mHomeInfo = response;
//                adapter.addData(mHomeInfo);
//                adapter.notifyDataSetChanged();
//                recyclerView.setShowLoadingView(false);
//                recyclerView.setLoading(false);
//                mLastLoadTime -= INTERVAL_DAY;
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
    public void onLoadMoreViewClick() {
        recyclerView.setLoadView("正在加载", true);
        recyclerView.setEnableLoadMore(true);
        if (mLastLoadTime < 0) {
            loadData();
        } else {
            onLoadMore();
        }
    }
}
