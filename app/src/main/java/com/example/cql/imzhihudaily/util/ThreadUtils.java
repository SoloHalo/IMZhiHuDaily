package com.example.cql.imzhihudaily.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by CQL on 2016/8/31.
 */

//线程池，一个工程最多可以开四个线程

public class ThreadUtils {
    private static ExecutorService threadPool;

    public synchronized static ExecutorService getThreadpool(){
        if(threadPool == null){
            threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        }
        return threadPool;
    }
}
