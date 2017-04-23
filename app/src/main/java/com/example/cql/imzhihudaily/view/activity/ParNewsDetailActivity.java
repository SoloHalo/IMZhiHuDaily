package com.example.cql.imzhihudaily.view.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.example.cql.imzhihudaily.R;
import com.example.cql.imzhihudaily.data.RetrofitManager;
import com.example.cql.imzhihudaily.data.bean.NewsDetailInfo;
import com.example.cql.imzhihudaily.database.DBHelper;
import com.example.cql.imzhihudaily.util.LikeUtils;
import com.example.cql.imzhihudaily.util.StatusBarUtils;
import com.example.cql.imzhihudaily.util.ToastUtils;
import com.google.gson.Gson;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by CQL on 2016/10/20.
 */

public class ParNewsDetailActivity extends AppCompatActivity {
    private static final String EXTRA_ID = "id";
    private NewsDetailInfo info;
    private WebView webView;
    private int mId;

    public static void startActivity(int id, Context context) {
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_ID, id);
        Intent intent = new Intent(context, ParNewsDetailActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partition_detail);
        initView();
        initData();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.news_detail_toolbar_partition);
        toolbar.inflateMenu(R.menu.news_detail_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.like) {
                    String content = new Gson().toJson(info);
                    LikeUtils.likeOrUnlike(getApplicationContext(),mId+"",info.getTitle(),content);
                }
                return false;
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        StatusBarUtils.setSimpleToolbarLayout(this, toolbar);
        webView = (WebView) findViewById(R.id.wb_partition_news_detail);
        webView.setWebChromeClient(new WebChromeClient());

    }

    private void initData() {
        mId = getIntent().getIntExtra(EXTRA_ID, 0);
        RetrofitManager.builder().getNewsDetail(mId + "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<NewsDetailInfo>() {
                    @Override
                    public void call(NewsDetailInfo newsDetailInfo) {
                        info = newsDetailInfo;
                        setWebViewDisplay();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        ToastUtils.showToast(ParNewsDetailActivity.this, "网络错误");
                    }
                });
//        NewsService.getInstance().getNewsDetail(id, new JsonCallback<NewsDetailInfo>() {
//            @Override
//            protected void onResponse(NewsDetailInfo response) {
//                info = response;
//                setWebViewDisplay();
//            }
//
//            @Override
//            protected void onFailure(Throwable t) {
//                ToastUtils.showToast(ParNewsDetailActivity.this, "网络错误");
//            }
//        });
    }

    private void setWebViewDisplay() {
        String css = "<link rel=\"stylesheet\" href=\"news.css\" type=\"text/css\">";
        String html = "<html><head>" + css + "</head><body>" + info.getBody() + "</body></html>";
        webView.loadDataWithBaseURL("file:///android_asset/", html, "text/html; charset=UTF-8", "UTF-8", null);
    }
}
