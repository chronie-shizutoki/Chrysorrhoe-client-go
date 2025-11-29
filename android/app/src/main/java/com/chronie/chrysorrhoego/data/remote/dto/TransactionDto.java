package com.chronie.chrysorrhoego.data.remote.dto;

/**
 * 交易数据传输对象
 * 用于API响应中的交易信息
 */
public class TransactionDto {

    private String transactionId;
    private double amount;
    private String sender;
    private String recipient;
    private long timestamp;
    private String status;
    private String type;
    private String direction;
    private String memo;
    private String currencyCode;
    private String blockchainHash;
    private String referenceId;
    
    // 默认构造函数
    public TransactionDto() {
    }
    
    // Getter和Setter方法
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public String getSender() {
        return sender;
    }
    
    public void setSender(String sender) {
        this.sender = sender;
    }
    
    public String getRecipient() {
        return recipient;
    }
    
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getDirection() {
        return direction;
    }
    
    public void setDirection(String direction) {
        this.direction = direction;
    }
    
    public String getMemo() {
        return memo;
    }
    
    public void setMemo(String memo) {
        this.memo = memo;
    }
    
    public String getCurrencyCode() {
        return currencyCode;
    }
    
    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }
    
    public String getBlockchainHash() {
        return blockchainHash;
    }
    
    public void setBlockchainHash(String blockchainHash) {
        this.blockchainHash = blockchainHash;
    }
    
    public String getReferenceId() {
        return referenceId;
    }
    
    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }
    
    @Override
    public String toString() {
        return "TransactionDto{" +
                "transactionId='" + transactionId + '\'' +
                ", amount=" + amount +
                ", sender='" + sender + '\'' +
                ", recipient='" + recipient + '\'' +
                ", timestamp=" + timestamp +
                ", status='" + status + '\'' +
                ", type='" + type + '\'' +
                ", direction='" + direction + '\'' +
                ", memo='" + memo + '\'' +
                ", currencyCode='" + currencyCode + '\'' +
                ", blockchainHash='" + blockchainHash + '\'' +
                ", referenceId='" + referenceId + '\'' +
                '}';
    }
}