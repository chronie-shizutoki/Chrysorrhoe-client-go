package com.chronie.chrysorrhoego.ui.wallet

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.chronie.chrysorrhoego.R

class WalletDashboardFragment : Fragment() {

    private lateinit var walletViewModel: WalletViewModel
    private var isTransferFormVisible = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        walletViewModel = ViewModelProvider(this).get(WalletViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_wallet_dashboard, container, false)

        val textBalance: TextView = root.findViewById(R.id.text_balance_amount)
        val textUsername: TextView = root.findViewById(R.id.text_username)
        val buttonTransfer: Button = root.findViewById(R.id.button_transfer)
        val buttonCdkExchange: Button = root.findViewById(R.id.button_cdk_exchange)
        val buttonTransactionHistory: Button = root.findViewById(R.id.button_transaction_history)
        val buttonRefresh: Button = root.findViewById(R.id.button_refresh)
        val buttonLogout: Button = root.findViewById(R.id.button_logout)
        
        // 转账表单元素
        val transferForm: LinearLayout = root.findViewById(R.id.transfer_form)
        val editTextRecipient: EditText = root.findViewById(R.id.edit_text_recipient)
        val editTextAmount: EditText = root.findViewById(R.id.edit_text_amount)
        val buttonSubmitTransfer: Button = root.findViewById(R.id.button_submit_transfer)
        val buttonCancelTransfer: Button = root.findViewById(R.id.button_cancel_transfer)
        
        // 消息显示元素
        val textError: TextView = root.findViewById(R.id.text_error)
        val textSuccess: TextView = root.findViewById(R.id.text_success)
        val progressBar: ProgressBar = root.findViewById(R.id.progress_bar)

        // 观察钱包数据
        walletViewModel.wallet.observe(viewLifecycleOwner) {
            if (it != null) {
                textBalance.text = "$$it.balance"
                textUsername.text = "Username: ${it.username}"
            }
        }

        // 观察加载状态
        walletViewModel.isLoading.observe(viewLifecycleOwner) {
            progressBar.visibility = if (it) View.VISIBLE else View.GONE
            textError.visibility = View.GONE
        }

        // 观察错误消息
        walletViewModel.error.observe(viewLifecycleOwner) {
            if (it != null) {
                textError.text = it
                textError.visibility = View.VISIBLE
                textSuccess.visibility = View.GONE
            }
        }

        // 观察转账成功状态
        walletViewModel.transferSuccess.observe(viewLifecycleOwner) {
            if (it) {
                textSuccess.text = "Transfer successful!"
                textSuccess.visibility = View.VISIBLE
                textError.visibility = View.GONE
                // 隐藏转账表单
                transferForm.visibility = View.GONE
                isTransferFormVisible = false
                // 清空表单
                editTextRecipient.text.clear()
                editTextAmount.text.clear()
                
                // 3秒后隐藏成功消息
                view?.postDelayed({
                    textSuccess.visibility = View.GONE
                }, 3000)
            }
        }

        // 转账按钮点击事件
        buttonTransfer.setOnClickListener {
            isTransferFormVisible = !isTransferFormVisible
            transferForm.visibility = if (isTransferFormVisible) View.VISIBLE else View.GONE
            textError.visibility = View.GONE
            textSuccess.visibility = View.GONE
        }

        // 提交转账按钮点击事件
        buttonSubmitTransfer.setOnClickListener {
            val recipient = editTextRecipient.text.toString().trim()
            val amountText = editTextAmount.text.toString().trim()
            
            // 验证输入
            if (recipient.isEmpty()) {
                editTextRecipient.error = "Recipient cannot be empty"
                return@setOnClickListener
            }
            
            try {
                val amount = amountText.toDouble()
                if (amount <= 0) {
                    editTextAmount.error = "Amount must be greater than 0"
                    return@setOnClickListener
                }
                
                // 调用ViewModel进行转账
                walletViewModel.transfer(recipient, amount)
            } catch (e: NumberFormatException) {
                editTextAmount.error = "Invalid amount"
            }
        }

        // 取消转账按钮点击事件
        buttonCancelTransfer.setOnClickListener {
            transferForm.visibility = View.GONE
            isTransferFormVisible = false
            editTextRecipient.text.clear()
            editTextAmount.text.clear()
            textError.visibility = View.GONE
        }

        // CDK兑换按钮点击事件 - 导航到CDK兑换页面
        buttonCdkExchange.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_wallet_dashboard_to_cdk_exchange)
        }

        // 交易历史按钮点击事件 - 导航到交易历史页面
        buttonTransactionHistory.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_wallet_dashboard_to_transaction_history)
        }

        // 刷新按钮点击事件
        buttonRefresh.setOnClickListener {
            walletViewModel.refreshData()
        }

        // 登出按钮点击事件
        buttonLogout.setOnClickListener {
            // 显示确认对话框
            AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes") { dialog: DialogInterface, which: Int ->
                    // 清除本地存储的钱包数据
                    // 在实际应用中，这里应该清除SharedPreferences中的数据
                    Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                    // 重新加载数据，显示默认状态
                    walletViewModel.refreshData()
                }
                .setNegativeButton("No", null)
                .show()
        }

        return root
    }
}