package com.chronie.chrysorrhoego.util;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.RequiresApi;

import com.chronie.chrysorrhoego.data.prefs.SharedPreferencesHelper;

/**
 * 安全管理器，负责统一管理应用的安全策略
 * 包括会话超时、应用锁定、敏感操作验证等功能
 */
public class SecurityManager {

    private static final String TAG = "SecurityManager";
    private static final long DEFAULT_SESSION_TIMEOUT_MS = 300000; // 5分钟
    
    private static SecurityManager instance;
    private final Context context;
    private final SharedPreferencesHelper prefsHelper;
    private final Handler sessionHandler;
    private final Runnable sessionTimeoutRunnable;
    private boolean isAppLocked = false;
    
    private SecurityManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefsHelper = SharedPreferencesHelper.getInstance(context);
        this.sessionHandler = new Handler(Looper.getMainLooper());
        
        this.sessionTimeoutRunnable = () -> {
            lockApp();
        };
    }
    
    /**
     * 获取单例实例
     * @param context 上下文
     * @return SecurityManager实例
     */
    public static synchronized SecurityManager getInstance(Context context) {
        if (instance == null) {
            instance = new SecurityManager(context);
        }
        return instance;
    }
    
    /**
     * 重置会话计时器
     */
    public void resetSessionTimer() {
        cancelSessionTimeout();
        scheduleSessionTimeout();
    }
    
    /**
     * 安排会话超时
     */
    private void scheduleSessionTimeout() {
        long timeoutMs = getSessionTimeoutMs();
        sessionHandler.postDelayed(sessionTimeoutRunnable, timeoutMs);
    }
    
    /**
     * 取消会话超时
     */
    public void cancelSessionTimeout() {
        sessionHandler.removeCallbacks(sessionTimeoutRunnable);
    }
    
    /**
     * 锁定应用
     */
    public void lockApp() {
        isAppLocked = true;
        cancelSessionTimeout();
    }
    
    /**
     * 解锁应用
     */
    public void unlockApp() {
        isAppLocked = false;
        resetSessionTimer();
    }
    
    /**
     * 检查应用是否被锁定
     * @return 是否锁定
     */
    public boolean isAppLocked() {
        return isAppLocked;
    }
    
    /**
     * 获取会话超时时间（毫秒）
     * @return 超时时间
     */
    private long getSessionTimeoutMs() {
        // 可以从偏好设置中获取用户自定义的超时时间
        // 如果未设置，则返回默认值
        return DEFAULT_SESSION_TIMEOUT_MS;
    }
    
    /**
     * 验证敏感操作是否需要额外验证
     * @param operationType 操作类型
     * @return 是否需要验证
     */
    public boolean requiresAdditionalVerification(String operationType) {
        // 根据操作类型和用户设置判断是否需要额外验证
        // 例如，转账、修改密码等操作需要生物识别验证
        return isSensitiveOperation(operationType) && isBiometricEnabled();
    }
    
    /**
     * 检查是否为敏感操作
     * @param operationType 操作类型
     * @return 是否为敏感操作
     */
    private boolean isSensitiveOperation(String operationType) {
        switch (operationType) {
            case OperationType.TRANSFER:
            case OperationType.MODIFY_PASSWORD:
            case OperationType.VIEW_SECRET_KEY:
                return true;
            default:
                return false;
        }
    }
    
    /**
     * 检查是否启用了生物识别
     * @return 是否启用
     */
    public boolean isBiometricEnabled() {
        return prefsHelper.isBiometricEnabled() && 
               Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
               BiometricUtil.isBiometricSupported(context);
    }
    
    /**
     * 启用/禁用生物识别
     * @param enabled 是否启用
     */
    public void setBiometricEnabled(boolean enabled) {
        prefsHelper.setBiometricEnabled(enabled);
    }
    
    /**
     * 清除安全相关的所有状态
     */
    public void clearSecurityState() {
        cancelSessionTimeout();
        isAppLocked = false;
    }
    
    /**
     * 操作类型常量类
     */
    public static class OperationType {
        public static final String TRANSFER = "transfer";
        public static final String MODIFY_PASSWORD = "modify_password";
        public static final String VIEW_SECRET_KEY = "view_secret_key";
    }
}