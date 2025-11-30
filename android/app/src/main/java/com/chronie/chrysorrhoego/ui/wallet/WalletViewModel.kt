package com.chronie.chrysorrhoego.ui.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WalletViewModel : ViewModel() {

    data class Wallet(
        val id: String,
        val username: String,
        val balance: String,
        val createdAt: String
    )

    private val _wallet = MutableStateFlow<Wallet?>(null)
    val wallet: StateFlow<Wallet?> = _wallet.asStateFlow()

    private val _balance = MutableStateFlow<String>("0.00")
    val balance: StateFlow<String> = _balance.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    // 让错误状态可从外部修改
    var errorMessage: String?
        get() = _error.value
        set(value) { _error.value = value }

    private val _transferSuccess = MutableStateFlow(false)
    val transferSuccess: StateFlow<Boolean> = _transferSuccess.asStateFlow()

    init {
        // 初始化时加载钱包数据
        loadWalletData()
    }

    private fun loadWalletData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // 模拟加载数据
                // 模拟网络请求延迟
                Thread.sleep(1000)
                
                // 创建模拟钱包数据
                val mockWallet = Wallet(
                    id = "mock-wallet-id",
                    username = "DemoUser",
                    balance = "1000.00",
                    createdAt = "2024-01-01T00:00:00Z"
                )
                
                _wallet.value = mockWallet
                _balance.value = mockWallet.balance
            } catch (e: Exception) {
                _error.value = "Failed to load wallet data"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 刷新钱包数据
    fun refreshData() {
        _transferSuccess.value = false
        loadWalletData()
    }

    // 转账功能
    fun transfer(toUsername: String, amount: Double) {
        viewModelScope.launch {
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
                        _error.value = "Insufficient funds"
                    } else {
                        // 模拟转账成功，更新余额
                        val newBalance = currentBalance - amount
                        val updatedWallet = currentWallet.copy(balance = String.format("%.2f", newBalance))
                        
                        _wallet.value = updatedWallet
                        _balance.value = updatedWallet.balance
                        _transferSuccess.value = true
                    }
                }
            } catch (e: Exception) {
                _error.value = "Transfer failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 清除错误信息
    fun clearError() {
        _error.value = null
    }
}