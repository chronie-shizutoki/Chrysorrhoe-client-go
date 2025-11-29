package com.chronie.chrysorrhoego.data.remote.dto;

/**
 * 交易状态响应数据传输对象
 * 用于获取交易状态API的响应信息
 */
public class TransactionStatusResponse {

    private String transactionId;
    private String status;
    private long updatedAt;
    private String errorMessage;
    private String blockchainHash;
    private String blockNumber;
    private String confirmationCount;
    
    // 默认构造函数
    public TransactionStatusResponse() {
    }
    
    // Getter和Setter方法
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public long getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public String getBlockchainHash() {
        return blockchainHash;
    }
    
    public void setBlockchainHash(String blockchainHash) {
        this.blockchainHash = blockchainHash;
    }
    
    public String getBlockNumber() {
        return blockNumber;
    }
    
    public void setBlockNumber(String blockNumber) {
        this.blockNumber = blockNumber;
    }
    
    public String getConfirmationCount() {
        return confirmationCount;
    }
    
    public void setConfirmationCount(String confirmationCount) {
        this.confirmationCount = confirmationCount;
    }
    
    // 判断交易是否成功
    public boolean isSuccess() {
        return "SUCCESS".equals(status) || "COMPLETED".equals(status);
    }
    
    // 判断交易是否失败
    public boolean isFailed() {
        return "FAILED".equals(status) || "REJECTED".equals(status) || "CANCELLED".equals(status);
    }
    
    // 判断交易是否处于处理中
    public boolean isPending() {
        return "PENDING".equals(status) || "PROCESSING".equals(status) || "CONFIRMING".equals(status);
    }
    
    @Override
    public String toString() {
        return "TransactionStatusResponse{" +
                "transactionId='" + transactionId + '\'' +
                ", status='" + status + '\'' +
                ", updatedAt=" + updatedAt +
                ", errorMessage='" + errorMessage + '\'' +
                ", blockchainHash='" + blockchainHash + '\'' +
                ", blockNumber='" + blockNumber + '\'' +
                ", confirmationCount='" + confirmationCount + '\'' +
                '}';
    }
}