package com.chronie.chrysorrhoego.ui.wallet;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.chronie.chrysorrhoego.data.remote.ApiService;
import com.chronie.chrysorrhoego.data.remote.RetrofitClient;
import com.chronie.chrysorrhoego.data.repository.RemoteWalletRepository;

public class WalletViewModelFactory implements ViewModelProvider.Factory {
    private static final String TAG = "WalletViewModelFactory";
    private final Context context;

    public WalletViewModelFactory(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(WalletViewModel.class)) {
            try {
                // 初始化RetrofitClient，传入应用上下文
                RetrofitClient.initialize(context.getApplicationContext());
                
                // 使用RetrofitClient获取ApiService实例
                ApiService apiService = RetrofitClient.getInstance().getApiService();
                
                // 创建Repository并注入ApiService和Context
                RemoteWalletRepository repository = new RemoteWalletRepository(apiService, context);
                
                // 创建并返回ViewModel
                return (T) new WalletViewModel(repository);
            } catch (Exception e) {
                Log.e(TAG, "Failed to create WalletViewModel", e);
                throw new RuntimeException("Failed to create WalletViewModel", e);
            }
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
