package com.example.cql.imzhihudaily.util;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by seaice on 2016/8/11.
 */
public class OkHttpHelper {
    private static final String TAG = "OkHttpHelper";

    private static OkHttpClient mOkHttpClient;

    private OkHttpHelper() {
    }

    public static OkHttpClient getInstance() {
        if (mOkHttpClient == null) {
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            mOkHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(3, TimeUnit.SECONDS)
                    .readTimeout(3, TimeUnit.SECONDS)
                    .addInterceptor(httpLoggingInterceptor)
                    .build();
        }
        return mOkHttpClient;
    }

//    /**
//     * 该不会开启异步线程。
//     *
//     * @param request
//     * @return
//     * @throws IOException
//     */
//    public static Response execute(Request request) {
//        try {
//            return getInstance().newCall(request).execute();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    /**
//     * 开启异步线程访问网络
//     *
//     * @param request
//     * @param responseCallBack
//     */
//    public static void enqueue(Request request, ZhihuCallback responseCallBack) {
//        getInstance().newCall(request).enqueue(responseCallBack);
//    }
//
//    /**
//     * 开启异步线程访问网络, 且不在意返回结果（实现空callback）
//     *
//     * @param request
//     */
//    public static Response enqueue(Request request) {
//        getInstance().newCall(request).enqueue(new ZhihuCallback() {
//
//            @Override
//            public void onFailure(Call call, IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//
//            }
//
//        });
//        return null;
//    }
}
