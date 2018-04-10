package com.lingyun_chain.zihua.base;

import android.app.Application;
import android.os.Handler;

import com.tencent.bugly.Bugly;

/**
 * Application的父类
 */
public class BaseApplication extends Application {
    private static BaseApplication application;
    private static int mainTid;
    private static Handler handler;
    @Override
    public void onCreate() {
        super.onCreate();
        Bugly.init(getApplicationContext(), "8d5b7b67b2", true);
        application = this;
        mainTid = android.os.Process.myTid();
        handler = new Handler();
    }
    public static BaseApplication getInstance() {

        return application;
    }
    public static int getMainTid() {
        return mainTid;
    }

    public static Handler getHandler() {
        return handler;
    }
}
