package com.chronie.chrysorrhoego.ui.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.gridlayout.widget.GridLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.chronie.chrysorrhoego.R;
import com.chronie.chrysorrhoego.ui.auth.AuthActivity;
import com.chronie.chrysorrhoego.ui.cdk.CdkRedeemActivity;
import com.chronie.chrysorrhoego.ui.transaction.TransactionHistoryActivity;
import com.chronie.chrysorrhoego.ui.transaction.TransferActivity;

/**
 * 钱包仪表盘活动，显示用户余额和主要功能入口
 */
public class WalletDashboardActivity extends AppCompatActivity {
    private static final String TAG = "WalletDashboardActivity";
    private TextView balanceAmountView;
    private Button transferButton;
    private Button cdkRedeemButton;
    private Button transactionHistoryButton;
    private Button logoutButton;
    private ProgressBar loadingIndicator;
    private GridLayout featuresGrid; // 添加对GridLayout的引用
    private String username;
    private String walletId;
    private double balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_dashboard);

        // 获取从AuthActivity传递的钱包数据
        Intent intent = getIntent();
        if (intent.hasExtra("wallet_id")) {
            walletId = intent.getStringExtra("wallet_id");
        }
        if (intent.hasExtra("username")) {
            username = intent.getStringExtra("username");
        }
        if (intent.hasExtra("balance")) {
            balance = intent.getDoubleExtra("balance", 0.0);
        }

        initializeViews();
        setupListeners();
        loadWalletData();
    }

    private void initializeViews() {
        balanceAmountView = findViewById(R.id.balance_amount);
        transferButton = findViewById(R.id.transfer_button);
        cdkRedeemButton = findViewById(R.id.cdk_redeem_button);
        transactionHistoryButton = findViewById(R.id.transaction_history_button);
        logoutButton = findViewById(R.id.logout_button);
        loadingIndicator = findViewById(R.id.loading_indicator);
        featuresGrid = findViewById(R.id.features_grid); // 初始化GridLayout
        
        // 添加调试日志
        Log.d(TAG, "Button initialization:");
        Log.d(TAG, "transferButton: " + (transferButton != null ? "initialized" : "null"));
        Log.d(TAG, "cdkRedeemButton: " + (cdkRedeemButton != null ? "initialized" : "null"));
        Log.d(TAG, "transactionHistoryButton: " + (transactionHistoryButton != null ? "initialized" : "null"));
        Log.d(TAG, "featuresGrid: " + (featuresGrid != null ? "initialized" : "null"));
        
        // 确保GridLayout可见
        if (featuresGrid != null) {
            featuresGrid.setVisibility(View.VISIBLE);
            Log.d(TAG, "Setting featuresGrid VISIBLE");
        }
        
        // 确保按钮可见
        if (transferButton != null) {
            transferButton.setVisibility(View.VISIBLE);
            Log.d(TAG, "Setting transferButton VISIBLE");
        }
        if (cdkRedeemButton != null) {
            cdkRedeemButton.setVisibility(View.VISIBLE);
            Log.d(TAG, "Setting cdkRedeemButton VISIBLE");
        }
        if (transactionHistoryButton != null) {
            transactionHistoryButton.setVisibility(View.VISIBLE);
            Log.d(TAG, "Setting transactionHistoryButton VISIBLE");
        }
    }

    private void setupListeners() {
        transferButton.setOnClickListener(v -> navigateToTransfer());
        cdkRedeemButton.setOnClickListener(v -> navigateToCdkRedeem());
        transactionHistoryButton.setOnClickListener(v -> navigateToTransactionHistory());
        logoutButton.setOnClickListener(v -> handleLogout());
    }

    private void loadWalletData() {
        showLoading(true);

        // 使用从AuthActivity传递的真实余额数据
        runOnUiThread(() -> {
            showLoading(false);
            // 格式化余额显示，保留两位小数
            balanceAmountView.setText(String.format("%.2f", balance));
            
            // 确保加载数据后按钮仍然可见
            if (transferButton != null) {
                transferButton.setVisibility(View.VISIBLE);
            }
            if (cdkRedeemButton != null) {
                cdkRedeemButton.setVisibility(View.VISIBLE);
            }
            if (transactionHistoryButton != null) {
                transactionHistoryButton.setVisibility(View.VISIBLE);
            }
        });
    }

    private void navigateToTransfer() {
        Intent intent = new Intent(this, TransferActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    private void navigateToCdkRedeem() {
        Intent intent = new Intent(this, CdkRedeemActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    private void navigateToTransactionHistory() {
        Intent intent = new Intent(this, TransactionHistoryActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    private void handleLogout() {
        // 清除登录状态
        Intent intent = new Intent(this, AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showLoading(boolean loading) {
        if (loading) {
            loadingIndicator.setVisibility(View.VISIBLE);
            // 只禁用按钮，不隐藏它们
            if (transferButton != null) transferButton.setEnabled(false);
            if (cdkRedeemButton != null) cdkRedeemButton.setEnabled(false);
            if (transactionHistoryButton != null) transactionHistoryButton.setEnabled(false);
        } else {
            loadingIndicator.setVisibility(View.GONE);
            // 确保按钮启用且可见
            if (transferButton != null) {
                transferButton.setEnabled(true);
                transferButton.setVisibility(View.VISIBLE);
            }
            if (cdkRedeemButton != null) {
                cdkRedeemButton.setEnabled(true);
                cdkRedeemButton.setVisibility(View.VISIBLE);
            }
            if (transactionHistoryButton != null) {
                transactionHistoryButton.setEnabled(true);
                transactionHistoryButton.setVisibility(View.VISIBLE);
            }
            // 确保GridLayout可见
            if (featuresGrid != null) {
                featuresGrid.setVisibility(View.VISIBLE);
            }
        }
    }
}