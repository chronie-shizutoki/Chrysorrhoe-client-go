package com.chronie.chrysorrhoego.ui.transaction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chronie.chrysorrhoego.R;
import com.chronie.chrysorrhoego.model.Transaction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 交易历史适配器，用于在RecyclerView中展示交易列表
 */
public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    private Context context;
    private List<Transaction> transactions;
    private SimpleDateFormat dateFormat;

    /**
     * 构造函数
     * @param context 上下文
     * @param transactions 交易列表
     */
    public TransactionAdapter(Context context, List<Transaction> transactions) {
        this.context = context;
        this.transactions = transactions;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.transaction_item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        
        // 设置交易描述
        String description = getTransactionDescription(transaction);
        holder.tvDescription.setText(description);
        
        // 设置交易金额
        String amountText = formatAmount(transaction.getAmount(), transaction.getType(), transaction.isOutgoing());
        holder.tvAmount.setText(amountText);
        holder.tvAmount.setTextColor(getAmountColor(transaction.getType(), transaction.isOutgoing()));
        
        // 设置交易时间
        String timeText = formatTime(transaction.getTimestamp());
        holder.tvTime.setText(timeText);
        
        // 设置备注
        if (transaction.getNote() != null && !transaction.getNote().isEmpty()) {
            holder.tvNote.setText(transaction.getNote());
            holder.tvNote.setVisibility(View.VISIBLE);
        } else {
            holder.tvNote.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return transactions != null ? transactions.size() : 0;
    }

    /**
     * 更新交易列表数据
     */
    public void updateData(List<Transaction> newTransactions) {
        this.transactions = newTransactions;
        notifyDataSetChanged();
    }

    /**
     * 获取交易描述
     */
    private String getTransactionDescription(Transaction transaction) {
        switch (transaction.getType()) {
            case DEPOSIT:
                return context.getString(R.string.transaction_deposit);
            case WITHDRAWAL:
                return context.getString(R.string.transaction_withdrawal);
            case TRANSFER:
                if (transaction.isOutgoing()) {
                    return context.getString(R.string.transaction_transfer_sent) + 
                            " " + transaction.getRecipientName();
                } else {
                    return context.getString(R.string.transaction_transfer_received) + 
                            " " + transaction.getSenderName();
                }
            case CDK_REDEEM:
                return context.getString(R.string.transaction_cdk_redeem);
            case FEE:
                return context.getString(R.string.transaction_fee);
            default:
                return transaction.getType().toString();
        }
    }

    /**
     * 格式化金额
     */
    private String formatAmount(double amount, Transaction.Type type, boolean isOutgoing) {
        String symbol = "¥";
        String formattedAmount = String.format(Locale.getDefault(), "%.2f", Math.abs(amount));
        
        if (type == Transaction.Type.DEPOSIT || (!isOutgoing && type == Transaction.Type.TRANSFER)) {
            return "+" + symbol + formattedAmount;
        } else {
            return "-" + symbol + formattedAmount;
        }
    }

    /**
     * 格式化时间
     */
    private String formatTime(long timestamp) {
        return dateFormat.format(new Date(timestamp));
    }

    /**
     * 获取金额颜色（收入为绿色，支出为红色）
     */
    private int getAmountColor(Transaction.Type type, boolean isOutgoing) {
        if (type == Transaction.Type.DEPOSIT || (!isOutgoing && type == Transaction.Type.TRANSFER)) {
            return context.getResources().getColor(R.color.success_green);
        } else {
            return context.getResources().getColor(R.color.error_red);
        }
    }

    /**
     * 交易ViewHolder类
     */
    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView tvDescription;
        TextView tvAmount;
        TextView tvTime;
        TextView tvNote;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDescription = itemView.findViewById(R.id.transaction_description);
                tvAmount = itemView.findViewById(R.id.transaction_amount);
            tvTime = itemView.findViewById(R.id.transaction_timestamp);
                tvNote = itemView.findViewById(R.id.transaction_memo);
        }
    }
}