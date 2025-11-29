package com.chronie.chrysorrhoego.ui.navigation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.chronie.chrysorrhoego.R;
import com.chronie.chrysorrhoego.ui.auth.AuthActivity;
import com.chronie.chrysorrhoego.ui.component.BottomNavigationBar;
import com.chronie.chrysorrhoego.ui.wallet.WalletDashboardActivity;
import com.chronie.chrysorrhoego.ui.transaction.TransactionHistoryActivity;

/**
 * 应用主导航活动，管理底部导航栏和内容区域的切换
 */
public class NavigationActivity extends AppCompatActivity {
    private static final String TAG = "NavigationActivity";
    
    private BottomNavigationBar mBottomNavigation;
    private FrameLayout mContentContainer;
    private FragmentManager mFragmentManager;
    private int mCurrentTabPosition = 0;
    
    // 导航标签常量
    public static final int TAB_WALLET = 0;
    public static final int TAB_TRANSACTION = 1;
    public static final int TAB_EXCHANGE = 2;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        
        // 初始化视图组件
        initViews();
        
        // 设置底部导航栏
        setupBottomNavigation();
        
        // 默认显示钱包页面
        switchToTab(TAB_WALLET);
    }
    
    private void initViews() {
        mBottomNavigation = findViewById(R.id.bottom_navigation);
        mContentContainer = findViewById(R.id.content_container);
        mFragmentManager = getSupportFragmentManager();
    }
    
    private void setupBottomNavigation() {
        // 添加导航标签
        mBottomNavigation.addTab(
                R.drawable.ic_wallet,
                R.drawable.ic_wallet_selected,
                getString(R.string.tab_wallet)
        );
        
        mBottomNavigation.addTab(
                R.drawable.ic_transaction,
                R.drawable.ic_transaction_selected,
                getString(R.string.tab_transaction)
        );
        
        mBottomNavigation.addTab(
                R.drawable.ic_exchange,
                R.drawable.ic_exchange_selected,
                getString(R.string.tab_exchange)
        );
        
        // 设置标签选中监听器
        mBottomNavigation.setOnTabSelectedListener(position -> {
            switchToTab(position);
        });
    }
    
    /**
     * 切换到指定标签页
     */
    private void switchToTab(int position) {
        if (position == mCurrentTabPosition) {
            return;
        }
        
        mCurrentTabPosition = position;
        
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        
        // 隐藏所有Fragment
        hideAllFragments(transaction);
        
        // 根据位置显示对应的Fragment或启动相应活动
        switch (position) {
            case TAB_WALLET:
                // 由于WalletDashboardActivity比较复杂，这里暂时使用Intent跳转到该活动
                startActivity(new Intent(this, WalletDashboardActivity.class));
                break;
            
            case TAB_TRANSACTION:
                // 使用Intent跳转到交易历史活动
                startActivity(new Intent(this, TransactionHistoryActivity.class));
                break;
            
            case TAB_EXCHANGE:
                // CDK兑换功能暂不可用
                Toast.makeText(this, "CDK兑换功能暂不可用", Toast.LENGTH_SHORT).show();
                // 返回到钱包页面
                mCurrentTabPosition = TAB_WALLET;
                // 移除对不存在方法的调用
                break;
            
            default:
                Log.e(TAG, "Invalid tab position: " + position);
                break;
        }
        
        transaction.commitAllowingStateLoss();
    }
    
    /**
     * 隐藏所有Fragment
     */
    private void hideAllFragments(FragmentTransaction transaction) {
        for (Fragment fragment : mFragmentManager.getFragments()) {
            if (fragment != null && !fragment.isHidden()) {
                transaction.hide(fragment);
            }
        }
    }
    
    /**
     * 获取指定位置的Fragment
     */
    private Fragment getFragment(int position) {
        String tag = getFragmentTag(position);
        return mFragmentManager.findFragmentByTag(tag);
    }
    
    /**
     * 获取Fragment的标签
     */
    private String getFragmentTag(int position) {
        return "fragment_" + position;
    }
    
    /**
     * 处理返回按钮事件
     */
    @Override
    public void onBackPressed() {
        // 如果当前不是首页，则返回到首页
        if (mCurrentTabPosition != TAB_WALLET) {
            mBottomNavigation.setSelectedTab(TAB_WALLET);
            switchToTab(TAB_WALLET);
        } else {
            // 如果是首页，则退出应用
            super.onBackPressed();
        }
    }
    
    /**
     * 登出功能
     */
    public void logout() {
        // 清除用户登录状态和相关数据
        // TODO: 实现用户登出逻辑
        
        // 跳转到登录页面
        Intent intent = new Intent(this, AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    /**
     * 更新主题样式
     */
    @Override
    protected void onResume() {
        super.onResume();
        // 更新底部导航栏主题
        if (mBottomNavigation != null) {
            mBottomNavigation.updateForTheme();
        }
    }
}