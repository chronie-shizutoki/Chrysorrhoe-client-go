package com.chronie.chrysorrhoego.ui.wallet;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.List;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chronie.chrysorrhoego.R;
import com.chronie.chrysorrhoego.model.Transaction;
import com.chronie.chrysorrhoego.model.Wallet;
import com.chronie.chrysorrhoego.ui.theme.ThemeObserver;
import com.chronie.chrysorrhoego.ui.theme.ThemeManager;
import com.chronie.chrysorrhoego.util.PerformanceMonitor;

/**
 * 钱包仪表盘Fragment
 * 显示钱包余额、转账功能入口和最近交易预览
 */
public class WalletDashboardFragment extends Fragment implements ThemeObserver {
    private static final String TAG = "WalletDashboardFragment";

    private TextView mBalanceAmount;
    private TextView mBalanceStatusText;
    private TextView mErrorMessage;
    private ProgressBar mLoadingIndicator;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Toolbar mToolbar;
    private ThemeManager mThemeManager;
    private WalletViewModel mViewModel;

    public WalletDashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wallet_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 初始化UI组件
        mBalanceAmount = view.findViewById(R.id.balance_amount);
        mBalanceStatusText = view.findViewById(R.id.balance_status_text);
        mErrorMessage = view.findViewById(R.id.error_text);
        mLoadingIndicator = view.findViewById(R.id.loading_indicator);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        mToolbar = view.findViewById(R.id.toolbar);

        // 初始化主题管理器
        mThemeManager = ThemeManager.getInstance();
        mThemeManager.registerObserver(this);
        updateForTheme();

        // 初始化ViewModel
        initViewModel();

        // 设置按钮事件
        setupButtonListeners(view);
        
        // 设置刷新功能
        setupRefreshFeature();
        
