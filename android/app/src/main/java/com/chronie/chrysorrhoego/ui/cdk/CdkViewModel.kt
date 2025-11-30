package com.chronie.chrysorrhoego.ui.cdk

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CdkViewModel : ViewModel() {

    private val _exchangeResult = MutableLiveData("")
    val exchangeResult: LiveData<String> = _exchangeResult

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun exchangeCdk(cdkCode: String) {
        _isLoading.value = true
        _exchangeResult.value = ""

        // 模拟CDK兑换过程
        try {
            // 模拟网络请求延迟
            Thread.sleep(1500)
            
            // 简单的模拟逻辑
            if (cdkCode.length >= 8) {
                _exchangeResult.postValue("CDK兑换成功！获得100金币")
            } else {
                _exchangeResult.postValue("CDK无效，请检查输入")
            }
        } catch (e: Exception) {
            _exchangeResult.postValue("兑换失败，请稍后重试")
        } finally {
            _isLoading.postValue(false)
        }
    }
}