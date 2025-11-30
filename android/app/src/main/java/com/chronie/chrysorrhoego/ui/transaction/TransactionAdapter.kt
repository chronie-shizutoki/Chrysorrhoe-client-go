package com.chronie.chrysorrhoego.ui.transaction

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chronie.chrysorrhoego.R

class TransactionAdapter : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    private var transactions: List<TransactionViewModel.Transaction> = emptyList()

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textType: TextView = itemView.findViewById(R.id.text_transaction_type)
        val textAmount: TextView = itemView.findViewById(R.id.text_transaction_amount)
        val textDate: TextView = itemView.findViewById(R.id.text_transaction_date)
        val textStatus: TextView = itemView.findViewById(R.id.text_transaction_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.textType.text = transaction.type
        holder.textAmount.text = if (transaction.type == "SEND") "-$${transaction.amount}" else "+$${transaction.amount}"
        holder.textDate.text = transaction.date
        holder.textStatus.text = transaction.status
    }

    override fun getItemCount() = transactions.size

    fun setTransactions(transactions: List<TransactionViewModel.Transaction>) {
        this.transactions = transactions
        notifyDataSetChanged()
    }
}