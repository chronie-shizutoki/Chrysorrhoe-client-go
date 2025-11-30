package com.chronie.chrysorrhoego.ui.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chronie.chrysorrhoego.R

class TransactionHistoryFragment : Fragment() {

    private lateinit var transactionViewModel: TransactionViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        transactionViewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_transaction_history, container, false)

        val recyclerView: RecyclerView = root.findViewById(R.id.recycler_view_transactions)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val adapter = TransactionAdapter()
        recyclerView.adapter = adapter

        transactionViewModel.transactions.observe(viewLifecycleOwner) {
            adapter.setTransactions(it)
        }

        return root
    }
}