package com.chronie.chrysorrhoego.data.local;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.chronie.chrysorrhoego.model.Wallet;

/**
 * 钱包数据访问对象接口
 * 定义与钱包相关的数据库操作
 */
@Dao
public interface WalletDao {
    
    /**
     * 获取钱包信息
     * @return 钱包对象，如果不存在则返回null
     */
    @Query("SELECT * FROM wallet LIMIT 1")
    Wallet getWallet();
    
    /**
     * 插入钱包信息
     * 如果已存在，则替换
     * @param wallet 钱包对象
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Wallet wallet);
    
    /**
     * 更新钱包信息
     * @param wallet 钱包对象
     */
    @Update
    void update(Wallet wallet);
    
    /**
     * 插入或更新钱包信息
     * 如果已存在则更新，否则插入
     * @param wallet 钱包对象
     */
    default void insertOrUpdate(Wallet wallet) {
        // 先查询是否存在钱包记录
        Wallet existingWallet = getWallet();
        if (existingWallet == null) {
            insert(wallet);
        } else {
            // 确保ID一致
            wallet.setId(existingWallet.getId());
            update(wallet);
        }
    }
    
    /**
     * 删除钱包信息
     * @param wallet 钱包对象
     */
    @Delete
    void delete(Wallet wallet);
    
    /**
     * 清空钱包表
     */
    @Query("DELETE FROM wallet")
    void clear();
    
    /**
     * 检查钱包记录是否存在
     * @return 如果存在返回true，否则返回false
     */
    @Query("SELECT COUNT(*) > 0 FROM wallet")
    boolean exists();
    
    /**
     * 更新钱包余额
     * @param balance 新的余额
     */
    @Query("UPDATE wallet SET balance = :balance WHERE id = (SELECT id FROM wallet LIMIT 1)")
    void updateBalance(double balance);
}