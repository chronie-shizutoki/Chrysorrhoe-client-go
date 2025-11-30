package com.chronie.chrysorrhoego.ui.transaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TransactionViewModel : ViewModel() {

    data class Transaction(
        val id: String,
        val type: String, // "SEND" or "RECEIVE"
        val amount: String,
        val date: String,
        val status: String
    )

    private val _transactions = MutableLiveData<List<Transaction>>(emptyList())
    val transactions: LiveData<List<Transaction>> = _transactions

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadTransactions()
    }

    private fun loadTransactions() {
        _isLoading.value = true

        // 模拟加载交易数据
        try {
            // 模拟网络请求延迟
            Thread.sleep(1000)
            val mockTransactions = listOf(
                Transaction("1", "SEND", "50.00", "2024-11-30 10:30", "COMPLETED"),
                Transaction("2", "RECEIVE", "100.00", "2024-11-29 15:45", "COMPLETED"),
                Transaction("3", "SEND", "25.50", "2024-11-28 09:15", "COMPLETED")
            )
            _transactions.postValue(mockTransactions)
        } catch (e: Exception) {
            // 处理错误
        } finally {
            _isLoading.postValue(false)
        }
    }

    fun refreshTransactions() {
        loadTransactions()
    }
}