package com.example.cql.imzhihudaily.hook;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by CQL on 2017/4/8.
 */

public class ProxyUtils {
    @SuppressWarnings("unchecked")
    public static <T> T creat(Class<T> clazz){
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{clazz}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return "bye";
            }
        });
    }
}
