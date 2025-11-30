package com.chronie.chrysorrhoego.presentation.wallet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.chronie.chrysorrhoego.ui.theme.ChrysorrhoegoTheme
import com.chronie.chrysorrhoego.ui.wallet.WalletViewModel

class WalletDashboardActivity : ComponentActivity() {
    private lateinit var viewModel: WalletViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 获取现有的WalletViewModel实例
        viewModel = ViewModelProvider(this)[WalletViewModel::class.java]
        
        setContent {
            ChrysorrhoegoTheme {
                WalletDashboardScreen(viewModel = viewModel)
            }
        }
    }
}
