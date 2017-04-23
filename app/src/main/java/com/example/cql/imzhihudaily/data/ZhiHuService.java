package com.example.cql.imzhihudaily.data;

import com.example.cql.imzhihudaily.data.bean.DrawerInfo;
import com.example.cql.imzhihudaily.data.bean.HomeInfo;
import com.example.cql.imzhihudaily.data.bean.NewsDetailInfo;
import com.example.cql.imzhihudaily.data.bean.ThemeInfo;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by CQL on 2017/2/27.
 */

public interface ZhiHuService {

    @GET("news/latest")
    Observable<HomeInfo> getLatestNews();

    @GET("news/before/{date}")
    Observable<HomeInfo> getOldNews(@Path("date") String date);

    @GET("news/{id}")
    Observable<NewsDetailInfo> getNewsDetail(@Path("id") String id);

    @GET("themes")
    Observable<DrawerInfo> getDrawerList();

    @GET("theme/{id}")
    Observable<ThemeInfo> getThemeNews(@Path("id") int id);

    @GET("theme/{themeId}/before/{beforeId}")
    Observable<ThemeInfo> getThemeOldNews(@Path("themeId") int themeId,@Path("beforeId") int beforeId);
}
