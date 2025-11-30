package com.chronie.chrysorrhoego.ui.wallet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WalletViewModel : ViewModel() {

    private val _balance = MutableLiveData<String>("0.00")
    val balance: LiveData<String> = _balance

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    init {
        // 初始化时加载钱包数据
        loadWalletData()
    }

    private fun loadWalletData() {
        _isLoading.value = true
        _error.value = null

        // 模拟加载数据
        try {
            // 模拟网络请求延迟
            Thread.sleep(1000)
            _balance.postValue("1000.00")
        } catch (e: Exception) {
            _error.postValue("Failed to load wallet data")
        } finally {
            _isLoading.postValue(false)
        }
    }

    // 刷新钱包数据
    fun refreshData() {
        loadWalletData()
    }
}