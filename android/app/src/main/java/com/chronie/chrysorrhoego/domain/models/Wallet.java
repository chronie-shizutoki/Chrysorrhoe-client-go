package com.chronie.chrysorrhoego.domain.models;

/**
 * 钱包领域模型
 */
public class Wallet {
    private final String userId;
    private final String username;
    private final double balance;

    public Wallet(String userId, String username, double balance) {
        this.userId = userId;
        this.username = username;
        this.balance = balance;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public double getBalance() {
        return balance;
    }
}