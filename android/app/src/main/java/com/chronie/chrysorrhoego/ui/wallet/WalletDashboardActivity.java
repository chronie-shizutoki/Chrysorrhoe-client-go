package com.chronie.chrysorrhoego.ui.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.viewpager.widget.ViewPager;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.tabs.TabLayout;

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
    private TabLayout featureTabs; // TabLayout引用
    private View transferContainer; // 转账功能容器
    private View cdkRedeemContainer; // CDK储值功能容器
    private View transactionHistoryContainer; // 交易记录功能容器
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
        featureTabs = findViewById(R.id.feature_tabs); // 初始化TabLayout
        transferContainer = findViewById(R.id.transfer_container);
        cdkRedeemContainer = findViewById(R.id.cdk_redeem_container);
        transactionHistoryContainer = findViewById(R.id.transaction_history_container);
        
        // 添加调试日志
        Log.d(TAG, "UI initialization:");
        Log.d(TAG, "transferButton: " + (transferButton != null ? "initialized" : "null"));
        Log.d(TAG, "cdkRedeemButton: " + (cdkRedeemButton != null ? "initialized" : "null"));
        Log.d(TAG, "transactionHistoryButton: " + (transactionHistoryButton != null ? "initialized" : "null"));
        Log.d(TAG, "featureTabs: " + (featureTabs != null ? "initialized" : "null"));
        
        // 确保初始状态下只有转账容器可见
        if (transferContainer != null) {
            transferContainer.setVisibility(View.VISIBLE);
        }
        if (cdkRedeemContainer != null) {
            cdkRedeemContainer.setVisibility(View.GONE);
        }
        if (transactionHistoryContainer != null) {
            transactionHistoryContainer.setVisibility(View.GONE);
        }
        
        // 设置TabLayout监听器
        setupTabLayoutListener();
    }

    private void setupTabLayoutListener() {
        if (featureTabs != null) {
            featureTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    int position = tab.getPosition();
                    updateFeatureVisibility(position);
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    // 不需要特别处理
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                    // 不需要特别处理
                }
            });
        }
    }

    private void updateFeatureVisibility(int tabPosition) {
        // 隐藏所有容器
        if (transferContainer != null) transferContainer.setVisibility(View.GONE);
        if (cdkRedeemContainer != null) cdkRedeemContainer.setVisibility(View.GONE);
        if (transactionHistoryContainer != null) transactionHistoryContainer.setVisibility(View.GONE);
        
        // 根据选择的标签显示对应的容器
        switch (tabPosition) {
            case 0: // 转账标签
                if (transferContainer != null) transferContainer.setVisibility(View.VISIBLE);
                break;
            case 1: // CDK储值标签
                if (cdkRedeemContainer != null) cdkRedeemContainer.setVisibility(View.VISIBLE);
                break;
            case 2: // 交易记录标签
                if (transactionHistoryContainer != null) transactionHistoryContainer.setVisibility(View.VISIBLE);
                break;
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
            
            // 确保加载数据后只有转账容器可见
            if (transferContainer != null) transferContainer.setVisibility(View.VISIBLE);
            if (cdkRedeemContainer != null) cdkRedeemContainer.setVisibility(View.GONE);
            if (transactionHistoryContainer != null) transactionHistoryContainer.setVisibility(View.GONE);
            
            // 确保按钮可见
            if (transferButton != null) transferButton.setVisibility(View.VISIBLE);
            if (cdkRedeemButton != null) cdkRedeemButton.setVisibility(View.VISIBLE);
            if (transactionHistoryButton != null) transactionHistoryButton.setVisibility(View.VISIBLE);
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
            // 确保初始状态下只有转账容器可见
            if (transferContainer != null) transferContainer.setVisibility(View.VISIBLE);
            if (cdkRedeemContainer != null) cdkRedeemContainer.setVisibility(View.GONE);
            if (transactionHistoryContainer != null) transactionHistoryContainer.setVisibility(View.GONE);
            if (featureTabs != null) {
                featureTabs.getTabAt(0).select(); // 选择第一个标签
            }
        }
    }
}