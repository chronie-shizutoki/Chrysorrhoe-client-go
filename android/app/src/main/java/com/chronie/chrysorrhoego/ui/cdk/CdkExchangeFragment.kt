package com.chronie.chrysorrhoego.ui.cdk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.chronie.chrysorrhoego.ui.cdk.CdkExchangeRecord
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chronie.chrysorrhoego.R

class CdkExchangeFragment : Fragment() {

    private lateinit var cdkViewModel: CdkViewModel
    private lateinit var adapter: CdkHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 初始化ViewModel
        cdkViewModel = ViewModelProvider(this).get(CdkViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_cdk_exchange, container, false)

        // 初始化UI组件
        val editTextCdkCode: EditText = root.findViewById(R.id.edit_text_cdk_code)
        val buttonExchange: Button = root.findViewById(R.id.button_exchange_cdk)
        val textResult: TextView = root.findViewById(R.id.text_exchange_result)
        val progressBar: ProgressBar = root.findViewById(R.id.progress_bar_loading)
        val textError: TextView = root.findViewById(R.id.text_error_message)
        val buttonBack: Button = root.findViewById(R.id.button_back)
        val recyclerViewHistory: RecyclerView = root.findViewById(R.id.recycler_view_exchange_history)

        // 设置历史记录RecyclerView
        adapter = CdkHistoryAdapter()
        recyclerViewHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@CdkExchangeFragment.adapter
        }

        // 设置返回按钮点击事件
        buttonBack.setOnClickListener {
            Navigation.findNavController(it).navigateUp()
        }

        // 设置兑换按钮点击事件
        buttonExchange.setOnClickListener {
            val cdkCode = editTextCdkCode.text.toString()
            cdkViewModel.exchangeCdk(cdkCode)
        }

        // 观察兑换结果
        cdkViewModel.exchangeResult.observe(viewLifecycleOwner) { result ->
            textResult.text = result
            // 如果有结果，显示它并隐藏错误信息
            if (result.isNotEmpty()) {
                textResult.visibility = View.VISIBLE
                textError.visibility = View.GONE
            }
        }

        // 观察加载状态
        cdkViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            buttonExchange.isEnabled = !isLoading
            editTextCdkCode.isEnabled = !isLoading
        }

        // 观察错误信息
        cdkViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                textError.text = errorMessage
                textError.visibility = View.VISIBLE
                // 显示错误提示
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            } else {
                textError.visibility = View.GONE
            }
        }

        // 观察兑换历史
        cdkViewModel.exchangeHistory.observe(viewLifecycleOwner) { history ->
            adapter.submitList(history)
            // 如果有历史记录，显示RecyclerView，否则隐藏
            recyclerViewHistory.visibility = if (history.isNotEmpty()) View.VISIBLE else View.GONE
            root.findViewById<TextView>(R.id.text_no_history).visibility = if (history.isEmpty()) View.VISIBLE else View.GONE
        }

        return root
    }

    // 历史记录适配器
    inner class CdkHistoryAdapter : RecyclerView.Adapter<CdkHistoryAdapter.HistoryViewHolder>() {

        private var records: List<CdkExchangeRecord> = emptyList()

        fun submitList(newRecords: List<CdkExchangeRecord>) {
            records = newRecords
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_cdk_history, parent, false)
            return HistoryViewHolder(view)
        }

        override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
            val record = records[position]
            holder.bind(record)
        }

        override fun getItemCount(): Int = records.size

        inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val textCode: TextView = itemView.findViewById(R.id.text_cdk_code)
            private val textStatus: TextView = itemView.findViewById(R.id.text_exchange_status)
            private val textReward: TextView = itemView.findViewById(R.id.text_exchange_reward)
            private val textDateTime: TextView = itemView.findViewById(R.id.text_exchange_time)

            fun bind(record: CdkExchangeRecord) {
                textCode.text = record.code
                textStatus.text = record.status
                textReward.text = record.reward
                textDateTime.text = record.dateTime

                // 根据状态设置不同颜色
                if (record.isSuccessful) {
                    textStatus.setTextColor(itemView.context.getColor(R.color.colorSuccess))
                } else {
                    textStatus.setTextColor(itemView.context.getColor(R.color.colorError))
                }
            }
        }
    }
}