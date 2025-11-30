package com.chronie.chrysorrhoego.ui.transaction

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chronie.chrysorrhoego.R

class TransactionAdapter : ListAdapter<TransactionViewModel.Transaction, TransactionAdapter.TransactionViewHolder>(DiffCallback()) {

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textType: TextView = itemView.findViewById(R.id.text_transaction_type)
        val textAmount: TextView = itemView.findViewById(R.id.text_transaction_amount)
        val textDate: TextView = itemView.findViewById(R.id.text_transaction_date)
        val textStatus: TextView = itemView.findViewById(R.id.text_transaction_status)
        val textUser: TextView = itemView.findViewById(R.id.text_transaction_user)
        val textDescription: TextView = itemView.findViewById(R.id.text_transaction_description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = getItem(position)
        
        // 设置交易类型
        holder.textType.text = transaction.type
        
        // 设置金额和颜色
        val amountText = if (transaction.type == "SEND") "-${transaction.amount}" else "+${transaction.amount}"
        holder.textAmount.text = amountText
        
        // 根据交易类型设置金额颜色
        if (transaction.type == "SEND") {
            holder.textAmount.setTextColor(holder.itemView.context.getColor(R.color.colorExpense))
        } else {
            holder.textAmount.setTextColor(holder.itemView.context.getColor(R.color.colorIncome))
        }
        
        // 设置日期
        holder.textDate.text = transaction.date
        
        // 设置状态
        holder.textStatus.text = transaction.status
        
        // 设置状态颜色
        when (transaction.status) {
            "COMPLETED" -> holder.textStatus.setTextColor(holder.itemView.context.getColor(R.color.colorSuccess))
            "PENDING" -> holder.textStatus.setTextColor(holder.itemView.context.getColor(R.color.colorWarning))
            "FAILED" -> holder.textStatus.setTextColor(holder.itemView.context.getColor(R.color.colorError))
        }
        
        // 设置用户信息（收款人或发送人）
        if (transaction.type == "SEND" && transaction.recipient != null) {
            holder.textUser.text = "To: ${transaction.recipient}"
        } else if (transaction.type == "RECEIVE" && transaction.sender != null) {
            holder.textUser.text = "From: ${transaction.sender}"
        } else {
            holder.textUser.visibility = View.GONE
        }
        
        // 设置交易描述
        if (transaction.description != null && transaction.description.isNotEmpty()) {
            holder.textDescription.text = transaction.description
            holder.textDescription.visibility = View.VISIBLE
        } else {
            holder.textDescription.visibility = View.GONE
        }
    }

    // 兼容原有的setTransactions方法
    fun setTransactions(transactions: List<TransactionViewModel.Transaction>) {
        submitList(transactions)
    }

    // 使用DiffUtil提高列表更新性能
    class DiffCallback : DiffUtil.ItemCallback<TransactionViewModel.Transaction>() {
        override fun areItemsTheSame(oldItem: TransactionViewModel.Transaction, newItem: TransactionViewModel.Transaction): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TransactionViewModel.Transaction, newItem: TransactionViewModel.Transaction): Boolean {
            return oldItem == newItem
        }
    }
}