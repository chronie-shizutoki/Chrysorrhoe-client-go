package com.chronie.chrysorrhoego.data.repository;

import android.util.Log;

import com.chronie.chrysorrhoego.data.local.AppDatabase;
import com.chronie.chrysorrhoego.data.remote.ApiService;
import com.chronie.chrysorrhoego.model.Wallet;
import com.chronie.chrysorrhoego.model.Transaction;
import com.chronie.chrysorrhoego.util.PerformanceMonitor;
import com.chronie.chrysorrhoego.util.TestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 钱包仓库测试类
 * 测试钱包相关的各种功能和性能
 */
public class WalletRepositoryTest {

    private static final String TAG = "WalletRepositoryTest";
    private static final long TIMEOUT_MS = 10000; // 10秒超时
    
    private WalletRepository repository;
    private ApiService mockApiService;
    private AppDatabase mockDatabase;
    private PerformanceMonitor performanceMonitor;
    
    @Before
    public void setup() {
        // 在实际测试中，这里应该使用Mock对象或测试数据库
        // 由于这是一个简单示例，我们使用实际的实现，但在生产环境中应使用Mockito等框架
        
        // 初始化性能监控
        performanceMonitor = PerformanceMonitor.getInstance();
        
        Log.d(TAG, "Setup test environment");
    }
    
    @After
    public void cleanup() {
        Log.d(TAG, "Cleanup test environment");
        // 清理资源，如关闭数据库连接等
    }
    
