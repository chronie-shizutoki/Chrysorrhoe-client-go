package com.chronie.chrysorrhoego.data.local;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.chronie.chrysorrhoego.model.Wallet;
import com.chronie.chrysorrhoego.model.Transaction;

/**
 * 应用数据库类
 * 使用Room持久化库实现本地数据存储
 */
@Database(entities = {Wallet.class, Transaction.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    
    private static final String DATABASE_NAME = "chronie_wallet_db";
    private static volatile AppDatabase instance;
    
    // 获取Wallet相关数据访问对象
    public abstract WalletDao walletDao();
    
    // 获取Transaction相关数据访问对象
    public abstract TransactionDao transactionDao();
    
    /**
     * 获取AppDatabase单例实例
     * 使用双重检查锁定模式确保线程安全
     * 
     * @param context 上下文
     * @return AppDatabase实例
     */
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            DATABASE_NAME)
                            .fallbackToDestructiveMigration() // 数据库版本升级时销毁旧数据并重建
                            .allowMainThreadQueries() // 允许在主线程查询数据库（仅用于简单操作）
                            .build();
                }
            }
        }
        return instance;
    }
    
    /**
     * 清除数据库实例
     * 通常在应用关闭或用户登出时调用
     */
    public static void clearInstance() {
        instance = null;
    }
}