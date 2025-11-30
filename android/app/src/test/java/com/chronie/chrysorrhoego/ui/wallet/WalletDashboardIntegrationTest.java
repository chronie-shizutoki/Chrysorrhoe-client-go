package com.chronie.chrysorrhoego.ui.wallet;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.FragmentTransaction;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.chronie.chrysorrhoego.R;
import com.chronie.chrysorrhoego.data.repository.RemoteWalletRepository;
import com.chronie.chrysorrhoego.model.Wallet;
import com.chronie.chrysorrhoego.model.Transaction;
import com.chronie.chrysorrhoego.util.TestUtil;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import io.reactivex.Single;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

/**
 * 钱包仪表盘集成测试
 * 测试Fragment与ViewModel之间的交互
 */
@RunWith(AndroidJUnit4.class)
public class WalletDashboardIntegrationTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private RemoteWalletRepository repository;
    
    private WalletViewModel viewModel;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        
        // 创建ViewModel并注入Mock Repository
        viewModel = new WalletViewModel(repository);
    }

    /**
     * 测试Fragment加载并显示钱包信息
     */
    @Test
    public void testFragmentDisplaysWalletInfo() {
        // 准备测试数据
        Wallet mockWallet = TestUtil.generateTestWallet();
        List<Transaction> mockTransactions = TestUtil.generateTestTransactions(3);
        
        // 模拟Repository行为
        when(repository.getWalletInfo()).thenReturn(Single.just(mockWallet));
        when(repository.getTransactionHistory(anyInt(), anyInt())).thenReturn(Single.just(mockTransactions));
        
        // 启动ActivityScenario
        try (ActivityScenario<TestActivity> scenario = ActivityScenario.launch(TestActivity.class)) {
            scenario.onActivity(activity -> {
                // 在测试Activity中添加Fragment
                WalletDashboardFragment fragment = new WalletDashboardFragment();
                
                // 替换Fragment的ViewModel
                fragment.setViewModel(viewModel);
                
                FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content_container, fragment);
                transaction.commit();
            });
            
            // 模拟等待Fragment加载完成
            Thread.sleep(1000);
            
            // 触发ViewModel加载数据
            viewModel.loadWalletInfo();
            viewModel.loadRecentTransactions();
            
            // 等待数据显示
            Thread.sleep(1000);
            
            // 验证UI显示
            onView(withId(R.id.wallet_balance))
                .check(matches(isDisplayed()))
                .check(matches(withText(String.valueOf(mockWallet.getBalance()))));
            
            onView(withId(R.id.wallet_id))
                .check(matches(isDisplayed()))
                .check(matches(withText(mockWallet.getWalletId())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试Fragment的按钮点击事件
     */
    @Test
    public void testFragmentButtonClicks() {
        // 准备测试数据
        Wallet mockWallet = TestUtil.generateTestWallet();
        
        // 模拟Repository行为
        when(repository.getWalletInfo()).thenReturn(Single.just(mockWallet));
        
        // 启动ActivityScenario
        try (ActivityScenario<TestActivity> scenario = ActivityScenario.launch(TestActivity.class)) {
            scenario.onActivity(activity -> {
                // 在测试Activity中添加Fragment
                WalletDashboardFragment fragment = new WalletDashboardFragment();
                fragment.setViewModel(viewModel);
                
                FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content_container, fragment);
                transaction.commit();
            });
            
            // 模拟等待Fragment加载完成
            Thread.sleep(1000);
            
            // 模拟点击转账按钮
            onView(withId(R.id.btn_transfer))
                .check(matches(isDisplayed()))
                .perform(click());
            
            // 模拟点击交易历史按钮
            onView(withId(R.id.btn_transaction_history))
                .check(matches(isDisplayed()))
                .perform(click());
            
            // 模拟点击CDK兑换按钮
            onView(withId(R.id.btn_cdk_redeem))
                .check(matches(isDisplayed()))
                .perform(click());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 用于测试的Activity
     */
    public static class TestActivity extends androidx.appcompat.app.AppCompatActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_test);
        }
    }
}