package com.example.cql.imzhihudaily.data;

import com.example.cql.imzhihudaily.BaseApplication;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Response;


/**
 * Created by CQL on 2016/10/9.
 * JsonCallback
 */

public abstract class JsonCallback<T> implements okhttp3.Callback {

    @Override
    public void onFailure(Call call, final IOException e) {
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                onFailure(e);
            }
        });
    }

    @Override
    public void onResponse(Call call, Response response){
        if (response.isSuccessful()) {
            try {
                final T t;
                Type type = getClass().getGenericSuperclass();
                if (type instanceof Class){
                    throw new RuntimeException("mast have T");
                }else {
                    Type cType = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
                    if (cType instanceof Class){
                        t = new Gson().fromJson(response.body().string(),cType);
                    }else {
                        t = new Gson().fromJson(response.body().string(),new TypeToken<T>(){}.getType());
                    }
                }
                BaseApplication.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        onResponse(t);
                    }
                });
            } catch (final IOException e) {
                e.printStackTrace();
                BaseApplication.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        onFailure(e);
                    }
                });
            }
        } else {
            BaseApplication.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    onFailure(new Exception("unKnown"));
                }
            });
        }
    }

    protected abstract void onResponse(T response);
    protected abstract void onFailure(Throwable t);
}
