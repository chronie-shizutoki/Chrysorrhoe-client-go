package com.chronie.chrysorrhoego.ui.transaction;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

// 添加缺失的导入
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.ripple.RippleUtils;
import com.chronie.chrysorrhoego.R;

import com.chronie.chrysorrhoego.R;
import com.chronie.chrysorrhoego.data.remote.ApiClient;
import com.chronie.chrysorrhoego.data.remote.ApiService;
import com.chronie.chrysorrhoego.data.remote.dto.TransferResponse;
import com.chronie.chrysorrhoego.util.ErrorHandler;
import com.chronie.chrysorrhoego.util.SecurityUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 转账活动，处理用户之间的金额转账
 */
public class TransferActivity extends AppCompatActivity {
    private static final String TAG = "TransferActivity";
    private TextInputEditText recipientInput;
    private TextInputEditText amountInput;
    private TextInputEditText memoInput;
    private TextInputLayout recipientLayout;
    private TextInputLayout amountLayout;
    private TextInputLayout memoLayout;
    private MaterialButton confirmButton;
    private TextView errorText;
    private ProgressBar progressBar;
    private String username;
    private ApiService apiService;
    private TextView resultTitle;
    private TextView resultMessage;
    private View resultCard;
    private View containerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 添加防止截屏/录屏的安全设置
        getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE, 
                            android.view.WindowManager.LayoutParams.FLAG_SECURE);
        // 明确设置支持所有屏幕方向
        setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        setContentView(R.layout.activity_transfer);
        
        // 获取传递的用户名
        if (getIntent().hasExtra("username")) {
            username = getIntent().getStringExtra("username");
        }
        
        // 初始化API服务
        apiService = ApiClient.getInstance(this).getApiService();
        
        // 初始化视图
        initializeViews();
        
        // 设置监听器
        setupListeners();
        
        // 添加页面进入动画
        animatePageEntry();
    }
    
    /**
     * 页面进入动画 - 优化为更现代的级联动画效果
     */
    private void animatePageEntry() {
        if (containerView != null) {
            // 设置初始状态
            containerView.setAlpha(0f);
            containerView.setTranslationY(30f);
            
            // 使用Material设计推荐的插值器和时长
            containerView.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(400)
                .setInterpolator(new FastOutSlowInInterpolator())
                .start();
                
            // 级联动画 - 卡片逐个出现
            View balanceCard = findViewById(R.id.balance_card);
            if (balanceCard != null) {
                animateCardDelayed(balanceCard, 150);
            }
            // 暂时注释掉对transfer_form_card的引用，因为在编译时找不到该id
            // View transferFormCard = findViewById(R.id.transfer_form_card);
            // if (transferFormCard != null) {
            //     animateCardDelayed(transferFormCard, 300);
            // }
        }
    }
    
    /**
     * 为卡片视图添加延迟动画
     */
    private void animateCardDelayed(final View card, long delay) {
        if (card != null) {
            card.setAlpha(0f);
            card.setTranslationY(20f);
            card.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(350)
                .setInterpolator(new FastOutSlowInInterpolator())
                .setStartDelay(delay)
                .start();
        }
    }
    
    /**
     * 页面退出动画 - 优化为更平滑的淡出效果
     */
    private void animatePageExit() {
        if (containerView != null) {
            containerView.animate()
                .alpha(0f)
                .translationY(-20f)
                .setDuration(300)
                .setInterpolator(new FastOutSlowInInterpolator())
                .withEndAction(() -> {
                    finish();
                    overridePendingTransition(0, 0); // 防止系统默认动画
                })
                .start();
        } else {
            finish();
        }
    }
    
    /**
     * 初始化视图
     */
    private void initializeViews() {
        // 初始化所有UI组件
        containerView = findViewById(R.id.container);
        recipientInput = findViewById(R.id.edit_text_recipient);
        recipientLayout = findViewById(R.id.text_input_recipient);
        amountInput = findViewById(R.id.edit_text_amount);
        amountLayout = findViewById(R.id.text_input_amount);
        memoInput = findViewById(R.id.edit_text_memo);
        memoLayout = findViewById(R.id.text_input_memo);
        confirmButton = findViewById(R.id.button_transfer);
        errorText = findViewById(R.id.error_text);
        resultCard = findViewById(R.id.result_card);
        resultTitle = findViewById(R.id.result_title);
        resultMessage = findViewById(R.id.result_message);
        progressBar = findViewById(R.id.progress_bar);
        
        // 确保只使用一种方式设置ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.transfer));
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        
        // 初始启用按钮，让用户可以点击并得到即时反馈
        confirmButton.setEnabled(true);
    }

    private void setupListeners() {
        // 改进按钮点击交互 - 使用Material推荐的水波纹效果和状态反馈
        confirmButton.setClickable(true);
        confirmButton.setFocusable(true);
        confirmButton.setRippleColor(ContextCompat.getColorStateList(this, R.color.primary_ripple));
        
        // 优化按钮点击动画 - 更符合Material Design规范
        confirmButton.setOnClickListener(v -> {
            // 先隐藏键盘
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null && getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
            
            // 添加更现代的按钮按下效果
            v.setScaleX(0.98f);
            v.setScaleY(0.98f);
            v.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(150)
                .setInterpolator(new FastOutSlowInInterpolator())
                .start();
            
            // 执行转账操作
            handleTransfer();
        });
        
        // 添加实时验证和焦点动画 - 实现现代化表单交互
        setupFormValidationAndFocusHandling();
        
        // 添加输入法导航
        setupImeNavigation();
    }
    
    /**
     * 设置表单验证和焦点处理
     */
    private void setupFormValidationAndFocusHandling() {
        // 统一的文本变化监听器类
        class ValidationTextWatcher implements TextWatcher {
            private final TextInputLayout layout;
            private final boolean isAmount;
            private long lastValidationTime = 0;
            private static final long DEBOUNCE_DELAY_MS = 500; // 防抖延迟
            
            ValidationTextWatcher(TextInputLayout layout, boolean isAmount) {
                this.layout = layout;
                this.isAmount = isAmount;
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 清除错误状态
                if (layout.getError() != null) {
                    layout.setError(null);
                }
                
                if (errorText.getVisibility() == View.VISIBLE) {
                    errorText.setVisibility(View.GONE);
                }
                
                // 防抖实时验证
                final long currentTime = System.currentTimeMillis();
                lastValidationTime = currentTime;
                
                new Handler().postDelayed(() -> {
                    if (lastValidationTime == currentTime) {
                        // 只有在没有错误且输入非空时才进行实时验证
                        if (!s.toString().trim().isEmpty()) {
                            validateInput(s.toString(), layout, isAmount);
                        }
                    }
                }, DEBOUNCE_DELAY_MS);
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        }
        
        // 为输入字段添加焦点动画
        setupFocusAnimation(recipientInput, recipientLayout);
        setupFocusAnimation(amountInput, amountLayout);
        setupFocusAnimation(memoInput, memoLayout);
        
        // 添加文本变化监听
        recipientInput.addTextChangedListener(new ValidationTextWatcher(recipientLayout, false));
        amountInput.addTextChangedListener(new ValidationTextWatcher(amountLayout, true));
        memoInput.addTextChangedListener(new ValidationTextWatcher(memoLayout, false));
    }
    
    /**
     * 设置输入字段焦点动画效果
     */
    private void setupFocusAnimation(final TextInputEditText editText, final TextInputLayout layout) {
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                // 焦点获得时的动画效果
                layout.setBoxStrokeColor(ContextCompat.getColor(this, R.color.primary));
                layout.animate()
                    .scaleX(1.01f)
                    .scaleY(1.01f)
                    .setDuration(150)
                    .withEndAction(() -> layout.animate().scaleX(1f).scaleY(1f).setDuration(150).start())
                    .start();
            } else {
                // 失去焦点时恢复默认颜色
                layout.setBoxStrokeColor(ContextCompat.getColor(this, R.color.outline));
                
                // 失去焦点时的验证
                String text = editText.getText().toString().trim();
                if (!text.isEmpty()) {
                    validateInput(text, layout, layout == amountLayout);
                }
            }
        });
    }
    
    /**
     * 验证输入内容
     */
    private void validateInput(String input, TextInputLayout layout, boolean isAmount) {
        if (isAmount) {
            try {
                double amount = Double.parseDouble(input);
                if (amount <= 0) {
                    layout.setError(getString(R.string.amount_must_be_positive));
                }
            } catch (NumberFormatException e) {
                if (!input.isEmpty()) {
                    layout.setError(getString(R.string.invalid_amount_format));
                }
            }
        } else {
            // 非金额字段的验证
            if (layout == recipientLayout && input.length() < 3) {
                layout.setError(getString(R.string.username_too_short));
            } else if (!SecurityUtils.isInputSafe(input)) {
                layout.setError(getString(R.string.invalid_input));
            }
        }
    }

    private void validateInputs() {
        // 按钮始终保持启用状态，在handleTransfer中执行实际验证
        // 这样用户可以随时点击并获得即时反馈
        confirmButton.setEnabled(true);
    }

    private void handleTransfer() {
        // 重置所有错误状态
        resetErrorStates();
        
        String recipient = recipientInput.getText().toString().trim();
        String amountStr = amountInput.getText().toString().trim();
        String memo = memoInput.getText().toString().trim();
        boolean hasError = false;

        // 输入验证 - 更详细的验证逻辑
        if (recipient.isEmpty()) {
            showFieldError(recipientLayout, getString(R.string.recipient_required));
            hasError = true;
        } else if (recipient.length() < 3) {
            showFieldError(recipientLayout, getString(R.string.username_too_short));
            hasError = true;
        }

        if (amountStr.isEmpty()) {
            showFieldError(amountLayout, getString(R.string.amount_required));
            hasError = true;
        } else {
            try {
                double amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    showFieldError(amountLayout, getString(R.string.amount_must_be_positive));
                    hasError = true;
                }
            } catch (NumberFormatException e) {
                showFieldError(amountLayout, getString(R.string.invalid_amount_format));
                hasError = true;
            }
        }

        // 安全检查
        if (!hasError && (!SecurityUtils.isInputSafe(recipient) || !SecurityUtils.isInputSafe(memo))) {
            showAnimatedError(getString(R.string.invalid_input));
            return;
        }

        if (hasError) {
            // 显示一般性错误提示
            showAnimatedError(getString(R.string.please_fix_errors));
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            
            // 添加转账确认对话框 - 防止误操作
            showTransferConfirmationDialog(recipient, amount, memo);

        } catch (NumberFormatException e) {
            // 这个错误应该已经在前面被捕获，但保留作为额外保障
            showFieldError(amountLayout, getString(R.string.invalid_amount_format));
        }
    }
    
    /**
     * 显示带动画效果的字段错误
     */
    private void showFieldError(TextInputLayout layout, String message) {
        layout.setError(message);
        
        // 添加错误时的轻微抖动动画
        layout.animate()
            .translationX(10f)
            .setDuration(70)
            .withEndAction(() -> layout.animate()
                .translationX(-10f)
                .setDuration(70)
                .withEndAction(() -> layout.animate()
                    .translationX(0f)
                    .setDuration(70)
                    .start())
                .start())
            .start();
    }
    
    /**
     * 显示带动画效果的全局错误
     */
    private void showAnimatedError(String message) {
        errorText.setText(message);
        errorText.setVisibility(View.VISIBLE);
        errorText.setAlpha(0f);
        
        // 添加错误淡入动画
        errorText.animate()
            .alpha(1f)
            .setDuration(300)
            .start();
    }
    
    /**
     * 显示转账确认对话框
     */
    private void showTransferConfirmationDialog(String recipient, double amount, String memo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, androidx.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert);
        builder.setTitle(getString(R.string.confirm_transfer))
            .setMessage(getString(R.string.confirm_transfer_message, amount, recipient))
            .setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                // 显示加载状态并执行转账
                showLoading(true);
                transferByUsername(recipient, amount, memo);
            })
            .setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss())
            .setCancelable(true);
            
        AlertDialog dialog = builder.create();
        dialog.show();
        
        // 自定义按钮样式
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(this, R.color.primary));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(this, R.color.outline));
    }

    private void transferByUsername(String recipient, double amount, String memo) {
        // 根据新的API规范，需要提供发送方用户名、接收方用户名、金额和描述
        // 使用username作为发送方用户名（fromUsername）
        Call<TransferResponse> call = apiService.transfer(
                username, // fromUsername - 发送方用户名
                recipient, // toUsername - 接收方用户名
                String.valueOf(amount), // 金额
                memo // description - 描述，对应原来的memo
        );

        call.enqueue(new Callback<TransferResponse>() {
            @Override
            public void onResponse(Call<TransferResponse> call, Response<TransferResponse> response) {
                showLoading(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    TransferResponse transferResponse = response.body();
                    if (transferResponse.isSuccess()) {
                        showTransferSuccess(recipient, amount);
                    } else {
                        showError(transferResponse.getMessage() != null ? 
                                transferResponse.getMessage() : "Transfer failed");
                    }
                } else {
                    // 处理HTTP错误
                    try {
                        String errorBody = response.errorBody() != null ? 
                                response.errorBody().string() : "";
                        Log.e(TAG, "Transfer failed with code: " + response.code() + ", body: " + errorBody);
                        
                        // 处理特定的错误情况
                        if (response.code() == 400) {
                            showError("Invalid input");
                        } else if (response.code() == 404) {
                            showError("Recipient not found");
                        } else if (response.code() == 409) {
                            showError("Insufficient balance");
                        } else {
                            showError("Transfer failed");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error response", e);
                        showError("Transfer failed");
                    }
                }
            }

            @Override
            public void onFailure(Call<TransferResponse> call, Throwable t) {
                Log.e(TAG, "Transfer network error", t);
                showLoading(false);
                ErrorHandler.handleNetworkError(TransferActivity.this, t instanceof Exception ? (Exception) t : new Exception(t));
            }
        });
    }

    private void showTransferSuccess(String recipient, double amount) {
        // 隐藏表单和错误信息
        errorText.setVisibility(View.GONE);
        
        // 添加成功时的视觉反馈动画
        runSuccessAnimation();
        
        // 显示结果卡片 - 使用更现代的显示方式
        resultCard.setVisibility(View.VISIBLE);
        resultCard.setAlpha(0f);
        resultCard.setTranslationY(20f);
        resultCard.animate()
                .alpha(1f)
                .translationY(0)
                .setDuration(400)
                .setInterpolator(new FastOutSlowInInterpolator())
                .start();
        
        // 设置成功信息
        resultTitle.setText(getString(R.string.transfer_successful));
        resultTitle.setTextColor(ContextCompat.getColor(this, R.color.success));
        resultMessage.setText(getString(R.string.successfully_transferred, amount, recipient));
        
        // 禁用转账按钮
        confirmButton.setEnabled(false);
        confirmButton.setText(getString(R.string.transfer_completed));
        confirmButton.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_check_circle));
        confirmButton.setBackgroundColor(ContextCompat.getColor(this, R.color.primary_container));
        
        // 显示成功Snackbar
        showAnimatedSnackbar(getString(R.string.transfer_successful), 
                ContextCompat.getColor(this, R.color.success), 
                ContextCompat.getColor(this, R.color.on_success));
        
        // 延迟后自动返回
        new Handler().postDelayed(this::animatePageExit, 3000);
    }
    
    /**
     * 执行成功状态动画
     */
    private void runSuccessAnimation() {
        // 整个容器轻微放大再恢复，提供成功反馈
        containerView.animate()
            .scaleX(1.02f)
            .scaleY(1.02f)
            .setDuration(150)
            .setInterpolator(new FastOutSlowInInterpolator())
            .withEndAction(() -> containerView.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(150)
                .setInterpolator(new FastOutSlowInInterpolator())
                .start())
            .start();
            
        // 添加表单卡片淡出动画
        // 暂时注释掉对transfer_form_card的引用，因为在编译时找不到该id
        // View transferFormCard = findViewById(R.id.transfer_form_card);
        // if (transferFormCard != null) {
        //     transferFormCard.animate()
        //         .alpha(0.7f)
        //         .setDuration(300)
        //         .start();
        // } else {
        //     Log.w(TAG, "Transfer form card not found");
        // }
    }

    private void showError(String message) {
        // 改进错误状态显示，使其更符合Material设计
        showAnimatedError(message);
        
        // 使用更现代和流畅的错误反馈动画
        runErrorAnimation();
        
        // 显示错误Snackbar
        showAnimatedSnackbar(message, 
                ContextCompat.getColor(this, R.color.error), 
                ContextCompat.getColor(this, R.color.on_error));
        
        // 自动聚焦到第一个有问题的输入框
        focusFirstInvalidField();
    }
    
    /**
     * 执行错误状态动画
     */
    private void runErrorAnimation() {
        // 更流畅的错误抖动动画 - 使用更少的步骤实现更好的效果
        containerView.animate()
            .translationX(-8f)
            .setDuration(60)
            .withEndAction(() -> containerView.animate()
                .translationX(8f)
                .setDuration(60)
                .withEndAction(() -> containerView.animate()
                    .translationX(-4f)
                    .setDuration(60)
                    .withEndAction(() -> containerView.animate()
                        .translationX(4f)
                        .setDuration(60)
                        .withEndAction(() -> containerView.animate()
                            .translationX(0f)
                            .setDuration(60)
                            .start())
                        .start())
                    .start())
                .start())
            .start();
    }
    
    /**
     * 聚焦到第一个无效的输入字段
     */
    private void focusFirstInvalidField() {
        if (recipientInput.getText().toString().trim().isEmpty()) {
            recipientInput.requestFocus();
        } else if (amountInput.getText().toString().trim().isEmpty()) {
            amountInput.requestFocus();
        }
    }
    
    /**
     * 设置输入法导航
     */
    private void setupImeNavigation() {
        recipientInput.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        amountInput.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        memoInput.setImeOptions(EditorInfo.IME_ACTION_DONE);
        
        recipientInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                amountInput.requestFocus();
                return true;
            }
            return false;
        });
        
        amountInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                memoInput.requestFocus();
                return true;
            }
            return false;
        });
        
        memoInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                handleTransfer();
                return true;
            }
            return false;
        });
    }
    
    /**
     * 显示带动画效果的Snackbar - 增强版本
     */
    private void showAnimatedSnackbar(String message, int backgroundColor, int textColor) {
        // 使用更长的显示时间，确保用户有足够时间阅读
        int duration = message.length() > 30 ? Snackbar.LENGTH_LONG : Snackbar.LENGTH_SHORT;
        Snackbar snackbar = Snackbar.make(containerView, message, duration);
        
        // 设置自定义样式
        View snackbarView = snackbar.getView();
        // 使用兼容的方式设置背景色调
        ViewCompat.setBackgroundTintList(snackbarView, ContextCompat.getColorStateList(this, backgroundColor));
        snackbarView.setElevation(getResources().getDimension(R.dimen.snackbar_elevation));
        
        // 设置文本样式
        TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(textColor);
        textView.setTextSize(14);
        
        // 初始化动画状态
        snackbarView.setAlpha(0f);
        snackbarView.setTranslationY(100f);
        
        // 添加自定义动画回调
        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onShown(Snackbar sb) {
                super.onShown(sb);
                // 进入动画 - 使用更平滑的插值器
                snackbarView.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(400)
                    .setInterpolator(new FastOutSlowInInterpolator())
                    .start();
            }
            
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                // 只有非手动关闭时才执行退出动画
                if (event != DISMISS_EVENT_ACTION) {
                    snackbarView.animate()
                        .alpha(0f)
                        .translationY(100f)
                        .setDuration(250)
                        .setInterpolator(new FastOutSlowInInterpolator())
                        .start();
                }
            }
        });
        
        snackbar.show();
    }
    
    // 重载方法，保持向后兼容
    private void showAnimatedSnackbar(String message, int backgroundColor) {
        showAnimatedSnackbar(message, backgroundColor, ContextCompat.getColor(this, R.color.on_error));
    }
    
    /**
     * 重置所有错误状态
     */
    private void resetErrorStates() {
        errorText.setVisibility(View.GONE);
        recipientLayout.setError(null);
        amountLayout.setError(null);
        memoLayout.setError(null);
    }
    
    private void hideError() {
        errorText.setVisibility(View.GONE);
    }

    private void showLoading(boolean loading) {
        if (loading) {
            // 平滑过渡到加载状态 - 使用Material推荐的加载状态切换
            confirmButton.animate()
                .alpha(0f)
                .setDuration(250)
                .withEndAction(() -> {
                    confirmButton.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setAlpha(0f);
                    
                    // 加载指示器淡入动画
                    progressBar.animate()
                        .alpha(1f)
                        .setDuration(250)
                        .start();
                })
                .start();
                
            hideError();
            
        } else {
            // 平滑过渡到正常状态
            progressBar.animate()
                .alpha(0f)
                .setDuration(250)
                .withEndAction(() -> {
                    progressBar.setVisibility(View.GONE);
                    confirmButton.setVisibility(View.VISIBLE);
                    confirmButton.setAlpha(0f);
                    
                    // 按钮淡入动画
                    confirmButton.animate()
                        .alpha(1f)
                        .setDuration(250)
                        .start();
                })
                .start();
        }
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        animatePageExit();
        return true;
    }
}