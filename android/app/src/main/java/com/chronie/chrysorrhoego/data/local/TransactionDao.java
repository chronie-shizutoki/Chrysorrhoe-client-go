package com.chronie.chrysorrhoego.data.local;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.chronie.chrysorrhoego.model.Transaction;
import java.util.List;

/**
 * 交易数据访问对象接口
 * 定义与交易历史相关的数据库操作
 */
@Dao
public interface TransactionDao {
    
    /**
     * 获取所有交易记录
     * @return 交易记录列表
     */
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    List<Transaction> getAllTransactions();
    
    /**
     * 获取最近的交易记录
     * @param limit 限制返回的记录数量
     * @return 最近的交易记录列表
     */
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC LIMIT :limit")
    List<Transaction> getRecentTransactions(int limit);
    
    /**
     * 根据交易ID获取交易记录
     * @param transactionId 交易ID
     * @return 交易记录对象
     */
    @Query("SELECT * FROM transactions WHERE transactionId = :transactionId")
    Transaction getTransactionById(String transactionId);
    
    /**
     * 插入单条交易记录
     * @param transaction 交易对象
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Transaction transaction);
    
    /**
     * 插入多条交易记录
     * @param transactions 交易记录列表
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Transaction> transactions);
    
    /**
     * 更新交易记录
     * @param transaction 交易对象
     */
    @Update
    void update(Transaction transaction);
    
    /**
     * 删除交易记录
     * @param transaction 交易对象
     */
    @Delete
    void delete(Transaction transaction);
    
    /**
     * 根据交易ID删除交易记录
     * @param transactionId 交易ID
     */
    @Query("DELETE FROM transactions WHERE transactionId = :transactionId")
    void deleteById(String transactionId);
    
    /**
     * 清空交易记录表
     */
    @Query("DELETE FROM transactions")
    void clear();
    
    /**
     * 获取指定类型的交易记录
     * @param type 交易类型
     * @return 交易记录列表
     */
    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY timestamp DESC")
    List<Transaction> getTransactionsByType(String type);
    
    /**
     * 获取指定方向的交易记录
     * @param direction 交易方向（INCOMING或OUTGOING）
     * @return 交易记录列表
     */
    @Query("SELECT * FROM transactions WHERE direction = :direction ORDER BY timestamp DESC")
    List<Transaction> getTransactionsByDirection(String direction);
    
    /**
     * 分页获取交易记录
     * @param limit 每页数量
     * @param offset 偏移量
     * @return 交易记录列表
     */
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC LIMIT :limit OFFSET :offset")
    List<Transaction> getTransactions(int limit, int offset);
    
    /**
     * 获取交易记录总数
     * @return 交易记录总数
     */
    @Query("SELECT COUNT(*) FROM transactions")
    int getTransactionsCount();
    
    /**
     * 获取指定时间范围内的交易记录
     * @param startTime 开始时间戳
     * @param endTime 结束时间戳
     * @return 交易记录列表
     */
    @Query("SELECT * FROM transactions WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    List<Transaction> getTransactionsByTimeRange(long startTime, long endTime);
}