package com.chronie.chrysorrhoego.ui.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.chronie.chrysorrhoego.R

class WalletDashboardFragment : Fragment() {

    private lateinit var walletViewModel: WalletViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        walletViewModel = ViewModelProvider(this).get(WalletViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_wallet_dashboard, container, false)

        val textBalance: TextView = root.findViewById(R.id.text_balance_amount)
        val buttonTransfer: Button = root.findViewById(R.id.button_transfer)
        val buttonCdkExchange: Button = root.findViewById(R.id.button_cdk_exchange)
        val buttonTransactionHistory: Button = root.findViewById(R.id.button_transaction_history)

        walletViewModel.balance.observe(viewLifecycleOwner) {
            textBalance.text = "$$it"
        }

        buttonTransfer.setOnClickListener {
            // 处理转账按钮点击事件
        }

        buttonCdkExchange.setOnClickListener {
            // 处理CDK兑换按钮点击事件
        }

        buttonTransactionHistory.setOnClickListener {
            // 处理交易历史按钮点击事件
        }

        return root
    }
}