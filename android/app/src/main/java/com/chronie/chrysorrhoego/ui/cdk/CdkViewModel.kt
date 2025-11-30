package com.chronie.chrysorrhoego.ui.cdk

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Random

// CDK兑换记录数据类
data class CdkExchangeRecord(
    val code: String,
    val status: String,
    val reward: String,
    val dateTime: String,
    val isSuccessful: Boolean
)

class CdkViewModel : ViewModel() {

    // 兑换结果
    private val _exchangeResult = MutableStateFlow("")
    val exchangeResult: StateFlow<String> = _exchangeResult

    // 加载状态
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // 兑换历史记录
    private val _exchangeHistory = MutableStateFlow<List<CdkExchangeRecord>>(emptyList())
    val exchangeHistory: StateFlow<List<CdkExchangeRecord>> = _exchangeHistory

    // 错误信息
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    // 外部可修改的错误消息属性
    var errorMessage: String?
        get() = _error.value
        set(value) {
            _error.value = value
        }

    // 有效的CDK码模式（用于演示）
    private val validCdkPatterns = listOf(
        "CHRONIE-" to "100金币",
        "CRH-" to "200金币",
        "GIFT-" to "500金币",
        "VIP-" to "VIP会员3天"
    )

    // 已兑换的CDK码（防止重复兑换）
    private val redeemedCodes = mutableSetOf<String>()

    // 执行CDK兑换
    fun exchangeCdk(cdkCode: String) {
        // 验证输入
        val trimmedCode = cdkCode.trim()
        if (trimmedCode.isEmpty()) {
            errorMessage = "请输入CDK码"
            return
        }

        // 重置状态
        _isLoading.value = true
        _exchangeResult.value = ""
        _error.value = null

        // 在后台线程执行兑换逻辑
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 模拟网络请求延迟
                Thread.sleep(1500)

                // 检查是否已兑换
                if (redeemedCodes.contains(trimmedCode)) {
                    _exchangeResult.value = "该CDK已被兑换"
                    _error.value = "兑换失败：CDK已使用"
                    return@launch
                }

                // 验证CDK格式
                if (!isValidCdkFormat(trimmedCode)) {
                    _exchangeResult.value = "CDK格式不正确"
                    _error.value = "兑换失败：无效的CDK格式"
                    return@launch
                }

                // 检查CDK是否在有效模式中
                val reward = getRewardForCdk(trimmedCode)
                if (reward != null) {
                    // 模拟随机失败，增加真实感
                    val random = Random()
                    if (random.nextDouble() < 0.8) { // 80%成功率
                        // 兑换成功
                        redeemedCodes.add(trimmedCode)
                        val record = createExchangeRecord(trimmedCode, "成功", reward, true)
                        addToHistory(record)
                        _exchangeResult.value = "兑换成功！获得$reward"
                    } else {
                        // 随机失败
                        val record = createExchangeRecord(trimmedCode, "失败", "无", false)
                        addToHistory(record)
                        _exchangeResult.value = "兑换失败，请稍后重试"
                        _error.value = "网络错误，请稍后再试"
                    }
                } else {
                    // CDK不存在或无效
                    val record = createExchangeRecord(trimmedCode, "失败", "无", false)
                    addToHistory(record)
                    _exchangeResult.value = "CDK无效或不存在"
                    _error.value = "兑换失败：CDK不存在"
                }
            } catch (e: Exception) {
                _exchangeResult.value = "兑换过程中发生错误"
                _error.value = "错误：${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 验证CDK格式
    private fun isValidCdkFormat(code: String): Boolean {
        // 检查长度和格式
        if (code.length < 8 || code.length > 20) return false
        
        // 检查是否包含有效前缀
        return validCdkPatterns.any { code.startsWith(it.first) }
                || code.matches(Regex("^[A-Z0-9-]{8,20}$"))
    }

    // 获取CDK对应的奖励
    private fun getRewardForCdk(code: String): String? {
        // 检查是否匹配已知模式
        for (pair in validCdkPatterns) {
            val prefix = pair.first
            val reward = pair.second
            if (code.startsWith(prefix)) {
                return reward
            }
        }
        
        // 模拟随机奖励（如果格式有效但没有特定前缀）
        return if (isValidCdkFormat(code)) {
            val rewards = listOf("50金币", "100金币", "能量饮料", "经验加成10%")
            rewards[Random().nextInt(rewards.size)]
        } else {
            null
        }
    }

    // 创建兑换记录
    private fun createExchangeRecord(
        code: String,
        status: String,
        reward: String,
        isSuccessful: Boolean
    ): CdkExchangeRecord {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentTime = dateFormat.format(Date())
        
        return CdkExchangeRecord(
            code = code,
            status = status,
            reward = reward,
            dateTime = currentTime,
            isSuccessful = isSuccessful
        )
    }

    // 将记录添加到历史中
    private fun addToHistory(record: CdkExchangeRecord) {
        val currentHistory = _exchangeHistory.value.orEmpty()
        _exchangeHistory.postValue(listOf(record) + currentHistory)
    }

    // 清除错误信息
    fun clearError() {
        _error.value = null
    }

    // 清除当前结果
    fun clearResult() {
        _exchangeResult.value = ""
    }
}