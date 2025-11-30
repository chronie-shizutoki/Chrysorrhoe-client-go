package com.chronie.chrysorrhoego;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

// import androidx.multidex.MultiDex;

import com.chronie.chrysorrhoego.data.remote.RetrofitClient;
import com.chronie.chrysorrhoego.ui.theme.ThemeManager;
import com.chronie.chrysorrhoego.util.BackgroundTaskExecutor;
import com.chronie.chrysorrhoego.util.PerformanceMonitor;

/**
 * 全局应用类，提供应用级别的上下文和配置
 */
public class ChronieApplication extends Application {
    private static final String TAG = "ChronieApplication";
    private static ChronieApplication instance;

    // @Override
    // protected void attachBaseContext(Context base) {
    //     super.attachBaseContext(base);
    //     MultiDex.install(this);
    // }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Log.d(TAG, "Chronie application started");
        
        // 开始性能监控
        PerformanceMonitor.getInstance().startMethodMonitoring("Application.onCreate");
        
        try {
            // 在开发环境中启用严格模式，帮助发现潜在问题
            if (isDebug()) {
                enableStrictMode();
            }
            
            // 初始化应用配置
            initAppConfig();
            
            // 初始化Retrofit客户端
            RetrofitClient.initialize(this);
            
            // 启动FPS监控（仅在Android 4.1及以上版本）
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                startFpsMonitoring();
            }
            
            // 在后台线程监控内存使用
            BackgroundTaskExecutor.getInstance().executeOnComputationThread(() -> {
                // 定期监控内存使用情况
                while (true) {
                    try {
                        Thread.sleep(30000); // 每30秒监控一次
                        PerformanceMonitor.getInstance().monitorMemoryUsage();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
            
            // 记录初始化完成
            Log.d(TAG, "Application initialized successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Error during application initialization: " + e.getMessage(), e);
        } finally {
            PerformanceMonitor.getInstance().endMethodMonitoring("Application.onCreate");
        }
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
        Log.d(TAG, "Initializing application configuration");
        
        // 初始化主题管理器
        ThemeManager.getInstance().init(this);
        
        // 初始化其他必要的组件和配置
        // TODO: 初始化网络库、数据库等
    }

    /**
     * 启用严格模式
     */
    private void enableStrictMode() {
        // 线程策略配置
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyDeathOnNetwork()
                .build();
        StrictMode.setThreadPolicy(policy);
        
        // VM策略配置
        StrictMode.VmPolicy vmPolicy = new StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build();
        StrictMode.setVmPolicy(vmPolicy);
    }

    /**
     * 检查应用是否在调试模式
     */
    public static boolean isDebug() {
        // 从构建配置中获取调试状态
        return BuildConfig.DEBUG;
    }
    
    /**
     * 启动FPS监控
     */
    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void startFpsMonitoring() {
        PerformanceMonitor.getInstance().startFpsMonitoring(fps -> {
            // 记录FPS变化，但只在低于阈值时打印日志
            if (fps < 50) {
                Log.d(TAG, "FPS dropped to: " + fps + "fps");
            }
        });
    }
    
    /**
     * 应用退出时调用
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
        
        // 记录应用退出性能统计
        Log.d(TAG, PerformanceMonitor.getInstance().getOptimizationSuggestions());
        Log.d(TAG, RetrofitClient.getCacheStats());
    }
}