package com.chronie.chrysorrhoego.ui.wallet;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.chronie.chrysorrhoego.data.repository.RemoteWalletRepository;
import com.chronie.chrysorrhoego.model.Transaction;
import com.chronie.chrysorrhoego.model.Wallet;
import com.chronie.chrysorrhoego.data.remote.dto.TransferRequest;
import com.chronie.chrysorrhoego.data.remote.dto.CdkRedeemRequest;
import com.chronie.chrysorrhoego.util.BackgroundTaskExecutor;
import com.chronie.chrysorrhoego.util.PerformanceMonitor;

import java.util.List;

// RxJava imports temporarily removed

public class WalletViewModel extends ViewModel {
    private static final String TAG = "WalletViewModel";

    // 数据状态LiveData
    private final MutableLiveData<Wallet> walletData = new MutableLiveData<>();
    private final MutableLiveData<List<Transaction>> recentTransactions = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> transferSuccess = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> cdkSuccess = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isRefreshing = new MutableLiveData<>(false);

    // 仓库和处理工具
    private final RemoteWalletRepository repository;
    // private final CompositeDisposable disposable = new CompositeDisposable(); // RxJava dependency removed
    private final BackgroundTaskExecutor executor;

    public WalletViewModel(RemoteWalletRepository repository) {
        this.repository = repository;
        this.executor = BackgroundTaskExecutor.getInstance();
    }

    // 获取LiveData实例（只读）
    public LiveData<Wallet> getWalletData() {
        return walletData;
    }

    public LiveData<List<Transaction>> getRecentTransactions() {
        return recentTransactions;
    }

    public LiveData<Boolean> isLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getTransferSuccess() {
        return transferSuccess;
    }

    public LiveData<Boolean> getCdkSuccess() {
        return cdkSuccess;
    }
    
    public LiveData<Boolean> isRefreshing() {
        return isRefreshing;
    }

    /**
     * 加载钱包数据
     */
    public void loadWalletData() {
        loadWalletData(false);
    }
    
    /**
     * 加载钱包数据
     * @param isRefresh 是否是刷新操作
     */
    public void loadWalletData(boolean isRefresh) {
        // 更新加载状态
        updateLoadingState(true, isRefresh);
        errorMessage.setValue(null);
        
        // 开始性能监控
        String operationName = "loadWalletData" + (isRefresh ? "(refresh)" : "");
        PerformanceMonitor.getInstance().startMethodMonitoring(operationName);

        // 直接处理返回值，不再使用RxJava的subscribe
        try {
            Wallet wallet = repository.getWalletInfo();
            walletData.setValue(wallet);
            updateLoadingState(false, isRefresh);
            
            // 结束性能监控
            long executionTime = PerformanceMonitor.getInstance().endMethodMonitoring(operationName);
            if (executionTime > 1000) {
                Log.w(TAG, "Slow wallet data loading: " + executionTime + "ms");
            }
        } catch (Exception e) {
            errorMessage.setValue(e.getMessage());
            updateLoadingState(false, isRefresh);
            
            // 结束性能监控
            PerformanceMonitor.getInstance().endMethodMonitoring(operationName);
            Log.e(TAG, "Error loading wallet data: " + e.getMessage(), e);
        }

    }

    /**
     * 加载最近交易记录
     */
    public void loadRecentTransactions() {
        loadRecentTransactions(false);
    }
    
