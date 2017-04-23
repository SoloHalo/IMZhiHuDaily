package com.example.cql.imzhihudaily.data;

import com.example.cql.imzhihudaily.data.bean.DrawerInfo;
import com.example.cql.imzhihudaily.data.bean.HomeInfo;
import com.example.cql.imzhihudaily.data.bean.NewsDetailInfo;
import com.example.cql.imzhihudaily.data.bean.ThemeInfo;
import com.example.cql.imzhihudaily.util.OkHttpHelper;

import java.text.SimpleDateFormat;
import java.util.Locale;

import okhttp3.Request;

/* http://news-at.zhihu.com/api/4/theme/11/before/7119483
 * Created by CQL on 2016/10/9.
 */

public class NewsService {

    private final String LATEST_NEWS_URL = "http://news-at.zhihu.com/api/4/news/latest";
    private final String OLDER_NEWS_URL = "http://news-at.zhihu.com/api/4/news/before/";
    private final String NEWS_DETAIL_URL = "http://news-at.zhihu.com/api/4/news/";
    private final String DRAWER_THEME_URL = " http://news-at.zhihu.com/api/4/themes";
    private final String THEME_NEWS_URL = "http://news-at.zhihu.com/api/4/theme/";

    private static NewsService mInstance = new NewsService();

    public static NewsService getInstance() {
        return mInstance;
    }

    public void getLatestNews(JsonCallback<HomeInfo> jsonCallback) {
        Request request = new Request.Builder().url(LATEST_NEWS_URL).build();
        OkHttpHelper.getInstance().newCall(request).enqueue(jsonCallback);
    }

    public void getOlderNews(long date, JsonCallback<HomeInfo> jsonCallback) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String queryDate = simpleDateFormat.format(date);
        Request request = new Request.Builder().url(OLDER_NEWS_URL + queryDate).build();
        OkHttpHelper.getInstance().newCall(request).enqueue(jsonCallback);
    }

    public void getNewsDetail(String id, JsonCallback<NewsDetailInfo> jsonCallback){
        Request request = new Request.Builder().url(NEWS_DETAIL_URL + id).build();
        OkHttpHelper.getInstance().newCall(request).enqueue(jsonCallback);
    }

    public void getNewsDetail(int id, JsonCallback<NewsDetailInfo> jsonCallback){
        Request request = new Request.Builder().url(NEWS_DETAIL_URL + id).build();
        OkHttpHelper.getInstance().newCall(request).enqueue(jsonCallback);
    }

    public void getDrawerList(JsonCallback<DrawerInfo> jsonCallback){
        Request request = new Request.Builder().url(DRAWER_THEME_URL).build();
        OkHttpHelper.getInstance().newCall(request).enqueue(jsonCallback);
    }

    public void getThemeNews(int id, JsonCallback<ThemeInfo> jsonCallback){
        Request request = new Request.Builder().url(THEME_NEWS_URL + id).build();
        OkHttpHelper.getInstance().newCall(request).enqueue(jsonCallback);
    }

    public void getThemeOldNews(int themeId, int beforeId ,JsonCallback<ThemeInfo> jsonCallback){
        Request request = new Request.Builder().url(THEME_NEWS_URL + themeId + "/before/" + beforeId).build();
        OkHttpHelper.getInstance().newCall(request).enqueue(jsonCallback);
    }
}
