package com.chronie.chrysorrhoego.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 钱包模型类，定义钱包的基本属性
 */
@Entity(tableName = "wallet")
public class Wallet {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String walletId;
    private double balance;
    private String currencyCode;
    private String walletName;
    private long lastUpdated;
    private boolean isDefault;

    public Wallet() {
        // 默认构造函数
    }

    // Getter and Setter for id
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
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

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
}