package com.chronie.chrysorrhoego.presentation.cdk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.chronie.chrysorrhoego.presentation.ui.theme.ChrysorrhoegoTheme
import com.chronie.chrysorrhoego.ui.cdk.CdkViewModel

class CdkExchangeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 初始化ViewModel
        val viewModel = ViewModelProvider(this)[CdkViewModel::class.java]
        
        // 设置Compose内容
        setContent {
            ChrysorrhoegoTheme {
                CdkExchangeScreen(viewModel = viewModel)
            }
        }
    }
}
