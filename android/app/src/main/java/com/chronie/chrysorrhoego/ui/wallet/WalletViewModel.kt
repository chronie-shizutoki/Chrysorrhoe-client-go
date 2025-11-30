package com.chronie.chrysorrhoego.ui.wallet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WalletViewModel : ViewModel() {

    data class Wallet(
        val id: String,
        val username: String,
        val balance: String,
        val createdAt: String
    )

    private val _wallet = MutableLiveData<Wallet?>(null)
    val wallet: LiveData<Wallet?> = _wallet

    private val _balance = MutableLiveData<String>("0.00")
    val balance: LiveData<String> = _balance

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val _transferSuccess = MutableLiveData(false)
    val transferSuccess: LiveData<Boolean> = _transferSuccess

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
            
            // 创建模拟钱包数据
            val mockWallet = Wallet(
                id = "mock-wallet-id",
                username = "DemoUser",
                balance = "1000.00",
                createdAt = "2024-01-01T00:00:00Z"
            )
            
            _wallet.postValue(mockWallet)
            _balance.postValue(mockWallet.balance)
        } catch (e: Exception) {
            _error.postValue("Failed to load wallet data")
        } finally {
            _isLoading.postValue(false)
        }
    }

    // 刷新钱包数据
    fun refreshData() {
        _transferSuccess.value = false
        loadWalletData()
    }

    // 转账功能
    fun transfer(toUsername: String, amount: Double) {
        _isLoading.value = true
        _error.value = null

        try {
            // 模拟网络请求延迟
            Thread.sleep(1500)
            
            val currentWallet = _wallet.value
            if (currentWallet != null) {
                val currentBalance = currentWallet.balance.toDouble()
                
                // 检查余额是否足够
                if (amount > currentBalance) {
                    _error.postValue("Insufficient funds")
                } else {
                    // 模拟转账成功，更新余额
                    val newBalance = currentBalance - amount
                    val updatedWallet = currentWallet.copy(balance = String.format("%.2f", newBalance))
                    
                    _wallet.postValue(updatedWallet)
                    _balance.postValue(updatedWallet.balance)
                    _transferSuccess.postValue(true)
                }
            }
        } catch (e: Exception) {
            _error.postValue("Transfer failed: ${e.message}")
        } finally {
            _isLoading.postValue(false)
        }
    }

    // 清除错误信息
    fun clearError() {
        _error.value = null
    }
}