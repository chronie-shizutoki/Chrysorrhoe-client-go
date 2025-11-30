package com.chronie.chrysorrhoego.data.remote;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static org.junit.Assert.*;

/**
 * RetrofitClient测试类
 * 测试网络客户端的配置和功能
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.O, Build.VERSION_CODES.P})
public class RetrofitClientTest {

    private static final String TAG = "RetrofitClientTest";
    private Context context;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        context = ApplicationProvider.getApplicationContext();
        
        // 初始化RetrofitClient
        RetrofitClient.initialize(context);
    }

    /**
     * 测试RetrofitClient单例模式
     */
    @Test
    public void testSingletonPattern() {
        // 获取两次实例并验证是否相同
        RetrofitClient client1 = RetrofitClient.getInstance();
        RetrofitClient client2 = RetrofitClient.getInstance();
        
        assertSame(client1, client2);
        assertNotNull(client1);
        assertNotNull(client2);
    }

    /**
     * 测试API服务实例获取
     */
    @Test
    public void testGetApiService() {
        RetrofitClient client = RetrofitClient.getInstance();
        ApiService apiService = client.getApiService();
        
        assertNotNull(apiService);
    }

    /**
     * 测试Retrofit实例获取
     */
    @Test
    public void testGetRetrofit() {
        RetrofitClient client = RetrofitClient.getInstance();
        retrofit2.Retrofit retrofit = client.getRetrofit();
        
        assertNotNull(retrofit);
        assertEquals("http://192.168.0.197:3200", retrofit.baseUrl().toString());
    }

    /**
     * 测试OkHttpClient配置
     */
    @Test
    public void testOkHttpClientConfig() {
        RetrofitClient client = RetrofitClient.getInstance();
        retrofit2.Retrofit retrofit = client.getRetrofit();
        
        // 获取OkHttpClient
        OkHttpClient clientImpl = (OkHttpClient) retrofit.callFactory();
        
        // 验证超时配置
        assertEquals(30, clientImpl.connectTimeoutMillis(), 0);
        assertEquals(30, clientImpl.readTimeoutMillis(), 0);
        assertEquals(30, clientImpl.writeTimeoutMillis(), 0);
        
        // 验证拦截器数量（至少应该有一个认证拦截器）
        assertTrue(clientImpl.interceptors().size() >= 1);
    }

    /**
     * 测试认证头添加功能
     */
    @Test
    public void testAuthInterceptor() throws IOException {
        // 设置测试token到SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
        prefs.edit().putString("auth_token", "test_token_123").apply();
        
        // 重新初始化RetrofitClient以应用新token
        RetrofitClient.initialize(context);
        
        // 创建一个测试请求
        Request request = new Request.Builder()
                .url("http://192.168.0.197:3200/test")
                .build();
        
        // 获取OkHttpClient并模拟调用
        RetrofitClient client = RetrofitClient.getInstance();
        OkHttpClient okHttpClient = (OkHttpClient) client.getRetrofit().callFactory();
        
        // 创建一个模拟响应的拦截器
        OkHttpClient testClient = okHttpClient.newBuilder()
                .addInterceptor(chain -> {
                    // 检查请求头
                    Request interceptedRequest = chain.request();
                    String authHeader = interceptedRequest.header("Authorization");
                    
                    // 验证认证头
                    assertEquals("Bearer test_token_123", authHeader);
                    
                    // 返回模拟响应
                    return new Response.Builder()
                            .request(interceptedRequest)
                            .protocol(okhttp3.Protocol.HTTP_1_1)
                            .code(200)
                            .message("OK")
                            .build();
                })
                .build();
        
        // 执行请求
        Response response = testClient.newCall(request).execute();
        
        // 验证响应
        assertEquals(200, response.code());
        
        // 清理
        prefs.edit().remove("auth_token").apply();
    }
}