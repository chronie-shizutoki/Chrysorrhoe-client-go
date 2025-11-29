package com.chronie.chrysorrhoego.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.chronie.chrysorrhoego.model.Wallet;
import com.chronie.chrysorrhoego.model.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 测试工具类
 * 提供各种测试辅助功能，包括生成测试数据、网络测试等
 */
public class TestUtil {

    private static final String TAG = "TestUtil";
    private static final Random RANDOM = new Random();
    
    /**
     * 生成测试钱包数据
     * @return 测试钱包对象
     */
    public static Wallet generateTestWallet() {
        Wallet wallet = new Wallet();
        wallet.setWalletId("wallet_" + generateRandomString(10));
        wallet.setBalance(RANDOM.nextDouble() * 10000 + 1000);
        wallet.setCurrencyCode("USD");
        wallet.setWalletName("Chronie Wallet");
        wallet.setLastUpdated(System.currentTimeMillis());
        return wallet;
    }
    
    /**
     * 生成测试交易历史数据
     * @param count 交易数量
     * @return 交易历史列表
     */
    public static List<Transaction> generateTestTransactions(int count) {
        List<Transaction> transactions = new ArrayList<>(count);
        
        // 生成发送者和接收者名称池
        String[] usernames = {"user123", "johndoe", "alice", "bob", "charlie", 
                             "david", "eve", "frank", "grace", "henry"};
        
        // 生成交易类型池
        Transaction.Type[] types = {Transaction.Type.TRANSFER, Transaction.Type.CDK_REDEEM, 
                                   Transaction.Type.SYSTEM, Transaction.Type.DEPOSIT};
        
        long currentTimeMillis = System.currentTimeMillis();
        
        for (int i = 0; i < count; i++) {
            Transaction transaction = new Transaction();
            
            // 设置基本字段
            transaction.setTransactionId("tx_" + generateRandomString(16));
            transaction.setAmount(Math.round((RANDOM.nextDouble() * 1000 + 1) * 100) / 100.0);
            transaction.setCurrencyCode("USD");
            
            // 随机设置方向（传入或传出）
            boolean isIncoming = RANDOM.nextBoolean();
            if (isIncoming) {
                transaction.setDirection(Transaction.Direction.INCOMING);
                transaction.setSender(usernames[RANDOM.nextInt(usernames.length)]);
                transaction.setRecipient("current_user");
            } else {
                transaction.setDirection(Transaction.Direction.OUTGOING);
                transaction.setSender("current_user");
                transaction.setRecipient(usernames[RANDOM.nextInt(usernames.length)]);
            }
            
            // 设置交易类型
            transaction.setType(types[RANDOM.nextInt(types.length)]);
            
            // 设置随机时间（过去30天内）
            long randomTimeOffset = RANDOM.nextInt(30 * 24 * 60 * 60 * 1000); // 30天内的随机毫秒数
            transaction.setTimestamp(currentTimeMillis - randomTimeOffset);
            
            // 设置交易状态
            transaction.setStatus(Transaction.Status.COMPLETED);
            
            // 设置交易备注
            transaction.setMemo(generateTransactionMemo(transaction.getType(), transaction.getDirection()));
            
            transactions.add(transaction);
        }
        
        return transactions;
    }
    
    /**
     * 生成交易备注
     */
    private static String generateTransactionMemo(Transaction.Type type, Transaction.Direction direction) {
        String[] transferMemos = {
            "Monthly allowance",
            "Payment for services",
            "Gift for you",
            "Loan repayment",
            "Salary",
            "Refund",
            "Transaction fee",
            "Purchase payment"
        };
        
        String[] cdkMemos = {
            "Redeemed promotional CDK",
            "Gift card redemption",
            "Special offer CDK",
            "Reward CDK"
        };
        
        String[] systemMemos = {
            "System adjustment",
            "Platform fee",
            "Reward for participation",
            "Maintenance reimbursement"
        };
        
        String[] depositMemos = {
            "Bank deposit",
            "Payment received",
            "Fund transfer",
            "Account top-up"
        };
        
        switch (type) {
            case TRANSFER:
                return transferMemos[RANDOM.nextInt(transferMemos.length)];
            case CDK_REDEEM:
                return cdkMemos[RANDOM.nextInt(cdkMemos.length)];
            case SYSTEM:
                return systemMemos[RANDOM.nextInt(systemMemos.length)];
            case DEPOSIT:
                return depositMemos[RANDOM.nextInt(depositMemos.length)];
            default:
                return "Transaction";
        }
    }
    