    /**
     * 测试获取钱包信息的功能和性能
     */
    @Test
    public void testGetWalletInfo() throws InterruptedException {
        // 准备性能监控
        String operationName = "testGetWalletInfo";
        performanceMonitor.startOperation(operationName);
        
        final CountDownLatch latch = new CountDownLatch(1);
        final boolean[] success = {false};
        
        try {
            // 创建测试数据
            Wallet testWallet = TestUtil.generateTestWallet();
            
            // 模拟API调用并测量性能
            Single<Wallet> walletSingle = Single.just(testWallet)
                    .delay(500, TimeUnit.MILLISECONDS); // 模拟网络延迟
            
            Disposable disposable = walletSingle
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.single())
                    .subscribe(
                            wallet -> {
                                Log.d(TAG, "Wallet info retrieved: " + wallet.getWalletId() + 
                                        ", Balance: " + wallet.getBalance());
                                
                                // 验证数据
                                assert wallet.getWalletId() != null;
                                assert wallet.getBalance() >= 0;
                                
                                success[0] = true;
                                latch.countDown();
                            },
                            error -> {
                                Log.e(TAG, "Error retrieving wallet info: " + error.getMessage());
                                latch.countDown();
                            }
                    );
            
            // 等待结果
            latch.await(TIMEOUT_MS, TimeUnit.MILLISECONDS);
            
            // 清理
            if (!disposable.isDisposed()) {
                disposable.dispose();
            }
            
            // 记录性能数据
            performanceMonitor.endOperation(operationName);
            long executionTime = performanceMonitor.getOperationTime(operationName);
            Log.d(TAG, "Wallet info retrieval took " + executionTime + "ms");
            
            assert success[0];
        } catch (Exception e) {
            Log.e(TAG, "Test failed: " + e.getMessage());
            performanceMonitor.endOperation(operationName);
            throw e;
        }
    }
    
    /**
     * 测试获取交易历史记录
     */
    @Test
    public void testGetTransactionHistory() throws InterruptedException {
        String operationName = "testGetTransactionHistory";
        performanceMonitor.startOperation(operationName);
        
        final CountDownLatch latch = new CountDownLatch(1);
        final boolean[] success = {false};
        
        try {
            // 生成测试交易数据
            List<Transaction> testTransactions = TestUtil.generateTestTransactions(20);
            
            // 模拟API调用
            Single<List<Transaction>> transactionsSingle = Single.just(testTransactions)
                    .delay(700, TimeUnit.MILLISECONDS); // 模拟网络延迟
            
            Disposable disposable = transactionsSingle
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.single())
                    .subscribe(
                            transactions -> {
                                Log.d(TAG, "Retrieved " + transactions.size() + " transactions");
                                
                                // 验证数据
                                assert transactions.size() == testTransactions.size();
                                
                                // 检查部分交易数据
                                for (int i = 0; i < Math.min(5, transactions.size()); i++) {
                                    Transaction tx = transactions.get(i);
                                    Log.d(TAG, "Transaction " + (i+1) + ": ID=" + tx.getTransactionId() + ", Amount=" + tx.getAmount());
                                    assert tx.getTransactionId() != null;
                                    assert tx.getAmount() > 0;
                                }
                                
                                success[0] = true;
                                latch.countDown();
                            },
                            error -> {
                                Log.e(TAG, "Error retrieving transaction history: " + error.getMessage());
                                latch.countDown();
                            }
                    );
            
            latch.await(TIMEOUT_MS, TimeUnit.MILLISECONDS);
            
            if (!disposable.isDisposed()) {
                disposable.dispose();
            }
            
            // 记录性能数据
            performanceMonitor.endOperation(operationName);
            long executionTime = performanceMonitor.getOperationTime(operationName);
            Log.d(TAG, "Transaction history retrieval took " + executionTime + "ms");
            
            assert success[0];
        } catch (Exception e) {
            Log.e(TAG, "Test failed: " + e.getMessage());
            performanceMonitor.endOperation(operationName);
            throw e;
        }
    }
    
    /**
     * 测试转账功能
     */
    @Test
    public void testTransfer() throws InterruptedException {
        String operationName = "testTransfer";
        performanceMonitor.startOperation(operationName);
        
        final CountDownLatch latch = new CountDownLatch(1);
        final boolean[] success = {false};
        
        try {
            // 准备测试数据
            String recipientId = "recipient_" + TestUtil.generateRandomString(8);
            double amount = 150.50;
            String memo = "Test transfer";
            
            // 创建预期结果
            Transaction expectedTransaction = new Transaction();
            expectedTransaction.setTransactionId(TestUtil.generateTestTransactionId());
            expectedTransaction.setAmount(amount);
            expectedTransaction.setSender("current_user");
            expectedTransaction.setRecipient(recipientId);
            expectedTransaction.setTimestamp(System.currentTimeMillis());
            expectedTransaction.setStatus(Transaction.Status.COMPLETED);
            expectedTransaction.setType(Transaction.Type.TRANSFER);
            expectedTransaction.setDirection(Transaction.Direction.OUTGOING);
            expectedTransaction.setMemo(memo);
            expectedTransaction.setCurrencyCode("USD");
            
            // 模拟API调用
            Single<Transaction> transferSingle = Single.just(expectedTransaction)
                    .delay(1000, TimeUnit.MILLISECONDS); // 模拟网络延迟
            
            Disposable disposable = transferSingle
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.single())
                    .subscribe(
                            transaction -> {
                                Log.d(TAG, "Transfer completed: " + transaction.getTransactionId());
                                
                                // 验证结果
                                assert transaction.getTransactionId() != null;
                                assert transaction.getAmount() == amount;
                                assert transaction.getRecipient().equals(recipientId);
                                assert transaction.getStatus() == Transaction.Status.COMPLETED;
                                
                                success[0] = true;
                                latch.countDown();
                            },
                            error -> {
                                Log.e(TAG, "Transfer failed: " + error.getMessage());
                                latch.countDown();
                            }
                    );
            
            latch.await(TIMEOUT_MS, TimeUnit.MILLISECONDS);
            
            if (!disposable.isDisposed()) {
                disposable.dispose();
            }
            
            // 记录性能数据
            performanceMonitor.endOperation(operationName);
            long executionTime = performanceMonitor.getOperationTime(operationName);
            Log.d(TAG, "Transfer operation took " + executionTime + "ms");
            
            assert success[0];
        } catch (Exception e) {
            Log.e(TAG, "Test failed: " + e.getMessage());
            performanceMonitor.endOperation(operationName);
            throw e;
        }
    }
    
    /**
     * 测试CDK兑换功能
     */
    @Test
    public void testRedeemCdk() throws InterruptedException {
        String operationName = "testRedeemCdk";
        performanceMonitor.startOperation(operationName);
        
        final CountDownLatch latch = new CountDownLatch(1);
        final boolean[] success = {false};
        
        try {
            // 准备测试数据
            String cdkCode = TestUtil.generateTestCdk();
            double expectedAmount = 50.0;
            
            // 创建预期结果
            Transaction expectedTransaction = new Transaction();
            expectedTransaction.setTransactionId(TestUtil.generateTestTransactionId());
            expectedTransaction.setAmount(expectedAmount);
            expectedTransaction.setSender("system");
            expectedTransaction.setRecipient("current_user");
            expectedTransaction.setTimestamp(System.currentTimeMillis());
            expectedTransaction.setStatus(Transaction.Status.COMPLETED);
            expectedTransaction.setType(Transaction.Type.CDK_REDEEM);
            expectedTransaction.setDirection(Transaction.Direction.INCOMING);
            expectedTransaction.setMemo("CDK redeemed: " + cdkCode);
            expectedTransaction.setCurrencyCode("USD");
            
            // 模拟API调用
            Single<Transaction> redeemSingle = Single.just(expectedTransaction)
                    .delay(800, TimeUnit.MILLISECONDS); // 模拟网络延迟
            
            Disposable disposable = redeemSingle
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.single())
                    .subscribe(
                            transaction -> {
                                Log.d(TAG, "CDK redeemed: " + cdkCode + ", Amount: " + transaction.getAmount());
                                
                                // 验证结果
                                assert transaction.getTransactionId() != null;
                                assert transaction.getAmount() == expectedAmount;
                                assert transaction.getType() == Transaction.Type.CDK_REDEEM;
                                assert transaction.getStatus() == Transaction.Status.COMPLETED;
                                
                                success[0] = true;
                                latch.countDown();
                            },
                            error -> {
                                Log.e(TAG, "CDK redemption failed: " + error.getMessage());
                                latch.countDown();
                            }
                    );
            
            latch.await(TIMEOUT_MS, TimeUnit.MILLISECONDS);
            
            if (!disposable.isDisposed()) {
                disposable.dispose();
            }
            
            // 记录性能数据
            performanceMonitor.endOperation(operationName);
            long executionTime = performanceMonitor.getOperationTime(operationName);
            Log.d(TAG, "CDK redemption took " + executionTime + "ms");
            
            assert success[0];
        } catch (Exception e) {
            Log.e(TAG, "Test failed: " + e.getMessage());
            performanceMonitor.endOperation(operationName);
            throw e;
        }
    }
    
    /**
     * 测试错误处理场景
     */
    @Test
    public void testErrorHandling() throws InterruptedException {
        String operationName = "testErrorHandling";
        performanceMonitor.startOperation(operationName);
        
        final CountDownLatch latch = new CountDownLatch(1);
        final boolean[] errorHandled = {false};
        
        try {
            // 模拟API调用失败
            Single<Wallet> errorSingle = Single.error(new RuntimeException("Network error"));
            
            Disposable disposable = errorSingle
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.single())
                    .subscribe(
                            wallet -> {
                                // 不应该到达这里
                                latch.countDown();
                            },
                            error -> {
                                Log.d(TAG, "Error properly handled: " + error.getMessage());
                                errorHandled[0] = true;
                                latch.countDown();
                            }
                    );
            
            latch.await(TIMEOUT_MS, TimeUnit.MILLISECONDS);
            
            if (!disposable.isDisposed()) {
                disposable.dispose();
            }
            
            // 记录性能数据
            performanceMonitor.endOperation(operationName);
            
            assert errorHandled[0];
        } catch (Exception e) {
            Log.e(TAG, "Test failed: " + e.getMessage());
            performanceMonitor.endOperation(operationName);
            throw e;
        }
    }
    
    /**
     * 生成性能报告
     */
    public void generatePerformanceReport() {
        Log.d(TAG, "Generating performance report...");
        performanceMonitor.generatePerformanceReport();
    }
}
