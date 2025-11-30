package com.chronie.chrysorrhoego.ui.cdk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.chronie.chrysorrhoego.R

class CdkExchangeFragment : Fragment() {

    private lateinit var cdkViewModel: CdkViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        cdkViewModel = ViewModelProvider(this).get(CdkViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_cdk_exchange, container, false)

        val editTextCdkCode: EditText = root.findViewById(R.id.edit_text_cdk_code)
        val buttonExchange: Button = root.findViewById(R.id.button_exchange_cdk)
        val textResult: TextView = root.findViewById(R.id.text_exchange_result)

        buttonExchange.setOnClickListener {
            val cdkCode = editTextCdkCode.text.toString()
            if (cdkCode.isNotEmpty()) {
                cdkViewModel.exchangeCdk(cdkCode)
            }
        }

        cdkViewModel.exchangeResult.observe(viewLifecycleOwner) {
            textResult.text = it
        }

        return root
    }
}