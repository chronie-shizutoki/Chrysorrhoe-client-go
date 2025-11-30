package com.chronie.chrysorrhoego.presentation.transaction

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.chronie.chrysorrhoego.ui.theme.ChrysorrhoegoTheme
import com.chronie.chrysorrhoego.ui.transaction.TransactionViewModel

class TransactionHistoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 初始化ViewModel
        val viewModel = ViewModelProvider(this)[TransactionViewModel::class.java]
        
        // 设置Compose内容
        setContent {
            ChrysorrhoegoTheme {
                TransactionHistoryScreen(viewModel = viewModel)
            }
        }
    }
}
