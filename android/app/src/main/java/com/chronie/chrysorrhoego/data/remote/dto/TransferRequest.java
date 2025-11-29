package com.chronie.chrysorrhoego.data.remote.dto;

/**
 * 转账请求数据传输对象
 * 用于封装转账API请求参数
 */
public class TransferRequest {

    private String recipient;
    private double amount;
    private String memo;
    private String sender;
    private String currencyCode;
    
    // 默认构造函数
    public TransferRequest() {
    }
    
    // 构造函数
    public TransferRequest(String recipient, double amount, String memo) {
        this.recipient = recipient;
        this.amount = amount;
        this.memo = memo;
    }
    
    // Getter和Setter方法
    public String getRecipient() {
        return recipient;
    }
    
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public String getMemo() {
        return memo;
    }
    
    public void setMemo(String memo) {
        this.memo = memo;
    }
    
    public String getSender() {
        return sender;
    }
    
    public void setSender(String sender) {
        this.sender = sender;
    }
    
    public String getCurrencyCode() {
        return currencyCode;
    }
    
    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }
    
    @Override
    public String toString() {
        return "TransferRequest{" +
                "recipient='" + recipient + '\'' +
                ", amount=" + amount +
                ", memo='" + memo + '\'' +
                ", sender='" + sender + '\'' +
                ", currencyCode='" + currencyCode + '\'' +
                '}';
    }
}