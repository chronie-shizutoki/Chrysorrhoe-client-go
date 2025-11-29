package com.chronie.chrysorrhoego.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * SharedPreferencesHelper 类用于管理应用的本地存储和用户偏好设置
 */
public class SharedPreferencesHelper {

    private static final String TAG = "SharedPreferencesHelper";
    private static SharedPreferencesHelper instance;
    private final SharedPreferences sharedPreferences;
    
    // 存储键名常量
    private static final String KEY_BIOMETRIC_ENABLED = "biometric_enabled";
    private static final String KEY_LAST_LOGIN_TIME = "last_login_time";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_SESSION_TOKEN = "session_token";
    private static final String KEY_THEME_MODE = "theme_mode";
    private static final String KEY_APP_LOCKED = "app_locked";
    
    private SharedPreferencesHelper(Context context) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }
    
    /**
     * 获取单例实例
     * @param context 上下文
     * @return SharedPreferencesHelper实例
     */
    public static synchronized SharedPreferencesHelper getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferencesHelper(context);
        }
        return instance;
    }
    
    /**
     * 设置是否启用生物识别
     * @param enabled 是否启用
     */
    public void setBiometricEnabled(boolean enabled) {
        sharedPreferences.edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply();
    }
    
    /**
     * 获取生物识别是否启用
     * @return 是否启用
     */
    public boolean isBiometricEnabled() {
        return sharedPreferences.getBoolean(KEY_BIOMETRIC_ENABLED, false);
    }
    
    /**
     * 保存最后登录时间
     * @param timestamp 时间戳
     */
    public void saveLastLoginTime(long timestamp) {
        sharedPreferences.edit().putLong(KEY_LAST_LOGIN_TIME, timestamp).apply();
    }
    
    /**
     * 获取最后登录时间
     * @return 时间戳
     */
    public long getLastLoginTime() {
        return sharedPreferences.getLong(KEY_LAST_LOGIN_TIME, 0);
    }
    
    /**
     * 保存用户名
     * @param username 用户名
     */
    public void saveUsername(String username) {
        sharedPreferences.edit().putString(KEY_USERNAME, username).apply();
    }
    
    /**
     * 获取用户名
     * @return 用户名
     */
    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, "");
    }
    
    /**
     * 保存会话令牌
     * @param token 会话令牌
     */
    public void saveSessionToken(String token) {
        sharedPreferences.edit().putString(KEY_SESSION_TOKEN, token).apply();
    }
    
    /**
     * 获取会话令牌
     * @return 会话令牌
     */
    public String getSessionToken() {
        return sharedPreferences.getString(KEY_SESSION_TOKEN, "");
    }
    
    /**
     * 设置主题模式
     * @param themeMode 主题模式
     */
    public void setThemeMode(String themeMode) {
        sharedPreferences.edit().putString(KEY_THEME_MODE, themeMode).apply();
    }
    
    /**
     * 获取主题模式
     * @return 主题模式
     */
    public String getThemeMode() {
        return sharedPreferences.getString(KEY_THEME_MODE, "system");
    }
    
    /**
     * 设置应用锁定状态
     * @param locked 是否锁定
     */
    public void setAppLocked(boolean locked) {
        sharedPreferences.edit().putBoolean(KEY_APP_LOCKED, locked).apply();
    }
    
    /**
     * 获取应用锁定状态
     * @return 是否锁定
     */
    public boolean isAppLocked() {
        return sharedPreferences.getBoolean(KEY_APP_LOCKED, false);
    }
    
    /**
     * 清除所有用户数据（登出时使用）
     */
    public void clearUserData() {
        sharedPreferences.edit()
            .remove(KEY_SESSION_TOKEN)
            .remove(KEY_USERNAME)
            .remove(KEY_LAST_LOGIN_TIME)
            .remove(KEY_APP_LOCKED)
            .apply();
    }
    
    /**
     * 检查是否已登录
     * @return 是否已登录
     */
    public boolean isLoggedIn() {
        return !getSessionToken().isEmpty();
    }
}