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
        // 根据CDK API文档，我们需要传递code和username参数
        // 获取username（假设在Activity中有username字段或可以通过其他方式获取）
        String username = getIntent().getStringExtra("username");
        Call<CdkRedeemResponse> call = apiService.redeemCdk(cdk, username != null ? username : "");

        call.enqueue(new Callback<CdkRedeemResponse>() {
            @Override
            public void onResponse(Call<CdkRedeemResponse> call, Response<CdkRedeemResponse> response) {
                showLoading(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    CdkRedeemResponse redeemResponse = response.body();
                    if (redeemResponse.isSuccess()) {
                        // 修复类型转换问题，将getAmount()转换为String
                        String amount = String.valueOf(redeemResponse.getAmount());
                        showRedeemSuccess(cdk, amount);
                    } else {
                        showError(redeemResponse.getMessage() != null ? 
                                redeemResponse.getMessage() : "CDK redemption failed");
                    }
                } else {
                    // 处理HTTP错误
                    try {
                        String errorBody = response.errorBody() != null ? 
                                response.errorBody().string() : "";
                        Log.e(TAG, "Redeem failed with code: " + response.code() + ", body: " + errorBody);
                        
                        // 处理特定的错误情况
                        if (response.code() == 400) {
                            showError("Invalid CDK format");
                        } else if (response.code() == 404) {
                            showError("CDK not found");
                        } else if (response.code() == 409) {
                            showError("CDK already used");
                        } else {
                            showError("CDK redemption failed");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error response", e);
                        showError("CDK redemption failed");
                    }
                }
            }

            @Override
            public void onFailure(Call<CdkRedeemResponse> call, Throwable t) {
                Log.e(TAG, "Redeem network error", t);
                showLoading(false);
                ErrorHandler.handleNetworkError(CdkRedeemActivity.this, t instanceof Exception ? (Exception) t : new Exception(t));
            }
        });
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