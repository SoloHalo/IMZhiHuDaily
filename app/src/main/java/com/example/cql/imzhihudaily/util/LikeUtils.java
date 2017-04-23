package com.example.cql.imzhihudaily.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.cql.imzhihudaily.database.DBHelper;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by CQL on 2017/3/5.
 */

public class LikeUtils {

    public static void likeOrUnlike(final Context context, final String id, final String title, final String content){
        LikeUtils.isLike(id+"").flatMap(new Func1<Boolean, Observable<?>>() {
            @Override
            public Observable<?> call(Boolean aBoolean) {
                if (aBoolean) {
                    return unlike(context,id);
                } else {
                    return like(context,id,title,content);
                }
            }
        }).subscribe(new Subscriber<Object>() {
            @Override
            public void onCompleted() {
                System.out.println("onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(Object o) {
                System.out.println("onNext");
            }
        });
    }

    private static Observable<Boolean> isLike(final String id) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                SQLiteDatabase db = DBHelper.getInstance().getReadableDatabase();
                Cursor cursor = db.rawQuery("select * from " + DBHelper.NAME + " where zhihu_id = ?", new String[]{id});
                boolean result = cursor.getCount() > 0;
                cursor.close();
                subscriber.onNext(result);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io());
    }

    private static Observable<?> like(final Context context, final String id, final String title, final String content) {
        return Observable
                .create(new Observable.OnSubscribe<Object>() {
                    @Override
                    public void call(Subscriber<? super Object> subscriber) {
                        try {
                            SQLiteDatabase db = DBHelper.getInstance().getWritableDatabase();
                            db.execSQL("insert into " + DBHelper.NAME + "(zhihu_id,title,time,json_content) values (?,?,?,?)",
                                    new String[]{id, title, System.currentTimeMillis() + "", content});
                            subscriber.onCompleted();
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        ToastUtils.showToast(context.getApplicationContext(), "收藏成功");
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        ToastUtils.showToast(context.getApplicationContext(), "收藏失败");
                    }
                });
    }

    private static Observable<?> unlike(final Context context, final String id) {
        return Observable
                .create(new Observable.OnSubscribe<Object>() {
                    @Override
                    public void call(Subscriber<? super Object> subscriber) {
                        try {
                            SQLiteDatabase db = DBHelper.getInstance().getWritableDatabase();
                            db.execSQL("delete from " + DBHelper.NAME + " where zhihu_id = " + id);
                            subscriber.onCompleted();
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        ToastUtils.showToast(context.getApplicationContext(), "取消收藏成功");
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        ToastUtils.showToast(context.getApplicationContext(), "取消收藏失败");
                    }
                });
    }
}
