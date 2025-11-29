package com.chronie.chrysorrhoego.ui.transaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chronie.chrysorrhoego.R;
import com.chronie.chrysorrhoego.model.Transaction;
import com.chronie.chrysorrhoego.util.ErrorHandler;
import com.chronie.chrysorrhoego.ui.wallet.WalletDashboardActivity;

import java.util.ArrayList;
import java.util.List;

public class TransactionHistoryActivity extends AppCompatActivity {
    private static final String TAG = "TransactionHistoryActivity";
    
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private Toolbar toolbar;
    private TransactionAdapter adapter;
    private List<Transaction> transactionList;
    private String username;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);
        
        // 获取传递的用户名
        if (getIntent().hasExtra("username")) {
            username = getIntent().getStringExtra("username");
        }
        
        // 初始化视图
        initViews();
        
        // 设置监听器
        setupListeners();
        
        // 初始化RecyclerView
        initRecyclerView();
        
        // 加载交易历史
        loadTransactionHistory();
    }
    
    /**
     * 初始化视图
     */
    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        tvEmpty = findViewById(R.id.tv_empty);
        toolbar = findViewById(R.id.toolbar);
        
        // 设置工具栏
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_transaction_history);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    
    /**
     * 设置监听器
     */
    private void setupListeners() {
        // 工具栏返回按钮
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    
    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        
        transactionList = new ArrayList<>();
        adapter = new TransactionAdapter(this, transactionList);
        recyclerView.setAdapter(adapter);
    }
    
    /**
     * 加载交易历史
     */
    private void loadTransactionHistory() {
        showLoading(true);
        
        // 模拟加载交易历史
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    // 生成模拟交易数据
                    List<Transaction> transactions = generateMockTransactions();
                    showTransactionHistory(transactions);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to load transaction history", e);
                    showLoading(false);
                    ErrorHandler.handleNetworkError(TransactionHistoryActivity.this, e);
                    showEmptyState();
                }
            }
        }, 1500);
    }
    
    /**
     * 生成模拟交易数据
     */
    private List<Transaction> generateMockTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        long currentTime = System.currentTimeMillis();
        long dayInMillis = 24 * 60 * 60 * 1000;
        
        // 添加模拟交易数据
        transactions.add(new Transaction.Builder()
                .id("txn_001")
                .type(Transaction.Type.TRANSFER)
                .amount(100.50)
                .timestamp(currentTime - dayInMillis * 0)
                .note("午餐费用")
                .sender("user123", "我")
                .recipient("user456", "张三")
                .outgoing(true)
                .status(Transaction.Status.COMPLETED)
                .build());
        
        transactions.add(new Transaction.Builder()
                .id("txn_002")
                .type(Transaction.Type.TRANSFER)
                .amount(500.00)
                .timestamp(currentTime - dayInMillis * 1)
                .note("项目报销")
                .sender("user789", "李四")
                .recipient("user123", "我")
                .outgoing(false)
                .status(Transaction.Status.COMPLETED)
                .build());
        
        transactions.add(new Transaction.Builder()
                .id("txn_003")
                .type(Transaction.Type.CDK_REDEEM)
                .amount(200.00)
                .timestamp(currentTime - dayInMillis * 2)
                .note("兑换奖励CDK")
                .outgoing(false)
                .status(Transaction.Status.COMPLETED)
                .build());
        
        transactions.add(new Transaction.Builder()
                .id("txn_004")
                .type(Transaction.Type.TRANSFER)
                .amount(150.75)
                .timestamp(currentTime - dayInMillis * 3)
                .sender("user123", "我")
                .recipient("user101", "王五")
                .outgoing(true)
                .status(Transaction.Status.COMPLETED)
                .build());
        
        transactions.add(new Transaction.Builder()
                .id("txn_005")
                .type(Transaction.Type.DEPOSIT)
                .amount(1000.00)
                .timestamp(currentTime - dayInMillis * 4)
                .note("充值")
                .outgoing(false)
                .status(Transaction.Status.COMPLETED)
                .build());
        
        transactions.add(new Transaction.Builder()
                .id("txn_006")
                .type(Transaction.Type.FEE)
                .amount(5.00)
                .timestamp(currentTime - dayInMillis * 5)
                .note("交易手续费")
                .outgoing(true)
                .status(Transaction.Status.COMPLETED)
                .build());
        
        return transactions;
    }
    
    /**
     * 显示交易历史列表
     */
    private void showTransactionHistory(List<Transaction> transactions) {
        showLoading(false);
        
        if (transactions != null && !transactions.isEmpty()) {
            transactionList.clear();
            transactionList.addAll(transactions);
            adapter.notifyDataSetChanged();
            
            recyclerView.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        } else {
            showEmptyState();
        }
    }
    
    /**
     * 显示空状态
     */
    private void showEmptyState() {
        recyclerView.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.VISIBLE);
    }
    
    /**
     * 显示/隐藏加载状态
     */
    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }
    
    /**
     * 导航到钱包仪表盘
     */
    private void navigateToWalletDashboard() {
        Intent intent = new Intent(TransactionHistoryActivity.this, WalletDashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}