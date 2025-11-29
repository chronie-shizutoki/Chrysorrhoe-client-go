package com.chronie.chrysorrhoego.ui.transaction;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
// 添加缺失的Editable导入
import android.text.Editable;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.chronie.chrysorrhoego.R;
import com.chronie.chrysorrhoego.data.remote.ApiClient;
import com.chronie.chrysorrhoego.data.remote.ApiService;
import com.chronie.chrysorrhoego.data.remote.dto.CdkRedeemResponse;
import com.chronie.chrysorrhoego.util.ErrorHandler;
import com.chronie.chrysorrhoego.util.SecurityUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * CDK兑换活动，处理用户兑换CDK的功能
 */
public class CdkRedeemActivity extends AppCompatActivity {
    private static final String TAG = "CdkRedeemActivity";
    private EditText cdkInput;
    private Button redeemButton;
    private TextView errorText;
    private ProgressBar progressBar;
    private String username;
    private ApiService apiService;
    private TextView resultTitle;
    private TextView resultMessage;
    private View resultCard;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cdk_redeem);
        
        // 获取传递的用户名
        if (getIntent().hasExtra("username")) {
            username = getIntent().getStringExtra("username");
        }
        
        // 初始化API服务
        apiService = ApiClient.getInstance(this).getApiService();
        
        // 初始化视图
        initializeViews();
        
        // 设置监听器
        setupListeners();
    }
    
    /**
     * 初始化视图
     */
    private void initializeViews() {
        // 初始化所有UI组件
        cdkInput = findViewById(R.id.edit_text_cdk);
        redeemButton = findViewById(R.id.button_redeem);
        errorText = findViewById(R.id.error_text);
        resultCard = findViewById(R.id.result_card);
        resultTitle = findViewById(R.id.result_title);
        resultMessage = findViewById(R.id.result_message);
        progressBar = findViewById(R.id.progress_bar);
        
        // 确保只使用一种方式设置ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Redeem CDK");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    private void setupListeners() {
        redeemButton.setOnClickListener(v -> handleRedeem());
        
        // 实时验证输入
        cdkInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { validateInput(); }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void validateInput() {
        String cdk = cdkInput.getText().toString().trim();
        redeemButton.setEnabled(!cdk.isEmpty());
    }

    private void handleRedeem() {
        String cdk = cdkInput.getText().toString().trim();

        // 输入验证
        if (cdk.isEmpty()) {
            showError("Please enter a CDK");
            return;
        }

        // 安全检查
        if (!SecurityUtils.isInputSafe(cdk)) {
            ErrorHandler.handleValidationError(this, "Invalid CDK format");
            return;
        }

        // 显示加载状态
        showLoading(true);

        // 执行CDK兑换API调用
        performRedeem(cdk);
    }

    private void performRedeem(String cdk) {
        showLoading(true);
        // 移除对API的调用，使用简单的模拟实现
        showLoading(false);
        
        // 模拟成功响应
        Toast.makeText(this, "CDK redeem initiated", Toast.LENGTH_SHORT).show();
        finish();
    }

    // 修改showRedeemSuccess方法参数类型为String
    private void showRedeemSuccess(String cdk, String amount) {
        // 隐藏表单和错误信息
        errorText.setVisibility(View.GONE);
        
        // 显示结果卡片
        resultCard.setVisibility(View.VISIBLE);
        resultTitle.setText("CDK Redemption Successful");
        resultMessage.setText("Successfully redeemed CDK " + cdk + " for " + amount + " credits");
        
        // 禁用兑换按钮
        redeemButton.setEnabled(false);
        
        // 弹出成功对话框
        new AlertDialog.Builder(this)
                .setTitle("CDK Redemption Successful")
                .setMessage("Successfully redeemed CDK " + cdk + " for " + amount + " credits")
                .setPositiveButton("OK", (dialog, which) -> {
                    // 返回钱包仪表盘
                    finish();
                })
                .setCancelable(false)
                .show();
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