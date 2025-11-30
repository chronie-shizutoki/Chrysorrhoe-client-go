package com.chronie.chrysorrhoego.ui.wallet;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import androidx.test.core.app.ApplicationProvider;

import com.chronie.chrysorrhoego.data.remote.ApiService;
import com.chronie.chrysorrhoego.data.remote.dto.TransferRequest;
import com.chronie.chrysorrhoego.data.remote.dto.WalletInfoResponse;
import com.chronie.chrysorrhoego.data.repository.RemoteWalletRepository;
import com.chronie.chrysorrhoego.model.Transaction;
import com.chronie.chrysorrhoego.model.Wallet;
import com.chronie.chrysorrhoego.util.TestUtil;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.Assert.*;

/**
 * WalletViewModel测试类
 * 测试ViewModel的核心功能和数据流
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.O, Build.VERSION_CODES.P})
public class WalletViewModelTest {

    private static final String TAG = "WalletViewModelTest";
    private static final long TIMEOUT_MS = 10000; // 10秒超时

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private ApiService mockApiService;
    
    private RemoteWalletRepository repository;
    private WalletViewModel viewModel;
    private Context context;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        context = ApplicationProvider.getApplicationContext();
        
        // 创建Repository并注入Mock ApiService
        repository = new RemoteWalletRepository(mockApiService, context);
        
        // 创建ViewModel
        viewModel = new WalletViewModel(repository);
    }

    /**
     * 测试获取钱包信息功能
     */
    @Test
    public void testLoadWalletInfo() throws InterruptedException {
        // 准备测试数据
        Wallet mockWallet = TestUtil.generateTestWallet();
        
        // 模拟Repository行为
        when(repository.getWalletInfo()).thenReturn(Single.just(mockWallet));
        
        final CountDownLatch latch = new CountDownLatch(1);
        
        // 观察LiveData
        Observer<Wallet> observer = wallet -> {
            if (wallet != null) {
                // 验证数据
                assertEquals(mockWallet.getWalletId(), wallet.getWalletId());
                assertEquals(mockWallet.getBalance(), wallet.getBalance());
                latch.countDown();
            }
        };
        
        viewModel.getWalletData().observeForever(observer);
        
        // 触发加载
        viewModel.loadWalletInfo();
        
        // 等待结果
        latch.await(TIMEOUT_MS, TimeUnit.MILLISECONDS);
        
        // 验证加载状态
        assertFalse(viewModel.isLoading().getValue());
        assertNull(viewModel.getErrorMessage().getValue());
        
        // 清理
        viewModel.getWalletData().removeObserver(observer);
    }

    /**
     * 测试获取交易历史功能
     */
    @Test
    public void testLoadRecentTransactions() throws InterruptedException {
        // 准备测试数据
        List<Transaction> mockTransactions = TestUtil.generateTestTransactions(5);
        
        // 模拟Repository行为
        when(repository.getTransactionHistory(0, 10)).thenReturn(Single.just(mockTransactions));
        
        final CountDownLatch latch = new CountDownLatch(1);
        
        // 观察LiveData
        Observer<List<Transaction>> observer = transactions -> {
            if (transactions != null && !transactions.isEmpty()) {
                // 验证数据
                assertEquals(5, transactions.size());
                latch.countDown();
            }
        };
        
        viewModel.getRecentTransactions().observeForever(observer);
        
        // 触发加载
        viewModel.loadRecentTransactions();
        
        // 等待结果
        latch.await(TIMEOUT_MS, TimeUnit.MILLISECONDS);
        
        // 验证加载状态
        assertFalse(viewModel.isLoading().getValue());
        
        // 清理
        viewModel.getRecentTransactions().removeObserver(observer);
    }

    /**
     * 测试错误处理
     */
    @Test
    public void testErrorHandling() throws InterruptedException {
        // 模拟Repository错误
        when(repository.getWalletInfo()).thenReturn(Single.error(new RuntimeException("Test error")));
        
        final CountDownLatch latch = new CountDownLatch(1);
        
        // 观察错误信息
        Observer<String> observer = error -> {
            if (error != null && !error.isEmpty()) {
                assertTrue(error.contains("Test error"));
                latch.countDown();
            }
        };
        
        viewModel.getErrorMessage().observeForever(observer);
        
        // 触发加载
        viewModel.loadWalletInfo();
        
        // 等待结果
        latch.await(TIMEOUT_MS, TimeUnit.MILLISECONDS);
        
        // 验证加载状态
        assertFalse(viewModel.isLoading().getValue());
        
        // 清理
        viewModel.getErrorMessage().removeObserver(observer);
    }
}