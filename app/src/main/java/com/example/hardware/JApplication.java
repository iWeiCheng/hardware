package com.example.hardware;


import android.app.Application;

public class JApplication extends Application {


    private static JApplication mApplication;

    public static JApplication getInstance() {
        return mApplication;
    }



    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        // 初始化mWanComponent

    }

}
