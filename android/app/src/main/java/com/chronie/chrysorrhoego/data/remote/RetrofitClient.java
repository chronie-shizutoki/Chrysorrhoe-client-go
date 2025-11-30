package com.chronie.chrysorrhoego.data.remote;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.Nullable;

// BuildConfig会由Gradle自动生成，正确的导入路径应该是:
// import com.chronie.chrysorrhoego.BuildConfig;
// 由于导入失败，临时使用硬编码常量
import com.chronie.chrysorrhoego.data.remote.interceptor.AuthInterceptor;
import com.chronie.chrysorrhoego.util.NetworkCacheManager;
import com.chronie.chrysorrhoego.util.PerformanceMonitor;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * RetrofitClient 是一个用于创建和管理 Retrofit 实例的单例类
 * 与 ApiClient 类似，但专为 ViewModel 层设计，提供更简洁的访问方式
 */
public class RetrofitClient {
    private static final String TAG = "RetrofitClient";
    private static final String BASE_URL = "https://api.chronie.example.com";
    private static RetrofitClient instance;
    private final Retrofit retrofit;
    private final ApiService apiService;
    private static Context applicationContext;
    private static NetworkCacheManager cacheManager;

    /**
     * 初始化 RetrofitClient
     * @param context 应用上下文，用于获取认证信息
     */
    public static void initialize(Context context) {
        if (context != null) {
            applicationContext = context.getApplicationContext();
            // 初始化缓存管理器
            cacheManager = new NetworkCacheManager(applicationContext);
            // 重置实例以应用新的配置
            instance = null;
        }
    }

    private RetrofitClient() {
        // 创建Gson实例
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .create();
                
        // 创建 OkHttpClient
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        
        // 添加缓存管理（如果上下文已初始化）
        if (applicationContext != null && cacheManager == null) {
            cacheManager = new NetworkCacheManager(applicationContext);
            clientBuilder.cache(cacheManager.getCache());
            clientBuilder.addInterceptor(cacheManager.getCacheControlInterceptor());
        }
        
        // 添加认证拦截器
        clientBuilder.addInterceptor(new SafeAuthInterceptor());
        
        // 添加性能监控拦截器
        clientBuilder.addInterceptor(chain -> {
            String url = chain.request().url().toString();
            PerformanceMonitor.getInstance().startMethodMonitoring("API:" + url.substring(url.lastIndexOf('/') + 1));
            
            try {
                Response response = chain.proceed(chain.request());
                return response;
            } finally {
                PerformanceMonitor.getInstance().endMethodMonitoring("API:" + url.substring(url.lastIndexOf('/') + 1));
            }
        });
        
        // 添加日志拦截器
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> {
            // 限制日志长度，避免过度打印
            if (message.length() > 500) {
                Log.d("OkHttp", message.substring(0, 500) + "... (truncated)");
            } else {
                Log.d("OkHttp", message);
            }
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        clientBuilder.addInterceptor(loggingInterceptor);
        
        // 设置超时
        clientBuilder.connectTimeout(15, TimeUnit.SECONDS); // 减少超时时间以提高用户体验
        clientBuilder.readTimeout(20, TimeUnit.SECONDS);
        clientBuilder.writeTimeout(20, TimeUnit.SECONDS);
        
        // 添加重试机制
        clientBuilder.retryOnConnectionFailure(true);
        
        OkHttpClient client = clientBuilder.build();

        // 创建 Retrofit 实例
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        // 创建 API 服务
        apiService = retrofit.create(ApiService.class);
    }

    /**
     * 获取 RetrofitClient 的单例实例
     * @return RetrofitClient 实例
     */
    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    /**
     * 获取 Retrofit 实例
     * @return Retrofit 实例
     */
    public Retrofit getRetrofit() {
        return retrofit;
    }

    /**
     * 获取 API 服务
     * @return ApiService 实例
     */
    public ApiService getApiService() {
        return apiService;
    }
    
    /**
     * 清除网络缓存
     */
    public static void clearCache() {
        if (cacheManager != null) {
            cacheManager.clearCache();
        }
    }
    
    /**
     * 获取缓存统计信息
     */
    public static String getCacheStats() {
        if (cacheManager != null) {
            return cacheManager.getCacheStats();
        }
        return "Cache manager not initialized";
    }

    /**
     * SafeAuthInterceptor 是一个安全的认证拦截器
     * 不需要在构造函数中传入 Context，而是使用类级别的 applicationContext
     */
    private static class SafeAuthInterceptor implements Interceptor {
        private static final String PREFS_NAME = "auth_prefs";
        private static final String KEY_TOKEN = "auth_token";

        @Override
        public Response intercept(Chain chain) throws IOException {
            String token = getAuthToken();
            Request originalRequest = chain.request();
            Request.Builder requestBuilder = originalRequest.newBuilder();

            if (!token.isEmpty()) {
                requestBuilder.header("Authorization", "Bearer " + token);
            }

            // 添加其他通用请求头
            requestBuilder.header("Content-Type", "application/json");
            requestBuilder.header("Accept", "application/json");

            Request request = requestBuilder.build();
            return chain.proceed(request);
        }

        private String getAuthToken() {
            if (applicationContext == null) {
                Log.w(TAG, "ApplicationContext is not initialized, cannot retrieve auth token");
                return "";
            }
            
            try {
                SharedPreferences prefs = applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                return prefs.getString(KEY_TOKEN, "");
            } catch (Exception e) {
                Log.e(TAG, "Failed to get auth token", e);
                return "";
            }
        }
    }
}