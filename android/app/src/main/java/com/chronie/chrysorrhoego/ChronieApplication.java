package com.chronie.chrysorrhoego;

import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * 全局应用类，提供应用级别的上下文和配置
 */
public class ChronieApplication extends Application {
    private static final String TAG = "ChronieApplication";
    private static ChronieApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Log.d(TAG, "Chronie application started");
        
        // 初始化应用配置
        initAppConfig();
    }

    /**
     * 获取应用实例
     */
    public static ChronieApplication getInstance() {
        return instance;
    }

    /**
     * 获取应用上下文
     */
    public static Context getAppContext() {
        return instance != null ? instance.getApplicationContext() : null;
    }

    /**
     * 初始化应用配置
     */
    private void initAppConfig() {
        // 可以在这里初始化各种库和设置
        Log.d(TAG, "Initializing application configuration");
    }

    /**
     * 检查应用是否在调试模式
     */
    public static boolean isDebug() {
        // 使用硬编码的调试标志作为临时解决方案
        return true;
    }
}