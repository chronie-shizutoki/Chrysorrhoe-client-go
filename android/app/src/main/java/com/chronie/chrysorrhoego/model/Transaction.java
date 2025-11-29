package com.chronie.chrysorrhoego.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.chronie.chrysorrhoego.data.local.converter.TransactionTypeConverter;

/**
 * 交易模型类，定义交易的基本属性和类型
 */
@Entity(tableName = "transactions")
@TypeConverters(TransactionTypeConverter.class)
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    private int uid;
    private String id;
    private Type type;
    private double amount;
    private long timestamp;
    private String note;
    private String senderName;
    private String recipientName;
    private String senderId;
    private String recipientId;
    private boolean isOutgoing;
    private Status status;
    private Direction direction;
    private String transactionId;
    private String currencyCode;
    private String memo;

    /**
     * 交易类型枚举
     */
    public enum Type {
        DEPOSIT,
        WITHDRAWAL,
        TRANSFER,
        CDK_REDEEM,
        FEE
    }

    /**
     * 交易状态枚举
     */
    public enum Status {
        PENDING,
        COMPLETED,
        FAILED,
        CANCELLED
    }

    /**
     * 交易方向枚举
     */
    public enum Direction {
        INCOMING,
        OUTGOING
    }

    public Transaction() {
        // 默认构造函数
    }

    public Transaction(String id, Type type, double amount, long timestamp) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
        this.status = Status.COMPLETED; // 默认状态为已完成
    }

    // Getter 和 Setter 方法
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
    
    // Getter and Setter for uid
    public int getUid() {
        return uid;
    }
    
    public void setUid(int uid) {
        this.uid = uid;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public boolean isOutgoing() {
        return isOutgoing;
    }

    public void setOutgoing(boolean outgoing) {
        isOutgoing = outgoing;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", amount=" + amount +
                ", timestamp=" + timestamp +
                ", status=" + status +
                '}';
    }

    /**
     * 创建交易构建器
     */
    public static class Builder {
        private Transaction transaction;

        public Builder() {
            transaction = new Transaction();
        }

        public Builder id(String id) {
            transaction.setId(id);
            return this;
        }

        public Builder type(Type type) {
            transaction.setType(type);
            return this;
        }

        public Builder amount(double amount) {
            transaction.setAmount(amount);
            return this;
        }

        public Builder timestamp(long timestamp) {
            transaction.setTimestamp(timestamp);
            return this;
        }

        public Builder note(String note) {
            transaction.setNote(note);
            return this;
        }

        public Builder sender(String id, String name) {
            transaction.setSenderId(id);
            transaction.setSenderName(name);
            return this;
        }

        public Builder recipient(String id, String name) {
            transaction.setRecipientId(id);
            transaction.setRecipientName(name);
            return this;
        }

        public Builder outgoing(boolean isOutgoing) {
            transaction.setOutgoing(isOutgoing);
            return this;
        }

        public Builder status(Status status) {
            transaction.setStatus(status);
            return this;
        }

        public Transaction build() {
            return transaction;
        }
    }
}