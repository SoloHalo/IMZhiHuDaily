package com.example.cql.imzhihudaily.hook;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.cql.imzhihudaily.BaseApplication;
import com.example.cql.imzhihudaily.view.activity.MainActivity;
import com.example.cql.imzhihudaily.view.activity.SplashActivity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by CQL on 2017/4/8.
 */

public class ActivityHookUtils {

    private static Object rawManager;
    private static Object myManager;

    public static void hook(){
        try {
            Class<?> clazz = Class.forName("android.app.ActivityManagerNative");
            Field field = clazz.getDeclaredField("gDefault");
            field.setAccessible(true);
            Object o = field.get(null);
            Class<?> clazz1 = Class.forName("android.util.Singleton");
            Field instance = clazz1.getDeclaredField("mInstance");
            instance.setAccessible(true);
            rawManager = instance.get(o);
            Class<?> mI = Class.forName("android.app.IActivityManager");
            myManager = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),new Class[]{mI},new InHandler());
            instance.set(o,myManager);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class InHandler implements InvocationHandler{

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("startActivity")) {
                for (int i = 0; i < args.length; i++) {
                    Object o = args[i];
                    if (o instanceof Intent) {
                        Intent in = new Intent(BaseApplication.getApplication(), MainActivity.class);
                        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        args[i] = in;
                    }
                }
            }
            return method.invoke(rawManager,args);
        }
    }
}
