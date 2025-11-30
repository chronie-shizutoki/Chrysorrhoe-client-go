package com.chronie.chrysorrhoego.ui.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.chronie.chrysorrhoego.R
import com.chronie.chrysorrhoego.databinding.FragmentTransactionHistoryBinding

class TransactionHistoryFragment : Fragment() {

    private lateinit var binding: FragmentTransactionHistoryBinding
    private lateinit var viewModel: TransactionViewModel
    private lateinit var adapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTransactionHistoryBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[TransactionViewModel::class.java]

        // 设置RecyclerView
        adapter = TransactionAdapter()
        binding.recyclerViewTransactions.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewTransactions.adapter = adapter

        // 设置筛选按钮点击事件
        setupFilterButtons()

        // 设置搜索功能
        setupSearchView()

        // 设置刷新按钮
        binding.buttonRefresh.setOnClickListener {
            viewModel.refreshTransactions()
        }

        // 设置返回按钮
        binding.buttonBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        // 观察筛选后的交易数据
        viewModel.filteredTransactions.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            
            // 如果没有交易记录，显示空状态
            if (it.isEmpty()) {
                binding.textEmptyState.visibility = View.VISIBLE
                binding.recyclerViewTransactions.visibility = View.GONE
            } else {
                binding.textEmptyState.visibility = View.GONE
                binding.recyclerViewTransactions.visibility = View.VISIBLE
            }
        }

        // 观察加载状态
        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }

        // 观察错误信息
        viewModel.error.observe(viewLifecycleOwner) {
            it?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        }

        return binding.root
    }

    // 设置筛选按钮点击事件
    private fun setupFilterButtons() {
        // 全部分类
        binding.filterButtonAll.setOnClickListener {
            applyFilter("ALL")
        }
        
        // 发送交易
        binding.filterButtonSend.setOnClickListener {
            applyFilter("SEND")
        }
        
        // 接收交易
        binding.filterButtonReceive.setOnClickListener {
            applyFilter("RECEIVE")
        }
        
        // 重置筛选
        binding.filterButtonReset.setOnClickListener {
            viewModel.resetFilters()
            resetFilterButtons()
            binding.searchView.setQuery("", false)
        }
    }

    // 应用筛选并更新UI
    private fun applyFilter(type: String) {
        viewModel.filterByType(type)
        resetFilterButtons()
        
        // 高亮选中的筛选按钮
        when (type) {
            "ALL" -> binding.filterButtonAll.setBackgroundColor(activity?.resources?.getColor(R.color.colorPrimary) ?: 0)
              "SEND" -> binding.filterButtonSend.setBackgroundColor(activity?.resources?.getColor(R.color.colorPrimary) ?: 0)
              "RECEIVE" -> binding.filterButtonReceive.setBackgroundColor(activity?.resources?.getColor(R.color.colorPrimary) ?: 0)
        }
    }

    // 重置筛选按钮样式
    private fun resetFilterButtons() {
        val defaultColor = activity?.resources?.getColor(R.color.colorBackgroundLight) ?: 0
        binding.filterButtonAll.setBackgroundColor(defaultColor)
        binding.filterButtonSend.setBackgroundColor(defaultColor)
        binding.filterButtonReceive.setBackgroundColor(defaultColor)
    }

    // 设置搜索功能
    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    viewModel.searchTransactions(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    viewModel.searchTransactions(it)
                }
                return true
            }
        })

        // 搜索框清除按钮事件
        binding.searchView.setOnCloseListener {
            viewModel.resetFilters()
            resetFilterButtons()
            false
        }
    }
}