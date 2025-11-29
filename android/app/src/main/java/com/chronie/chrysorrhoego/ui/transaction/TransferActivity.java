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
import android.widget.Toast;

// 添加缺失的Editable导入
import android.text.Editable;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.chronie.chrysorrhoego.R;
import com.chronie.chrysorrhoego.data.remote.ApiClient;
import com.chronie.chrysorrhoego.data.remote.ApiService;
import com.chronie.chrysorrhoego.data.remote.dto.TransferResponse;
import com.chronie.chrysorrhoego.util.ErrorHandler;
import com.chronie.chrysorrhoego.util.SecurityUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 转账活动，处理用户之间的金额转账
 */
public class TransferActivity extends AppCompatActivity {
    private static final String TAG = "TransferActivity";
    private EditText recipientInput;
    private EditText amountInput;
    private EditText memoInput;
    private Button confirmButton;
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
        setContentView(R.layout.activity_transfer);
        
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
        recipientInput = findViewById(R.id.edit_text_recipient);
        amountInput = findViewById(R.id.edit_text_amount);
        memoInput = findViewById(R.id.edit_text_memo);
        confirmButton = findViewById(R.id.button_transfer);
        errorText = findViewById(R.id.error_text);
        resultCard = findViewById(R.id.result_card);
        resultTitle = findViewById(R.id.result_title);
        resultMessage = findViewById(R.id.result_message);
        progressBar = findViewById(R.id.progress_bar);
        
        // 确保只使用一种方式设置ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Transfer");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        
        // 初始启用按钮，让用户可以点击并得到即时反馈
        confirmButton.setEnabled(true);
    }

    private void setupListeners() {
        confirmButton.setOnClickListener(v -> handleTransfer());
        
        // 移除实时输入验证，在用户点击时验证，提供更好的用户体验
        // 保留文本变更监听器用于清除错误信息，但不改变按钮状态
        recipientInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 当用户开始输入时清除错误信息
                if (errorText.getVisibility() == View.VISIBLE) {
                    errorText.setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        
        amountInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 当用户开始输入时清除错误信息
                if (errorText.getVisibility() == View.VISIBLE) {
                    errorText.setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void validateInputs() {
        // 按钮始终保持启用状态，在handleTransfer中执行实际验证
        // 这样用户可以随时点击并获得即时反馈
        confirmButton.setEnabled(true);
    }

    private void handleTransfer() {
        String recipient = recipientInput.getText().toString().trim();
        String amountStr = amountInput.getText().toString().trim();
        String memo = memoInput.getText().toString().trim();

        // 输入验证
        if (recipient.isEmpty() || amountStr.isEmpty()) {
            showError("Please fill in all required fields");
            return;
        }

        // 安全检查
        if (!SecurityUtils.isInputSafe(recipient) || !SecurityUtils.isInputSafe(memo)) {
            ErrorHandler.handleValidationError(this, "Invalid input");
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                showError("Amount must be greater than zero");
                return;
            }

            // 显示加载状态
            showLoading(true);

            // 执行真实的转账API调用
            transferByUsername(recipient, amount, memo);

        } catch (NumberFormatException e) {
            showError("Invalid amount format");
        }
    }

    private void transferByUsername(String recipient, double amount, String memo) {
        // 根据新的API规范，需要提供发送方用户名、接收方用户名、金额和描述
        // 使用username作为发送方用户名（fromUsername）
        Call<TransferResponse> call = apiService.transfer(
                username, // fromUsername - 发送方用户名
                recipient, // toUsername - 接收方用户名
                String.valueOf(amount), // 金额
                memo // description - 描述，对应原来的memo
        );

        call.enqueue(new Callback<TransferResponse>() {
            @Override
            public void onResponse(Call<TransferResponse> call, Response<TransferResponse> response) {
                showLoading(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    TransferResponse transferResponse = response.body();
                    if (transferResponse.isSuccess()) {
                        showTransferSuccess(recipient, amount);
                    } else {
                        showError(transferResponse.getMessage() != null ? 
                                transferResponse.getMessage() : "Transfer failed");
                    }
                } else {
                    // 处理HTTP错误
                    try {
                        String errorBody = response.errorBody() != null ? 
                                response.errorBody().string() : "";
                        Log.e(TAG, "Transfer failed with code: " + response.code() + ", body: " + errorBody);
                        
                        // 处理特定的错误情况
                        if (response.code() == 400) {
                            showError("Invalid input");
                        } else if (response.code() == 404) {
                            showError("Recipient not found");
                        } else if (response.code() == 409) {
                            showError("Insufficient balance");
                        } else {
                            showError("Transfer failed");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error response", e);
                        showError("Transfer failed");
                    }
                }
            }

            @Override
            public void onFailure(Call<TransferResponse> call, Throwable t) {
                Log.e(TAG, "Transfer network error", t);
                showLoading(false);
                ErrorHandler.handleNetworkError(TransferActivity.this, t instanceof Exception ? (Exception) t : new Exception(t));
            }
        });
    }

    private void showTransferSuccess(String recipient, double amount) {
        // 隐藏表单和错误信息
        errorText.setVisibility(View.GONE);
        
        // 显示结果卡片
        resultCard.setVisibility(View.VISIBLE);
        resultTitle.setText("Transfer Successful");
        resultMessage.setText("Successfully transferred " + amount + " to " + recipient);
        
        // 禁用转账按钮
        confirmButton.setEnabled(false);
        
        // 弹出成功对话框
        new AlertDialog.Builder(this)
                .setTitle("Transfer Successful")
                .setMessage("Successfully transferred " + amount + " to " + recipient)
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
            confirmButton.setVisibility(View.INVISIBLE);
            hideError();
        } else {
            progressBar.setVisibility(View.GONE);
            confirmButton.setVisibility(View.VISIBLE);
        }
    }
}