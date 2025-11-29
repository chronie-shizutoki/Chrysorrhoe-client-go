package com.chronie.chrysorrhoego.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.chronie.chrysorrhoego.R;
import com.chronie.chrysorrhoego.ui.navigation.NavigationActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 用户认证活动，处理登录逻辑
 */
public class AuthActivity extends AppCompatActivity {
    private static final String TAG = "AuthActivity";
    private static final String API_BASE_URL = "http://192.168.0.197:3200/api";
    private static final String PREFS_NAME = "wallet_prefs";
    private static final String KEY_WALLET_DATA = "wallet_data";
    
    private EditText usernameInput;
    private Button loginButton;
    private TextView errorText;
    private ProgressBar progressBar;
    private OkHttpClient okHttpClient;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        initializeViews();
        setupListeners();
        okHttpClient = new OkHttpClient();
        executorService = Executors.newSingleThreadExecutor();
    }

    private void initializeViews() {
        usernameInput = findViewById(R.id.username_input);
        loginButton = findViewById(R.id.login_button);
        errorText = findViewById(R.id.error_text);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setupListeners() {
        loginButton.setOnClickListener(v -> handleLogin());
    }

    private void handleLogin() {
        String username = usernameInput.getText().toString().trim();

        // 简单的输入验证 - 现在只需要用户名
        if (username.isEmpty()) {
            showError(getString(R.string.login_error));
            return;
        }

        // 显示加载状态
        showLoading(true);

        // 调用登录API - 参考网页版实现，调用getWalletByUsername接口
        String url = API_BASE_URL + "/wallets/username/" + username;
        Log.d(TAG, "Login URL: " + url);

        // 创建请求
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        // 执行请求
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Log.e(TAG, "Login error: " + e.getMessage());
                    showError("Network error, please check your connection");
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(() -> showLoading(false));
                
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String responseBody = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        
                        boolean success = jsonResponse.getBoolean("success");
                        if (success) {
                            // 保存钱包信息到SharedPreferences
                            JSONObject walletData = jsonResponse.getJSONObject("wallet");
                            saveWalletData(walletData.toString());
                            
                            // 导航到仪表板
                            navigateToDashboard(walletData);
                        } else {
                            String errorMessage = jsonResponse.optString("error", getString(R.string.login_error));
                            runOnUiThread(() -> showError(errorMessage));
                        }
                    } else {
                        runOnUiThread(() -> {
                            if (response.code() == 404) {
                                showError("Wallet not found");
                            } else {
                                showError(getString(R.string.login_error));
                            }
                        });
                    }
                } catch (JSONException | IOException e) {
                    Log.e(TAG, "Error processing response: " + e.getMessage());
                    runOnUiThread(() -> showError(getString(R.string.login_error)));
                } finally {
                    if (response.body() != null) {
                        response.body().close();
                    }
                }
            }
        });
    }

    private void saveWalletData(String walletData) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_WALLET_DATA, walletData);
        editor.apply();
        Log.d(TAG, "Wallet data saved");
    }

    private void navigateToDashboard(JSONObject walletData) throws JSONException {
        Intent intent = new Intent(this, NavigationActivity.class);
        intent.putExtra("wallet_id", walletData.getString("id"));
        intent.putExtra("username", walletData.getString("username"));
        intent.putExtra("balance", walletData.getDouble("balance"));
        startActivity(intent);
        finish();
    }

    private void showError(String message) {
        errorText.setText(message);
        errorText.setVisibility(View.VISIBLE);
    }

    private void showLoading(boolean loading) {
        if (loading) {
            progressBar.setVisibility(View.VISIBLE);
            loginButton.setEnabled(false);
            errorText.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            loginButton.setEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 清理资源
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}