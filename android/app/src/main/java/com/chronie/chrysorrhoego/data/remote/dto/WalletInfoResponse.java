package com.chronie.chrysorrhoego.data.remote.dto;

/**
 * 钱包信息响应DTO
 * 用于接收钱包信息API的响应数据
 */
public class WalletInfoResponse {

    private String walletId;
    private String walletName;
    private double balance;
    private String currencyCode;
    private long timestamp;
    private String status;
    private String userId;
    
    // 默认构造函数
    public WalletInfoResponse() {
    }
    
    // Getter和Setter方法
    public String getWalletId() {
        return walletId;
    }
    
    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }
    
    public String getWalletName() {
        return walletName;
    }
    
    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }
    
    public double getBalance() {
        return balance;
    }
    
    public void setBalance(double balance) {
        this.balance = balance;
    }
    
    public String getCurrencyCode() {
        return currencyCode;
    }
    
    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
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
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    @Override
    public String toString() {
        return "WalletInfoResponse{" +
                "walletId='" + walletId + '\'' +
                ", walletName='" + walletName + '\'' +
                ", balance=" + balance +
                ", currencyCode='" + currencyCode + '\'' +
                ", timestamp=" + timestamp +
                ", status='" + status + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}