    /**
     * 生成随机字符串
     */
    private static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(RANDOM.nextInt(chars.length())));
        }
        return sb.toString();
    }
    
    /**
     * 模拟网络延迟
     */
    public static void simulateNetworkDelay(long millis) {
        try {
            Log.d(TAG, "Simulating network delay of " + millis + "ms");
            TimeUnit.MILLISECONDS.sleep(millis);
        } catch (InterruptedException e) {
            Log.e(TAG, "Network delay simulation interrupted", e);
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * 检查网络连接状态
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }
    
    /**
     * 生成测试CDK代码
     * @return 测试CDK代码
     */
    public static String generateTestCdk() {
        StringBuilder cdk = new StringBuilder();
        // 格式: XXXX-XXXX-XXXX
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        
        for (int i = 0; i < 3; i++) {
            if (i > 0) {
                cdk.append("-");
            }
            for (int j = 0; j < 4; j++) {
                cdk.append(chars.charAt(RANDOM.nextInt(chars.length())));
            }
        }
        return cdk.toString();
    }
    
    /**
     * 生成随机金额
     * @param min 最小值
     * @param max 最大值
     * @return 随机金额
     */
    public static double generateRandomAmount(double min, double max) {
        return Math.round((min + (max - min) * RANDOM.nextDouble()) * 100) / 100.0;
    }
    
    /**
     * 格式化交易日期
     */
    public static String formatTransactionDate(long timestamp) {
        Date date = new Date(timestamp);
        return date.toString();
    }
    
    /**
     * 生成测试交易ID
     */
    public static String generateTestTransactionId() {
        return "tx_" + System.currentTimeMillis() + "_" + generateRandomString(8);
    }
    
    /**
     * 生成错误场景的测试数据
     */
    public static List<Transaction> generateErrorTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        
        // 生成一个失败的交易
        Transaction failedTx = new Transaction();
        failedTx.setTransactionId(generateTestTransactionId());
        failedTx.setAmount(150.75);
        failedTx.setSender("current_user");
        failedTx.setRecipient("user_with_error");
        failedTx.setTimestamp(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(2));
        failedTx.setStatus(Transaction.Status.FAILED);
        failedTx.setType(Transaction.Type.TRANSFER);
        failedTx.setDirection(Transaction.Direction.OUTGOING);
        failedTx.setMemo("Failed transaction test");
        failedTx.setCurrencyCode("USD");
        transactions.add(failedTx);
        
        // 生成一个处理中的交易
        Transaction pendingTx = new Transaction();
        pendingTx.setTransactionId(generateTestTransactionId());
        pendingTx.setAmount(250.00);
        pendingTx.setSender("current_user");
        pendingTx.setRecipient("user_pending");
        pendingTx.setTimestamp(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(5));
        pendingTx.setStatus(Transaction.Status.PENDING);
        pendingTx.setType(Transaction.Type.TRANSFER);
        pendingTx.setDirection(Transaction.Direction.OUTGOING);
        pendingTx.setMemo("Pending transaction test");
        pendingTx.setCurrencyCode("USD");
        transactions.add(pendingTx);
        
        return transactions;
    }
    
    /**
     * 验证输入金额是否有效
     */
    public static boolean isValidAmount(String amountStr) {
        try {
            double amount = Double.parseDouble(amountStr);
            return amount > 0 && amount <= 1000000; // 金额必须大于0，且不超过100万
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * 验证CDK格式是否有效
     */
    public static boolean isValidCdkFormat(String cdk) {
        if (cdk == null) return false;
        // CDK格式：XXXX-XXXX-XXXX
        return cdk.matches("[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{4}");
    }
    
    /**
     * 获取设备类型描述
     */
    public static String getDeviceType() {
        String model = android.os.Build.MODEL;
        String manufacturer = android.os.Build.MANUFACTURER;
        String osVersion = android.os.Build.VERSION.RELEASE;
        
        return manufacturer + " " + model + " (Android " + osVersion + ")";
    }
}
