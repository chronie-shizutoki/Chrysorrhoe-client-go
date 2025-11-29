package com.chronie.chrysorrhoego.ui.transaction;

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

import com.chronie.chrysorrhoego.R;
import com.chronie.chrysorrhoego.util.ErrorHandler;
import com.chronie.chrysorrhoego.util.SecurityUtils;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        // 获取传递的用户名
        if (getIntent().hasExtra("username")) {
            username = getIntent().getStringExtra("username");
        }

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        recipientInput = findViewById(R.id.edit_text_recipient);
        amountInput = findViewById(R.id.edit_text_amount);
        memoInput = findViewById(R.id.edit_text_memo);
        confirmButton = findViewById(R.id.button_transfer);
        errorText = findViewById(R.id.error_text);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setupListeners() {
        confirmButton.setOnClickListener(v -> handleTransfer());
    }

    private void handleTransfer() {
        String recipient = recipientInput.getText().toString().trim();
        String amountStr = amountInput.getText().toString().trim();
        String memo = memoInput.getText().toString().trim();

        // 输入验证
        if (recipient.isEmpty() || amountStr.isEmpty()) {
            showError(getString(R.string.transfer_error));
            return;
        }

        // 安全检查
        if (!SecurityUtils.isInputSafe(recipient) || !SecurityUtils.isInputSafe(memo)) {
            ErrorHandler.handleValidationError(this, getString(R.string.transfer_error_invalid_input));
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                showError(getString(R.string.transfer_error));
                return;
            }

            // 显示加载状态
            showLoading(true);

            // 模拟转账请求
            // 在实际实现中，这里应该调用转账服务
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                try {
                    // 模拟转账成功
                    boolean transferSuccess = true; // 这里将被替换为实际的转账逻辑
                    
                    showLoading(false);
                    if (transferSuccess) {
                        showTransferSuccess();
                    } else {
                        ErrorHandler.handleNetworkError(this, new Exception("Transfer failed"));
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Transfer error", e);
                    showLoading(false);
                    ErrorHandler.handleNetworkError(this, e);
                }
            }, 1500);

        } catch (NumberFormatException e) {
            showError(getString(R.string.transfer_error_invalid_amount));
        }
    }

    private void showTransferSuccess() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.transfer_success)
                .setMessage(R.string.transfer_success_message)
                .setPositiveButton(R.string.ok_button, (dialog, which) -> {
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