package com.chronie.chrysorrhoego.ui.cdk;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.chronie.chrysorrhoego.R;
import com.chronie.chrysorrhoego.util.ErrorHandler;
import com.chronie.chrysorrhoego.util.SecurityUtils;
import com.chronie.chrysorrhoego.ui.wallet.WalletDashboardActivity;

/**
 * CDK兑换活动，处理用户输入CDK码进行兑换
 */
public class CdkRedeemActivity extends AppCompatActivity {
    private static final String TAG = "CdkRedeemActivity";
    private EditText cdkInput;
    private Button redeemButton;
    private TextView errorText;
    private ProgressBar progressBar;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cdk_redeem);

        // 获取传递的用户名
        if (getIntent().hasExtra("username")) {
            username = getIntent().getStringExtra("username");
        }

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        cdkInput = findViewById(R.id.edit_text_cdk);
        redeemButton = findViewById(R.id.button_redeem);
        errorText = findViewById(R.id.error_text);
        progressBar = findViewById(R.id.progress_bar);
        
        // 设置活动标题
        setTitle(R.string.title_cdk_redeem);
    }

    private void setupListeners() {
        // CDK代码输入变化监听器
        cdkInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                hideError();
            }
            
            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
        
        redeemButton.setOnClickListener(v -> handleRedeem());
        
        // 移除对不存在的toolbar的引用
    }

    private void handleRedeem() {
        String cdkCode = cdkInput.getText().toString().trim();

        // 输入验证
        if (!validateInput(cdkCode)) {
            return;
        }
        
        // 安全检查
        if (!SecurityUtils.isInputSafe(cdkCode)) {
            ErrorHandler.handleValidationError(this, getString(R.string.error_input_not_safe));
            return;
        }

        // 显示加载状态
        showLoading(true);

        // 优化的CDK兑换请求处理
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            try {
                // 在实际实现中，这里应该调用CDK兑换服务
                boolean redeemSuccess = true; // 这里将被替换为实际的兑换逻辑
                String rewardMessage = getString(R.string.cdk_success_message);

                showLoading(false);
                if (redeemSuccess) {
                    showRedeemSuccess(rewardMessage);
                } else {
                    ErrorHandler.handleNetworkError(CdkRedeemActivity.this, 
                            new Exception("CDK redeem failed"));
                }
            } catch (Exception e) {
                Log.e(TAG, "CDK redeem error", e);
                showLoading(false);
                ErrorHandler.handleNetworkError(CdkRedeemActivity.this, e);
            }
        }, 1500);
    }
    
    /**
     * 验证输入
     */
    private boolean validateInput(String cdkCode) {
        if (cdkCode.isEmpty()) {
            showError(getString(R.string.error_cdk_code_required));
            return false;
        }
        
        // CDK格式验证
        if (!cdkCode.matches("^[A-Za-z0-9]{6,20}$")) {
            showError(getString(R.string.error_invalid_cdk_format));
            return false;
        }
        
        return true;
    }

    private void showRedeemSuccess(String message) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.cdk_success_title)
                .setMessage(message)
                .setPositiveButton(R.string.ok_button, (dialog, which) -> {
                    // 清空输入并导航到钱包仪表盘
                    cdkInput.setText("");
                    navigateToWalletDashboard();
                })
                .setCancelable(false)
                .show();
    }
    
    /**
     * 导航到钱包仪表盘
     */
    private void navigateToWalletDashboard() {
        Intent intent = new Intent(CdkRedeemActivity.this, WalletDashboardActivity.class);
        intent.putExtra("username", username);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void showError(String message) {
        errorText.setText(message);
        errorText.setVisibility(View.VISIBLE);
    }
    
    private void hideError() {
        errorText.setVisibility(View.GONE);
    }

    private void showLoading(boolean loading) {
        if (loading) {
            progressBar.setVisibility(View.VISIBLE);
            redeemButton.setVisibility(View.INVISIBLE);
            hideError();
        } else {
            progressBar.setVisibility(View.GONE);
            redeemButton.setVisibility(View.VISIBLE);
        }
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}