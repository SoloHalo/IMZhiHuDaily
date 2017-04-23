package com.example.cql.imzhihudaily.view.fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.cql.imzhihudaily.BaseApplication;
import com.example.cql.imzhihudaily.BaseFragment;
import com.example.cql.imzhihudaily.R;
import com.example.cql.imzhihudaily.adapter.LikeAdapter;
import com.example.cql.imzhihudaily.data.bean.NewsDetailInfo;
import com.example.cql.imzhihudaily.database.DBHelper;
import com.example.cql.imzhihudaily.util.ToastUtils;
import com.example.cql.imzhihudaily.view.activity.IDrawerLayoutActivity;
import com.example.cql.imzhihudaily.view.activity.ParNewsDetailActivity;
import com.google.gson.Gson;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * Created by CQL on 2017/3/5.
 */

public class LikeFragment extends BaseFragment {

    public static final String TAG = LikeFragment.class.getName();

    Toolbar mToolbar;

    SwipeRefreshLayout mSwipeRefreshLayout;

    RecyclerView mRecyclerView;

    private LikeAdapter mLikeAdapter;

    public static LikeFragment newInstance() {
        return new LikeFragment();
    }

    @Override
    protected void initView(View view) {
        mToolbar = (Toolbar) view.findViewById(R.id.favorite_toolbar);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycle_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadLikeData();
            }
        });
        mToolbar.setTitle(R.string.my_favorite);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof IDrawerLayoutActivity) {
                    ((IDrawerLayoutActivity) getActivity()).openDrawer();
                }
            }
        });
        mLikeAdapter = new LikeAdapter();
        final LinearLayoutManager layoutManager = new LinearLayoutManager(BaseApplication.getApplication());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mLikeAdapter);
        mLikeAdapter.setOnItemClickListener(new LikeAdapter.onItemClickListener() {
            @Override
            public void onItemClick(String id) {
                ParNewsDetailActivity.startActivity(Integer.parseInt(id),getContext());
            }
        });
        loadLikeData();
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_like;
    }

    private void loadLikeData() {
        Observable
                .create(new Observable.OnSubscribe<NewsDetailInfo>() {
                    @Override
                    public void call(Subscriber<? super NewsDetailInfo> subscriber) {
                        SQLiteDatabase db = DBHelper.getInstance().getReadableDatabase();
                        Cursor cursor = db.rawQuery("select * from " + DBHelper.NAME, new String[]{});
                        Gson gson = new Gson();
                        while (cursor.moveToNext()) {
                            String content = cursor.getString(cursor.getColumnIndex("json_content"));
                            NewsDetailInfo info = gson.fromJson(content, NewsDetailInfo.class);
                            subscriber.onNext(info);
                        }
                        subscriber.onCompleted();
                        cursor.close();
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mSwipeRefreshLayout.setRefreshing(true);
                        mLikeAdapter.clear();
                    }
                })
                .subscribe(new Subscriber<List<NewsDetailInfo>>() {
                    @Override
                    public void onCompleted() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showToast(getContext().getApplicationContext(),"加载失败");
                    }

                    @Override
                    public void onNext(List<NewsDetailInfo> infos) {
                        mLikeAdapter.addAll(infos);
                    }
                });
    }
}
