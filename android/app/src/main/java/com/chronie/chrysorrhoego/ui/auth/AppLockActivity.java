package com.chronie.chrysorrhoego.ui.auth;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chronie.chrysorrhoego.R;
import com.chronie.chrysorrhoego.util.BiometricUtil;
import com.chronie.chrysorrhoego.util.SecurityManager;

/**
 * 应用锁定活动，用于会话超时或应用被锁定后重新验证用户身份
 */
public class AppLockActivity extends AppCompatActivity implements BiometricUtil.BiometricCallback {

    private static final String TAG = "AppLockActivity";
    private ProgressBar progressBar;
    private Button unlockButton;
    private TextView appNameText;
    private ImageView logoImageView;
    private SecurityManager securityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);

        initializeViews();
        setupListeners();
        setupSecurityManager();
        
        // 如果启用了生物识别，自动尝试生物识别验证
        if (securityManager.isBiometricEnabled() && BiometricUtil.isBiometricSupported(this)) {
            startBiometricAuthentication();
        }
    }

    private void initializeViews() {
        progressBar = findViewById(R.id.lock_progress_bar);
        unlockButton = findViewById(R.id.unlock_button);
        appNameText = findViewById(R.id.app_name_text);
        logoImageView = findViewById(R.id.app_logo);
    }

    private void setupListeners() {
        unlockButton.setOnClickListener(v -> handleUnlock());
    }

    private void setupSecurityManager() {
        securityManager = SecurityManager.getInstance(this);
    }

    private void handleUnlock() {
        showLoading(true);
        
        // 模拟身份验证过程
        new android.os.Handler().postDelayed(() -> {
            securityManager.unlockApp();
            showLoading(false);
            Toast.makeText(AppLockActivity.this, R.string.unlock_success, Toast.LENGTH_SHORT).show();
            finish(); // 解锁成功后关闭锁定界面
        }, 1000);
    }

    private void startBiometricAuthentication() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            BiometricUtil.authenticateWithBiometric(
                this,
                getString(R.string.verify_identity),
                getString(R.string.app_locked_due_to_inactivity),
                getString(R.string.use_biometric_to_unlock),
                this
            );
        }
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            unlockButton.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            unlockButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAuthenticationSuccess() {
        securityManager.unlockApp();
        Toast.makeText(this, R.string.unlock_success, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onAuthenticationFailed() {
        Toast.makeText(this, R.string.authentication_failed, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAuthenticationError(int errorCode, String errorMessage) {
        Log.e(TAG, "Authentication error: " + errorMessage);
        // 生物识别失败，不显示错误信息，让用户可以点击解锁按钮
    }

    @Override
    public void onBiometricNotAvailable() {
        // 生物识别不可用，不做任何操作
    }

    @Override
    public void onBackPressed() {
        // 锁定状态下不允许返回
    }
}