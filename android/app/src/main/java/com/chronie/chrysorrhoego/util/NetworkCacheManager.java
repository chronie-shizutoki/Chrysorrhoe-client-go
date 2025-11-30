package com.chronie.chrysorrhoego.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

// import com.chronie.chrysorrhoego.BuildConfig; // 临时注释，Gradle会自动生成

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 网络缓存管理器
 * 用于管理OkHttp的响应缓存策略
 */
public class NetworkCacheManager {
    private static final String TAG = "NetworkCacheManager";
    private static final int MAX_CACHE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final String CACHE_DIR_NAME = "http-cache";
    
    private final Cache cache;
    
    public NetworkCacheManager(Context context) {
        // 创建缓存目录
        File cacheDir = new File(context.getCacheDir(), CACHE_DIR_NAME);
        this.cache = new Cache(cacheDir, MAX_CACHE_SIZE);
    }
    
    /**
     * 获取OkHttp缓存实例
     */
    public Cache getCache() {
        return cache;
    }
    
    /**
     * 获取缓存控制拦截器
     * 用于设置请求的缓存策略
     */
    public Interceptor getCacheControlInterceptor() {
        return chain -> {
            Request request = chain.request();
            Response response = null;
            
            // 监控请求执行时间
            PerformanceMonitor.getInstance().startMethodMonitoring("NetworkRequest:" + request.url().encodedPath());
            
            try {
                // 根据请求头设置缓存策略
                if (request.header("Cache-Control") == null) {
                    // 默认缓存策略：在线时优先使用网络，离线时使用缓存
                    CacheControl cacheControl = new CacheControl.Builder()
                            .maxAge(1, TimeUnit.MINUTES) // 在线时缓存1分钟
                            .maxStale(24, TimeUnit.HOURS) // 离线时缓存24小时
                            .build();
                    
                    request = request.newBuilder()
                            .cacheControl(cacheControl)
                            .build();
                }
                
                response = chain.proceed(request);
                
                // 设置缓存响应头
                Response.Builder responseBuilder = response.newBuilder()
                        .removeHeader("Pragma") // 移除Pragma以允许缓存
                        .header("Cache-Control", "max-age=60"); // 响应缓存1分钟
                
                response = responseBuilder.build();
                
                return response;
            } catch (Exception e) {
                Log.e(TAG, "Error during network request: " + e.getMessage());
                throw e;
            } finally {
                // 结束性能监控
                PerformanceMonitor.getInstance().endMethodMonitoring("NetworkRequest:" + request.url().encodedPath());
            }
        };
    }
    
    /**
     * 清除所有缓存
     */
    public void clearCache() {
        try {
            cache.evictAll();
            Log.d(TAG, "Network cache cleared successfully");
        } catch (IOException e) {
            Log.e(TAG, "Failed to clear network cache: " + e.getMessage());
        }
    }
    
    /**
     * 获取缓存大小
     */
    public long getCacheSize() {
        try {
            return cache.size();
        } catch (IOException e) {
            Log.e(TAG, "Failed to get cache size: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * 获取缓存统计信息
     */
    public String getCacheStats() {
        try {
            // 暂时简化缓存统计，避免Stats类的问题
            return String.format("Cache Stats - Size: %d bytes", cache.size());
        } catch (IOException e) {
            return "Failed to get cache stats: " + e.getMessage();
        }
    }
    
    /**
     * 创建针对特定API的缓存控制请求
     * @param request 原始请求
     * @param maxAge 最大缓存时间（秒）
     * @return 带缓存控制的请求
     */
    public static Request createCacheControlledRequest(Request request, int maxAge) {
        CacheControl cacheControl = new CacheControl.Builder()
                .maxAge(maxAge, TimeUnit.SECONDS)
                .build();
        
        return request.newBuilder()
                .cacheControl(cacheControl)
                .build();
    }
}