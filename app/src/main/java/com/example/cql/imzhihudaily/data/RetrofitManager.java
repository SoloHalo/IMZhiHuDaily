package com.example.cql.imzhihudaily.data;

import com.example.cql.imzhihudaily.data.bean.DrawerInfo;
import com.example.cql.imzhihudaily.data.bean.HomeInfo;
import com.example.cql.imzhihudaily.data.bean.NewsDetailInfo;
import com.example.cql.imzhihudaily.data.bean.ThemeInfo;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 * Created by CQL on 2017/2/27.
 */

public class RetrofitManager {

    public static final String BASE_ZHIHU_URL = "http://news-at.zhihu.com/api/4/";

    private static OkHttpClient mOkHttpClient;
    private final ZhiHuService mZhiHuService;

    public static RetrofitManager builder(){
        return new RetrofitManager();
    }

    private RetrofitManager() {
        initOkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_ZHIHU_URL)
                .client(mOkHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mZhiHuService = retrofit.create(ZhiHuService.class);
    }

    private void initOkHttpClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        if (mOkHttpClient == null) {
            synchronized (RetrofitManager.class) {
                if (mOkHttpClient == null) {

                    mOkHttpClient = new OkHttpClient.Builder()
                            .addInterceptor(interceptor)
                            .addNetworkInterceptor(new StethoInterceptor())
                            .retryOnConnectionFailure(true)
                            .connectTimeout(15, TimeUnit.SECONDS)
                            .build();
                }
            }
        }
    }

    public Observable<HomeInfo> getLatestNews(){
        return mZhiHuService.getLatestNews();
    }

    public Observable<HomeInfo> getOldNews(long date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String queryDate = simpleDateFormat.format(date);
        return mZhiHuService.getOldNews(queryDate);
    }

    public Observable<NewsDetailInfo> getNewsDetail(String id){
        return mZhiHuService.getNewsDetail(id);
    }

    public Observable<DrawerInfo> getDrawerList(){
        return mZhiHuService.getDrawerList();
    }

    public Observable<ThemeInfo> getThemeNews(int id){
        return mZhiHuService.getThemeNews(id);
    }

    public Observable<ThemeInfo> getThemeOldNews(int themeId,int beforeId){
        return mZhiHuService.getThemeOldNews(themeId,beforeId);
    }
}
