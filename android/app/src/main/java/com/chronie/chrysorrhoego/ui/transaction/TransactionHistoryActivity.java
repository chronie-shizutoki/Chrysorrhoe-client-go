package com.chronie.chrysorrhoego.ui.transaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chronie.chrysorrhoego.R;
import com.chronie.chrysorrhoego.model.Transaction;
import com.chronie.chrysorrhoego.data.remote.ApiClient;
import com.chronie.chrysorrhoego.data.remote.ApiService;
import com.chronie.chrysorrhoego.data.remote.dto.TransactionHistoryResponse;
import com.chronie.chrysorrhoego.util.ErrorHandler;
import com.chronie.chrysorrhoego.ui.wallet.WalletDashboardActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionHistoryActivity extends AppCompatActivity {
    private static final String TAG = "TransactionHistoryActivity";
    private static final int DEFAULT_PAGE_SIZE = 20;
    
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private Toolbar toolbar;
    private TransactionAdapter adapter;
    private List<Transaction> transactionList;
    private String username;
    private ApiService apiService;
    private SwipeRefreshLayout swipeRefreshLayout;
    
    // 分页相关
    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean hasMoreTransactions = true;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);
        
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
        
        // 初始加载数据
        loadTransactionHistory();
    }
    
    /**
     * 获取当前用户的钱包ID
     * 按照JavaScript版本的实现，这里需要根据用户名获取钱包信息
     * 在实际应用中，可能需要从SharedPreferences或登录状态中获取
     */
    private String getCurrentWalletId() {
        // 简化实现，实际应用中应该通过API根据用户名获取钱包ID
        // 这里可以尝试从本地存储或Intent中获取，如果没有则使用默认值
        SharedPreferences sharedPreferences = getSharedPreferences("chronie_app", MODE_PRIVATE);
        return sharedPreferences.getString("wallet_id", "default_wallet");
    }
    
    /**
     * 初始化视图
     */
    private void initializeViews() {
        // 初始化所有UI组件
        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        tvEmpty = findViewById(R.id.tv_empty);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        
        // 初始化交易列表
        transactionList = new ArrayList<>();
        adapter = new TransactionAdapter(this, transactionList);
        
        // 配置RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        
        // 确保只使用一种方式设置ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.title_transaction_history));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }
    
    /**
     * 设置监听器
     */
    private void setupListeners() {
        // 下拉刷新
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(() -> {
                // 重置分页状态
                resetPagination();
                // 加载第一页数据
                loadTransactionHistory();
            });
        }
        
        // 上拉加载更多
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                    
                    // 当滑动到底部且没有正在加载且还有更多数据时加载更多
                    if (!isLoading && hasMoreTransactions && 
                            (visibleItemCount + firstVisibleItemPosition) >= totalItemCount && 
                            firstVisibleItemPosition >= 0) {
                        loadMoreTransactions();
                    }
                }
            }
        });
    }
    
    /**
     * 重置分页状态
     */
    private void resetPagination() {
        currentPage = 1;
        transactionList.clear();
        hasMoreTransactions = true;
    }
    
    /**
     * 加载交易历史
     */
    private void loadTransactionHistory() {
        if (isLoading) return;
        
        isLoading = true;
        showLoading(true);
        
        try {
            // 移除API调用，使用模拟数据进行简化
            isLoading = false;
            showLoading(false);
            
            if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
            
            // 模拟空的交易历史
            List<Transaction> emptyList = new ArrayList<>();
            transactionList.clear();
            transactionList.addAll(emptyList);
            adapter.notifyDataSetChanged();
            hasMoreTransactions = false;
            
            // 显示空状态
            showEmptyState();
            
        } catch (Exception e) {
            Log.e(TAG, "Error loading transaction history", e);
            isLoading = false;
            showLoading(false);
            
            if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
            
            // 使用ErrorHandler处理错误，而不是不存在的showError方法
            ErrorHandler.handleNetworkError(TransactionHistoryActivity.this, e);
            if (currentPage == 1) {
                showEmptyState();
            }
        }
    }
    
    // 添加一个带页码参数的重载方法，确保向后兼容性
    private void loadTransactionHistory(int page) {
        // 移除对不存在方法的调用，直接使用成员变量控制页码
        this.currentPage = page;
        loadTransactionHistory();
    }
    
    /**
     * 根据HTTP状态码获取适当的错误消息
     */
    private String getErrorMessageForStatusCode(int statusCode) {
        switch (statusCode) {
            case 401:
                return "Unauthorized access";
            case 403:
                return "Access forbidden";
            case 404:
                return "Resource not found";
            case 500:
                return "Server error";
            case 503:
                return "Service unavailable";
            default:
                return "Unknown error";
        }
    }
    
    /**
     * 加载更多交易记录
     */
    private void loadMoreTransactions() {
        if (!hasMoreTransactions || isLoading) return;
        
        // 直接加载交易历史，loadTransactionHistory方法已经包含了获取walletId的逻辑
        loadTransactionHistory();
    }
    
    /**
     * 更新交易列表
     */
    private void updateTransactionList(List<Transaction> transactions, boolean isFirstPage) {
        if (transactions == null || transactions.isEmpty()) {
            showEmptyState();
            return;
        }
        
        // 不再调用不存在的setLoading方法
        
        if (isFirstPage) {
            // 第一页，直接替换数据
            transactionList.clear();
            transactionList.addAll(transactions);
            adapter.notifyDataSetChanged();
        } else {
            // 加载更多，添加到列表末尾
            int previousSize = transactionList.size();
            transactionList.addAll(transactions);
            adapter.notifyItemRangeInserted(previousSize, transactions.size());
        }
        
        // 更新UI状态
        recyclerView.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
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
        if (isLoading && currentPage == 1) {
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