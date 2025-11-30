package com.chronie.chrysorrhoego.ui.navigation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import com.chronie.chrysorrhoego.ui.component.CustomButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.chronie.chrysorrhoego.R;
import com.chronie.chrysorrhoego.ui.auth.AuthActivity;
import com.chronie.chrysorrhoego.ui.component.BottomNavigationBar;
import com.chronie.chrysorrhoego.ui.wallet.WalletDashboardFragment;
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
        
        // 清除内容容器中的现有视图
        mContentContainer.removeAllViews();
        
        // 根据位置加载对应的内容
        switch (position) {
            case TAB_WALLET:
                // 加载钱包仪表板Fragment
                loadFragment(new WalletDashboardFragment());
                break;
            
            case TAB_TRANSACTION:
                // 直接加载交易历史布局到内容容器
                LayoutInflater.from(this).inflate(R.layout.activity_transaction_history, mContentContainer, true);
                // 初始化交易历史功能
                initTransactionHistory();
                break;
            
            case TAB_EXCHANGE:
                // CDK兑换功能暂不可用，显示提示信息
                TextView textView = new TextView(this);
                textView.setText("CDK兑换功能暂不可用");
                textView.setGravity(Gravity.CENTER);
                textView.setTextSize(16);
                textView.setTextColor(getResources().getColor(R.color.text_primary)); // 使用正确的颜色资源名称
                mContentContainer.addView(textView, new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                ));
                break;
            
            default:
                Log.e(TAG, "Invalid tab position: " + position);
                break;
        }
    }
    
    /**
     * 初始化钱包仪表板功能
     */
    private void initWalletDashboard() {
        // 初始化钱包仪表板的基本功能
        // 这里简化处理，主要确保界面可见
        Log.d(TAG, "Initializing wallet dashboard");
        
        // 设置登出按钮点击事件
        CustomButton logoutButton = findViewById(R.id.logout_button);
        if (logoutButton != null) {
            logoutButton.setOnClickListener(v -> logout());
        }
    }
    
    /**
     * 初始化交易历史功能
     */
    private void initTransactionHistory() {
        // 初始化交易历史的基本功能
        // 这里简化处理，主要确保界面可见
        Log.d(TAG, "Initializing transaction history");
        
        // 设置工具栏
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(getString(R.string.tab_transaction)); // 使用已有的标签字符串资源
                getSupportActionBar().setDisplayHomeAsUpEnabled(false); // 不显示返回按钮，因为是在导航中
            }
        }
        
        // 显示空状态，因为我们没有实际的交易数据
        TextView emptyView = findViewById(R.id.tv_empty);
        if (emptyView != null) {
            emptyView.setVisibility(View.VISIBLE);
        }
        
        // 隐藏RecyclerView和加载指示器
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        if (recyclerView != null) {
            recyclerView.setVisibility(View.GONE);
        }
        
        ProgressBar progressBar = findViewById(R.id.progress_bar);
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
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
     * 加载Fragment到内容容器
     */
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        // 使用content_container作为Fragment容器，并添加标签以便后续查找
        String tag = getFragmentTag(mCurrentTabPosition);
        transaction.replace(mContentContainer.getId(), fragment, tag);
        transaction.commit();
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