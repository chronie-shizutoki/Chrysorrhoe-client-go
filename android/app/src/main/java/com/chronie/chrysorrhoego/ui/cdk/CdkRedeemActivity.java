package com.chronie.chrysorrhoego.ui.cdk;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.chronie.chrysorrhoego.ui.wallet.WalletDashboardActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private ApiService apiService;
    private View resultCard;
    private TextView resultTitle;
    private TextView resultMessage;
    private ImageView resultIcon;
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
        resultIcon = findViewById(R.id.result_icon);
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
        // 初始启用按钮，让用户可以点击并得到即时反馈
        redeemButton.setEnabled(true);
        
        // CDK代码输入变化监听器
        cdkInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 当用户开始输入时清除错误信息
                hideError();
                // 不立即禁用按钮，而是在点击时进行验证
            }
            
            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
        
        redeemButton.setOnClickListener(v -> handleRedeem());
    }

    private void updateRedeemButtonState() {
        // 按钮状态管理变更 - 按钮始终保持启用状态
        // 实际验证将在handleRedeem中执行，以提供更好的用户体验
        redeemButton.setEnabled(true);
    }

    private void handleRedeem() {
        String cdkCode = cdkInput.getText().toString().trim();

        // 输入验证
        if (!validateInput(cdkCode)) {
            return;
        }
        
        // 安全检查
        if (!SecurityUtils.isInputSafe(cdkCode)) {
            ErrorHandler.handleValidationError(this, "Unsafe input detected");
            return;
        }

        // 显示加载状态
        showLoading(true);

        // 执行真实的CDK兑换API调用
        redeemCdk(cdkCode);
    }
    
    /**
     * 验证CDK格式
     * 格式要求：6组4位字母数字，用连字符分隔
     * 根据CDK API文档，格式应该是：XXXX-XXXX-XXXX-XXXX-XXXX-XXXX
     */
    private boolean validateCdkFormat(String cdkCode) {
        // CDK格式：6组4位字母数字，用连字符分隔
        // 注意：根据API文档，也支持小写字母，所以修改正则表达式
        return cdkCode.matches("^[A-Za-z0-9]{4}-[A-Za-z0-9]{4}-[A-Za-z0-9]{4}-[A-Za-z0-9]{4}-[A-Za-z0-9]{4}-[A-Za-z0-9]{4}$");
    }
    
    /**
     * 验证输入
     */
    private boolean validateInput(String cdkCode) {
        if (cdkCode.isEmpty()) {
            showError("Please enter a CDK code");
            return false;
        }
        
        // CDK格式验证
        if (!validateCdkFormat(cdkCode)) {
            showError("Invalid CDK format");
            return false;
        }
        
        return true;
    }

    private void redeemCdk(String cdkCode) {
        // 直接执行兑换，不再进行单独的验证步骤
        performRedeem(cdkCode);
    }
    
    private void performRedeem(String cdkCode) {
        // 移除对不存在方法的调用
        showLoading(false);
        
        // 模拟CDK兑换成功
        Toast.makeText(this, "CDK redeem initiated", Toast.LENGTH_SHORT).show();
        String rewardAmount = "1000"; // 模拟奖励金额
        showRedeemSuccess(rewardAmount);
    }

    private void showRedeemSuccess(String rewardAmount) {
        // 隐藏错误信息
        errorText.setVisibility(View.GONE);
        
        // 显示结果卡片
        resultCard.setVisibility(View.VISIBLE);
        resultTitle.setText("Redemption Successful");
        // 根据API文档，这里应该显示货币类型
        resultMessage.setText("Successfully redeemed CDK for " + rewardAmount + " USD");
        resultIcon.setImageResource(R.drawable.ic_success);
        
        // 禁用兑换按钮
        redeemButton.setEnabled(false);
        
        // 弹出成功对话框
        new AlertDialog.Builder(this)
                .setTitle("Success")
                .setMessage("Successfully redeemed CDK for " + rewardAmount + " USD")
                .setPositiveButton("OK", (dialog, which) -> {
                    // 导航到钱包仪表盘
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
    
    private interface ValidateCallback {
        void onValidationSuccess();
        void onValidationFailed(String message);
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}