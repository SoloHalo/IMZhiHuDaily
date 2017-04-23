package com.example.cql.imzhihudaily.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.cql.imzhihudaily.LazyLoadFragment;
import com.example.cql.imzhihudaily.R;
import com.example.cql.imzhihudaily.data.RetrofitManager;
import com.example.cql.imzhihudaily.data.bean.NewsDetailInfo;
import com.example.cql.imzhihudaily.util.LikeUtils;
import com.example.cql.imzhihudaily.util.StatusBarUtils;
import com.example.cql.imzhihudaily.util.ToastUtils;
import com.example.cql.imzhihudaily.widget.ParallaxScrollView;
import com.google.gson.Gson;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by CQL on 2016/10/14.
 * 侧边栏列表
 */

public class StoriesFragment extends LazyLoadFragment {

    private Toolbar toolbar;
    private ParallaxScrollView scrollView;
    private ImageView iv_header;
    private TextView tv_header_title;
    private TextView tv_image_source;
    private WebView webView;
    private NewsDetailInfo info;
    private View headView;
    private String id;

    public static StoriesFragment newInstance(String id) {
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        StoriesFragment fragment = new StoriesFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initView(View view) {
        toolbar = (Toolbar) view.findViewById(R.id.news_detail_toolbar);
        toolbar.inflateMenu(R.menu.news_detail_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.like) {
                    String content = new Gson().toJson(info);
                    LikeUtils.likeOrUnlike(getContext().getApplicationContext(), id, info.getTitle(), content);
                }
                return false;
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        scrollView = (ParallaxScrollView) view.findViewById(R.id.news_detail_scrollView);
        iv_header = (ImageView) view.findViewById(R.id.iv_detail_head);
        tv_header_title = (TextView) view.findViewById(R.id.tv_detail_head_title);
        tv_image_source = (TextView) view.findViewById(R.id.tv_image_source);
        webView = (WebView) view.findViewById(R.id.wb_news_detail);
        headView = view.findViewById(R.id.head_view);
        StatusBarUtils.setImageTransparent(getActivity(), toolbar);
        webView.setWebChromeClient(new WebChromeClient());

        scrollView.setOnScrollChangeListener(new ParallaxScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {
                float total = headView.getMeasuredHeight() - toolbar.getMeasuredHeight();
                float alpha = 1 - t / total;
                if (alpha < 0) {
                    alpha = 0;
                } else if (alpha >= 1) {
                    alpha = 1;
                }
                toolbar.setAlpha(alpha);
            }
        });
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_news_detail;
    }

    @Override
    protected void fetchData() {
        initData();
    }

    private void initData() {
        id = getArguments().getString("id");

        RetrofitManager.builder().getNewsDetail(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<NewsDetailInfo>() {
                    @Override
                    public void call(NewsDetailInfo newsDetailInfo) {
                        info = newsDetailInfo;
                        setImage();
                        tv_header_title.setText(info.getTitle());
                        tv_image_source.setText(info.getImageSource());
                        setWebViewDisplay();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        ToastUtils.showToast(getContext(), "网络错误");
                    }
                });
//        NewsService.getInstance().getNewsDetail(id, new JsonCallback<NewsDetailInfo>() {
//            @Override
//            protected void onResponse(NewsDetailInfo response) {
//                info = response;
//                setImage();
//                tv_header_title.setText(info.getTitle());
//                tv_image_source.setText(info.getImageSource());
//                setWebViewDisplay();
//            }
//
//            @Override
//            protected void onFailure(Throwable t) {
//                ToastUtils.showToast(getContext(), "网络错误");
//            }
//        });
    }

    private void setImage() {
        Glide.with(this).load(info.getImage()).centerCrop().into(iv_header);
    }

    private void setWebViewDisplay() {
        String css = "<link rel=\"stylesheet\" href=\"news.css\" type=\"text/css\">";
        String html = "<html><head>" + css + "</head><body>" + info.getBody() + "</body></html>";
        webView.loadDataWithBaseURL("file:///android_asset/", html, "text/html; charset=UTF-8", "UTF-8", null);
    }
}
