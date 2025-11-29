package com.chronie.chrysorrhoego.data.remote;

/**
 * 转账请求类，用于API调用的请求体
 */
public class TransferRequest {
    private String recipient;
    private double amount;
    private String memo;

    /**
     * 构造函数
     * @param recipient 收款人ID
     * @param amount 转账金额
     * @param memo 备注
     */
    public TransferRequest(String recipient, double amount, String memo) {
        this.recipient = recipient;
        this.amount = amount;
        this.memo = memo;
    }

    // Getter and Setter methods
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
}