        // 加载钱包数据
        mViewModel.loadWalletData();
        mViewModel.loadRecentTransactions();
    }

    private void initViewModel() {
        // 如果ViewModel未被外部设置（如测试中），则使用工厂方法创建
        if (mViewModel == null && requireActivity() != null) {
            mViewModel = new ViewModelProvider(
                    this,
                    new WalletViewModelFactory(requireContext())
            ).get(WalletViewModel.class);
        }

        // 观察钱包数据变化
        if (mViewModel != null) {
            mViewModel.getWalletData().observe(getViewLifecycleOwner(), this::updateWalletData);

            // 观察加载状态
            mViewModel.isLoading().observe(getViewLifecycleOwner(), this::showLoading);

            // 观察刷新状态
            mViewModel.isRefreshing().observe(getViewLifecycleOwner(), this::setRefreshing);

            // 观察错误信息
            mViewModel.getErrorMessage().observe(getViewLifecycleOwner(), this::showError);

            // 观察转账成功状态
            mViewModel.getTransferSuccess().observe(getViewLifecycleOwner(), success -> {
                if (success) {
                    showToast(getString(R.string.transfer_success));
                }
            });

            // 观察CDK兑换成功状态
            mViewModel.getCdkSuccess().observe(getViewLifecycleOwner(), success -> {
                if (success) {
                    showToast(getString(R.string.cdk_redeem_success));
                }
            });

            // 观察交易记录变化
            mViewModel.getRecentTransactions().observe(getViewLifecycleOwner(), this::updateTransactions);
        }
    }
    
    /**
     * 设置ViewModel实例（用于测试）
     * @param viewModel 要设置的ViewModel实例
     */
    public void setViewModel(WalletViewModel viewModel) {
        this.mViewModel = viewModel;
        initViewModel();
    }

    private void setupRefreshFeature() {
        // 设置下拉刷新监听器
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setOnRefreshListener(this::refreshWalletData);
            // 设置刷新颜色
            mSwipeRefreshLayout.setColorSchemeResources(
                    android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light
            );
        }
        
        // 设置toolbar刷新按钮
        if (mToolbar != null) {
            mToolbar.inflateMenu(R.menu.wallet_dashboard_menu);
            mToolbar.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_refresh) {
                    refreshWalletData();
                    return true;
                }
                return false;
            });
        }
    }
    
    /**
     * 刷新钱包数据
     */
    private void refreshWalletData() {
        // 开始性能监控
        PerformanceMonitor.getInstance().startMethodMonitoring("refreshWalletData");
        
        // 刷新ViewModel中的数据
        mViewModel.refreshAllData();
        
        // 记录刷新操作
        Log.d(TAG, "Wallet data refresh initiated");
        
        // 结束性能监控
        PerformanceMonitor.getInstance().endMethodMonitoring("refreshWalletData");
    }
    
    /**
     * 设置刷新状态
     */
    private void setRefreshing(boolean refreshing) {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(refreshing);
        }
        
        // 更新工具栏刷新图标状态
        if (mToolbar != null && mToolbar.getMenu().findItem(R.id.action_refresh) != null) {
            MenuItem refreshItem = mToolbar.getMenu().findItem(R.id.action_refresh);
            if (refreshing) {
                refreshItem.setActionView(R.layout.actionbar_progress);
            } else {
                refreshItem.setActionView(null);
            }
        }
    }
    
    private void setupButtonListeners(View view) {
        // 初始化转账按钮
        Button transferButton = view.findViewById(R.id.transfer_button);
        if (transferButton != null) {
            transferButton.setOnClickListener(v -> {
                // 导航到转账页面
                if (getActivity() instanceof WalletDashboardListener) {
                    ((WalletDashboardListener) getActivity()).onNavigateToTransfer();
                }
            });
        }

        // 初始化CDK兑换按钮
        Button cdkRedeemButton = view.findViewById(R.id.cdk_redeem_button);
        if (cdkRedeemButton != null) {
            cdkRedeemButton.setOnClickListener(v -> {
                // 导航到CDK兑换页面
                if (getActivity() instanceof WalletDashboardListener) {
                    ((WalletDashboardListener) getActivity()).onNavigateToCdkRedeem();
                }
            });
        }

        // 初始化交易历史按钮
        Button transactionHistoryButton = view.findViewById(R.id.transaction_history_button);
        if (transactionHistoryButton != null) {
            transactionHistoryButton.setOnClickListener(v -> {
                // 导航到交易历史页面
                if (getActivity() instanceof WalletDashboardListener) {
                    ((WalletDashboardListener) getActivity()).onNavigateToTransactionHistory();
                }
            });
        }

        // 初始化查看全部交易按钮
        Button viewAllTransactionsButton = view.findViewById(R.id.view_all_transactions_button);
        if (viewAllTransactionsButton != null) {
            viewAllTransactionsButton.setOnClickListener(v -> {
                // 导航到交易历史页面
                if (getActivity() instanceof WalletDashboardListener) {
                    ((WalletDashboardListener) getActivity()).onNavigateToTransactionHistory();
                }
            });
        }

        // 初始化登出按钮
        Button logoutButton = view.findViewById(R.id.logout_button);
        if (logoutButton != null) {
            logoutButton.setOnClickListener(v -> {
                // 处理登出操作
                if (getActivity() instanceof WalletDashboardListener) {
                    ((WalletDashboardListener) getActivity()).onLogout();
                }
            });
        }
        
        // 初始化刷新按钮（备用方式）
        Button refreshButton = view.findViewById(R.id.refresh_button);
        if (refreshButton != null) {
            refreshButton.setOnClickListener(v -> refreshWalletData());
        }
    }

    private void updateWalletData(Wallet wallet) {
        if (wallet != null) {
            // 格式化余额显示
            String formattedBalance = String.format("%,.2f", wallet.getBalance());
            mBalanceAmount.setText(formattedBalance);
            updateBalanceStatus(wallet.getBalance());
        }
    }

    private void updateTransactions(List<Transaction> transactions) {
        // 这里可以更新交易列表UI
        // 例如使用RecyclerView显示交易记录
        // 暂时不实现，因为fragment_wallet_dashboard.xml中没有交易列表视图
    }

    private void updateBalanceStatus(double balance) {
        if (balance > 1000) {
            mBalanceStatusText.setText(R.string.balance_status_excellent);
        } else if (balance > 500) {
            mBalanceStatusText.setText(R.string.balance_status_good);
        } else if (balance > 100) {
            mBalanceStatusText.setText(R.string.balance_status_fair);
        } else {
            mBalanceStatusText.setText(R.string.balance_status_low);
        }
    }

    private void showLoading(boolean loading) {
        mLoadingIndicator.setVisibility(loading ? View.VISIBLE : View.GONE);
        if (loading) {
            mErrorMessage.setVisibility(View.GONE);
        }
    }

    private void showError(String error) {
        if (error != null) {
            mErrorMessage.setText(error);
            mErrorMessage.setVisibility(View.VISIBLE);
        } else {
            mErrorMessage.setVisibility(View.GONE);
        }
    }

    private void showToast(String message) {
        if (isAdded()) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onThemeChanged(boolean isDarkMode) {
        updateForTheme();
    }

    private void updateForTheme() {
        if (isAdded() && getView() != null) {
            // 根据当前主题更新视图样式
            // 这里可以根据需要添加主题相关的UI调整
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mThemeManager != null) {
            mThemeManager.unregisterObserver(this);
        }
    }

    /**
     * 与父Activity通信的接口
     */
    public interface WalletDashboardListener {
        void onNavigateToTransfer();
        void onNavigateToCdkRedeem();
        void onNavigateToTransactionHistory();
        void onLogout();
    }
}