    /**
     * 加载最近交易记录
     * @param isRefresh 是否是刷新操作
     */
    public void loadRecentTransactions(boolean isRefresh) {
        // 更新加载状态（仅当不是刷新操作时设置loading状态）
        if (!isRefresh) {
            isLoading.postValue(true);
        }
        
        // 开始性能监控
        String operationName = "loadRecentTransactions" + (isRefresh ? "(refresh)" : "");
        PerformanceMonitor.getInstance().startMethodMonitoring(operationName);

        // 直接处理返回值，不再使用RxJava的subscribe
        try {
            List<Transaction> transactions = repository.getTransactionHistory(1, 20);
            // 只取最近5条交易记录
            if (transactions != null && transactions.size() > 5) {
                recentTransactions.setValue(transactions.subList(0, 5));
            } else {
                recentTransactions.setValue(transactions);
            }
            
            // 更新加载状态
            if (!isRefresh) {
                isLoading.postValue(false);
            }
            
            // 结束性能监控
            long executionTime = PerformanceMonitor.getInstance().endMethodMonitoring(operationName);
            if (executionTime > 1000) {
                Log.w(TAG, "Slow transactions loading: " + executionTime + "ms");
            }
        } catch (Exception e) {
            // 静默失败，不显示错误消息，只使用缓存数据
            try {
                recentTransactions.setValue(repository.getCachedTransactionHistory());
            } catch (Exception cacheException) {
                Log.e(TAG, "Failed to get cached transactions", cacheException);
            }
            
            // 更新加载状态
            if (!isRefresh) {
                isLoading.postValue(false);
            }
            
            // 结束性能监控
            PerformanceMonitor.getInstance().endMethodMonitoring(operationName);
            Log.e(TAG, "Error loading transactions: " + e.getMessage(), e);
        }
    }

    /**
     * 执行转账操作
     */
    public void executeTransfer(TransferRequest request) {
        setLoading(true);
        errorMessage.setValue(null);
        transferSuccess.setValue(false);

        // 开始性能监控
        PerformanceMonitor.getInstance().startMethodMonitoring("executeTransfer");

        // 直接处理返回值，不再使用RxJava的subscribe
        try {
            Transaction response = repository.transfer(request.getRecipient(), request.getAmount(), request.getMemo());
            transferSuccess.setValue(true);
            // 转账成功后重新加载钱包数据
            loadWalletData();
            setLoading(false);
            
            // 结束性能监控
            PerformanceMonitor.getInstance().endMethodMonitoring("executeTransfer");
        } catch (Exception e) {
            errorMessage.setValue(e.getMessage());
            setLoading(false);
            
            // 结束性能监控
            PerformanceMonitor.getInstance().endMethodMonitoring("executeTransfer");
            Log.e(TAG, "Error executing transfer: " + e.getMessage(), e);
        }
    }

    /**
     * 兑换CDK
     */
    public void redeemCdk(CdkRedeemRequest request) {
        setLoading(true);
        errorMessage.setValue(null);
        cdkSuccess.setValue(false);

        // 开始性能监控
        PerformanceMonitor.getInstance().startMethodMonitoring("redeemCdk");

        // 直接处理返回值，不再使用RxJava的subscribe
        try {
            Transaction response = repository.redeemCdk(request.getCdk());
            cdkSuccess.setValue(true);
            // 兑换成功后重新加载钱包数据
            loadWalletData();
            setLoading(false);
            
            // 结束性能监控
            PerformanceMonitor.getInstance().endMethodMonitoring("redeemCdk");
        } catch (Exception e) {
            errorMessage.setValue(e.getMessage());
            setLoading(false);
            
            // 结束性能监控
            PerformanceMonitor.getInstance().endMethodMonitoring("redeemCdk");
            Log.e(TAG, "Error redeeming CDK: " + e.getMessage(), e);
        }
    }

    /**
     * 重置转账成功状态
     */
    public void resetTransferSuccess() {
        transferSuccess.setValue(false);
    }

    /**
     * 重置CDK兑换成功状态
     */
    public void resetCdkSuccess() {
        cdkSuccess.setValue(false);
    }

    /**
     * 设置加载状态
     */
    private void setLoading(boolean loading) {
        isLoading.postValue(loading);
    }
    
    /**
     * 更新加载状态
     */
    private void updateLoadingState(boolean loading, boolean refresh) {
        if (refresh) {
            isRefreshing.postValue(loading);
        } else {
            isLoading.postValue(loading);
        }
    }
    
    /**
     * 刷新所有钱包数据
     */
    public void refreshAllData() {
        // 避免并发刷新
        if (isRefreshing.getValue() == Boolean.TRUE) {
            return;
        }
        
        // 先清除错误信息
        errorMessage.setValue(null);
        
        // 同时刷新钱包信息和交易记录
        loadWalletData(true);
        loadRecentTransactions(true);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // disposable.dispose(); // RxJava dependency removed
        Log.d(TAG, "WalletViewModel cleared");
    }
}
