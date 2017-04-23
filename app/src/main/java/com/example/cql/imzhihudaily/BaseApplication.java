package com.example.cql.imzhihudaily;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.example.cql.imzhihudaily.database.DBHelper;
import com.facebook.stetho.Stetho;

/**
 * Created by CQL on 2016/10/5.
 */

public class BaseApplication extends Application {
    private static BaseApplication application;

    private static Handler handler;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate(){
        super.onCreate();
        application = this;
//        ActivityHookUtils.hook();
//        MyProxy proxy = ProxyUtils.creat(MyProxy.class);
//        System.out.println(proxy.sayBye());
        handler = new Handler();
        Stetho.initializeWithDefaults(this);
        DBHelper.init(this);
    }

    public static BaseApplication getApplication(){
        return application;
    }

    public static Handler getHandler(){
        return handler;
    }
}
