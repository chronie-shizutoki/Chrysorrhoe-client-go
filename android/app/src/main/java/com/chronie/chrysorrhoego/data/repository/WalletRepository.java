package com.chronie.chrysorrhoego.data.repository;

import com.chronie.chrysorrhoego.model.Wallet;
import com.chronie.chrysorrhoego.model.Transaction;
import java.util.List;

/**
 * 钱包数据仓库接口
 * 定义了钱包相关数据的访问抽象方法
 * 遵循Repository模式，将数据源实现与业务逻辑分离
 */
public interface WalletRepository {

    /**
     * 获取用户钱包信息
     * @return 钱包信息
     */
    Wallet getWalletInfo();

    /**
     * 获取交易历史列表
     * @param page 页码
     * @param pageSize 每页大小
     * @return 交易列表
     */
    List<Transaction> getTransactionHistory(int page, int pageSize);

    /**
     * 执行转账操作
     * @param recipient 接收方
     * @param amount 金额
     * @param memo 备注
     * @return 转账结果
     */
    Transaction transfer(String recipient, double amount, String memo);

    /**
     * 兑换CDK
     * @param cdk CDK代码
     * @return 兑换结果
     */
    Transaction redeemCdk(String cdk);

    /**
     * 刷新钱包数据
     * @return 刷新结果
     */
    Boolean refreshWalletData();

    /**
     * 缓存钱包信息到本地
     * @param wallet 钱包信息
     */
    void cacheWalletInfo(Wallet wallet);

    /**
     * 缓存交易历史到本地
     * @param transactions 交易列表
     */
    void cacheTransactionHistory(List<Transaction> transactions);

    /**
     * 从本地获取缓存的钱包信息
     * @return 钱包信息
     */
    Wallet getCachedWalletInfo();

    /**
     * 从本地获取缓存的交易历史
     * @return 交易列表
     */
    List<Transaction> getCachedTransactionHistory();
}