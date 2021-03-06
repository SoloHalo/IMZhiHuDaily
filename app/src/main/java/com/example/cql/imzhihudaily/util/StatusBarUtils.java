package com.example.cql.imzhihudaily.util;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import com.example.cql.imzhihudaily.R;


/**
 * Created by liuhui on 2016/9/3.
 */
public class StatusBarUtils {

//    public static void setDrawerToolbarTabLayout(Activity activity, CoordinatorLayout coordinatorLayout, ViewGroup drawer){
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
//            activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity, R.color.statusBar));
//        } else if(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT){
//            ViewGroup contentLayout = (ViewGroup) activity.findViewById(android.R.id.content);
//            contentLayout.getChildAt(0).setFitsSystemWindows(false);
//            coordinatorLayout.setFitsSystemWindows(true);
//            drawer.setPadding(0,getStatusBarHeight(activity),0,0);
//            setKKStatusBar(activity, R.color.colorPrimaryDark);
//        }
//    }

    public static void setImageTranslucent(Activity activity, Toolbar toolbar) {
        toolbar.setFitsSystemWindows(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity, R.color.statusBar));
        } else {
            setKKStatusBar(activity, R.color.statusBar);
        }
    }

    public static void setImageTransparent(Activity activity, Toolbar toolbar) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
//        toolbar.setFitsSystemWindows(true);
        ViewGroup.LayoutParams params = toolbar.getLayoutParams();
        params.height += getStatusBarHeight(activity);
        toolbar.setLayoutParams(params);
        toolbar.setPadding(0,getStatusBarHeight(activity),0,0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    public static void setDrawerToolbarLayout(Activity activity, ViewGroup container, Toolbar toolbar, ViewGroup drawer) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark));
            ViewGroup contentLayout = (ViewGroup) activity.findViewById(android.R.id.content);
            contentLayout.getChildAt(0).setFitsSystemWindows(true);
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            ViewGroup contentLayout = (ViewGroup) activity.findViewById(android.R.id.content);
            contentLayout.getChildAt(0).setFitsSystemWindows(false);
            container.setFitsSystemWindows(true);
            toolbar.setFitsSystemWindows(true);
            drawer.setFitsSystemWindows(false);
            drawer.setPadding(0, getStatusBarHeight(activity), 0, 0);
            setKKStatusBar(activity, R.color.colorPrimaryDark);
        }
    }

    public static void setSimpleToolbarLayout(Activity activity, View toolbar) {
        toolbar.setFitsSystemWindows(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark));
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            setKKStatusBar(activity, R.color.colorPrimaryDark);
        }
    }

    private static void setKKStatusBar(Activity activity, int statusBarColor) {
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        View mStatusBarView = decorView.getChildAt(0);
        //改变颜色时避免重复添加statusBarView
        if (mStatusBarView != null && mStatusBarView.getMeasuredHeight() == getStatusBarHeight(activity)) {
            mStatusBarView.setBackgroundColor(ContextCompat.getColor(activity, statusBarColor));
            return;
        }
        mStatusBarView = new View(activity);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                getStatusBarHeight(activity));
        mStatusBarView.setBackgroundColor(ContextCompat.getColor(activity, statusBarColor));
        decorView.addView(mStatusBarView, lp);
    }

    private static int getStatusBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }
}
