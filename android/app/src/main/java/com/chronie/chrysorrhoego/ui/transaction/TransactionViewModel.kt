package com.chronie.chrysorrhoego.ui.transaction

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TransactionViewModel : ViewModel() {

    data class Transaction(
        val id: String,
        val type: String, // "SEND" or "RECEIVE"
        val amount: String,
        val date: String,
        val status: String,
        val recipient: String? = null, // 发送时的收款人
        val sender: String? = null,    // 接收时的发送人
        val description: String? = null // 交易描述
    )

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions

    private val _filteredTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val filteredTransactions: StateFlow<List<Transaction>> = _filteredTransactions

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    // 添加公共的errorMessage属性用于修改错误信息
    var errorMessage: String?
        get() = _error.value
        set(value) {
            _error.value = value
        }

    private var currentFilterType: String = "ALL" // "ALL", "SEND", "RECEIVE"
    private var currentSearchQuery: String = ""

    init {
        loadTransactions()
    }

    fun loadTransactions() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // 模拟网络延迟
                delay(1000)
                // 生成更丰富的模拟交易数据
                val mockTransactions = generateMockTransactions()
                _transactions.value = mockTransactions
                applyFilterAndSearch(mockTransactions)
            } catch (e: Exception) {
                _error.value = "加载交易记录失败：${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshTransactions() {
        loadTransactions()
    }

    // 按交易类型筛选
    fun filterByType(type: String) {
        currentFilterType = type
        val transactionsList = _transactions.value ?: emptyList()
        applyFilterAndSearch(transactionsList)
    }

    // 搜索交易记录
    fun searchTransactions(query: String) {
        currentSearchQuery = query.lowercase()
        val transactionsList = _transactions.value ?: emptyList()
        applyFilterAndSearch(transactionsList)
    }

    // 重置筛选和搜索
    fun resetFilters() {
        currentFilterType = "ALL"
        currentSearchQuery = ""
        _filteredTransactions.value = _transactions.value
    }

    // 应用筛选和搜索
    private fun applyFilterAndSearch(transactions: List<Transaction>) {
        var filteredList = transactions
        
        // 应用类型筛选
        if (currentFilterType != "ALL") {
            filteredList = filteredList.filter { it.type == currentFilterType }
        }
        
        // 应用搜索
        if (currentSearchQuery.isNotEmpty()) {
            filteredList = filteredList.filter {
                it.recipient?.lowercase()?.contains(currentSearchQuery) == true ||
                it.sender?.lowercase()?.contains(currentSearchQuery) == true ||
                it.description?.lowercase()?.contains(currentSearchQuery) == true ||
                it.amount.contains(currentSearchQuery) ||
                it.date.contains(currentSearchQuery) ||
                it.id.contains(currentSearchQuery)
            }
        }
        
        _filteredTransactions.value = filteredList
    }

    // 生成模拟交易数据
    private fun generateMockTransactions(): List<Transaction> {
        val random = Random()
        val types = listOf("SEND", "RECEIVE")
        val statuses = listOf("COMPLETED", "PENDING", "FAILED")
        val descriptions = listOf(
            "日常转账", "工资收入", "购物", "餐饮消费", "房租支付", 
            "礼品", "退款", "投资收益", "服务费", "充值"
        )
        val usernames = listOf(
            "alice", "bob", "charlie", "david", "eve", 
            "frank", "grace", "henry", "ivy", "jack"
        )
        
        val transactions = mutableListOf<Transaction>()
        
        // 生成30条模拟交易记录
        for (i in 1..30) {
            val type = types[random.nextInt(types.size)]
            val amount = String.format("%.2f", random.nextDouble() * 500 + 10)
            val date = generateRandomDate()
            val status = statuses[random.nextInt(statuses.size)]
            val description = descriptions[random.nextInt(descriptions.size)]
            
            val transaction = if (type == "SEND") {
                Transaction(
                    id = i.toString(),
                    type = type,
                    amount = amount,
                    date = date,
                    status = status,
                    recipient = usernames[random.nextInt(usernames.size)],
                    description = description
                )
            } else {
                Transaction(
                    id = i.toString(),
                    type = type,
                    amount = amount,
                    date = date,
                    status = status,
                    sender = usernames[random.nextInt(usernames.size)],
                    description = description
                )
            }
            
            transactions.add(transaction)
        }
        
        // 按日期降序排序
        return transactions.sortedByDescending { it.date }
    }

    // 生成随机日期（最近30天内）
    private fun generateRandomDate(): String {
        val calendar = Calendar.getInstance()
        val random = Random()
        
        // 随机减去0-30天
        val daysToSubtract = random.nextInt(30)
        calendar.add(Calendar.DAY_OF_YEAR, -daysToSubtract)
        
        // 随机时间
        calendar.set(Calendar.HOUR_OF_DAY, random.nextInt(24))
        calendar.set(Calendar.MINUTE, random.nextInt(60))
        
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    // 添加新交易（用于从钱包转账后更新交易记录）
    fun addNewTransaction(transaction: Transaction) {
        val currentList = _transactions.value?.toMutableList() ?: mutableListOf()
        currentList.add(0, transaction) // 添加到列表开头
        _transactions.value = currentList
        applyFilterAndSearch(currentList)
    }
}