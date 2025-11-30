package com.chronie.chrysorrhoego.ui.theme;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 主题管理器，采用单例模式，负责管理应用的主题状态并通知观察者
 * 提供系统主题同步和手动主题切换功能
 */
public class ThemeManager {
    private static final String TAG = "ThemeManager";
    private static final String PREF_THEME_MODE = "theme_mode";
    private static final String PREF_FOLLOW_SYSTEM = "theme_follow_system";
    
    private static volatile ThemeManager sInstance;
    private final List<WeakReference<ThemeObserver>> mObservers;
    private WeakReference<Context> mContextRef;
    private ThemeMode mThemeMode;
    private boolean mFollowSystem;

    /**
     * 主题模式枚举
     */
    public enum ThemeMode {
        LIGHT,    // 亮色主题
        DARK,     // 暗色主题
        AUTO      // 跟随系统
    }

    // 私有构造函数
    private ThemeManager() {
        mObservers = new ArrayList<>();
        mThemeMode = ThemeMode.LIGHT;
        mFollowSystem = true;
    }

    /**
     * 获取主题管理器的单例实例
     */
    @NonNull
    public static ThemeManager getInstance() {
        if (sInstance == null) {
            synchronized (ThemeManager.class) {
                if (sInstance == null) {
                    sInstance = new ThemeManager();
                }
            }
        }
        return sInstance;
    }

    /**
     * 初始化主题管理器
     * @param context 应用上下文
     */
    public void init(@NonNull Context context) {
        mContextRef = new WeakReference<>(context.getApplicationContext());
        loadThemePreferences();
        applyTheme();
    }

    /**
     * 从SharedPreferences加载主题设置
     */
    private void loadThemePreferences() {
        Context context = getContext();
        if (context == null) return;

        SharedPreferences prefs = getSharedPreferences(context);
        mFollowSystem = prefs.getBoolean(PREF_FOLLOW_SYSTEM, true);
        
        // 从存储的整数值转换为ThemeMode枚举
        int modeValue = prefs.getInt(PREF_THEME_MODE, ThemeMode.AUTO.ordinal());
        mThemeMode = ThemeMode.values()[modeValue];
    }

    /**
     * 保存主题设置到SharedPreferences
     */
    private void saveThemePreferences() {
        Context context = getContext();
        if (context == null) return;

        SharedPreferences prefs = getSharedPreferences(context);
        prefs.edit()
                .putBoolean(PREF_FOLLOW_SYSTEM, mFollowSystem)
                .putInt(PREF_THEME_MODE, mThemeMode.ordinal())
                .apply();
    }

    /**
     * 获取SharedPreferences实例
     */
    @NonNull
    private SharedPreferences getSharedPreferences(@NonNull Context context) {
        return context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE);
    }

    /**
     * 安全获取上下文，避免内存泄漏
     */
    private Context getContext() {
        return mContextRef != null ? mContextRef.get() : null;
    }

    /**
     * 应用当前主题设置
     */
    public void applyTheme() {
        int nightMode;
        
        if (mFollowSystem || mThemeMode == ThemeMode.AUTO) {
            nightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        } else {
            nightMode = mThemeMode == ThemeMode.DARK 
                    ? AppCompatDelegate.MODE_NIGHT_YES 
                    : AppCompatDelegate.MODE_NIGHT_NO;
        }
        
        AppCompatDelegate.setDefaultNightMode(nightMode);
        notifyThemeChanged();
    }

    /**
     * 设置主题模式
     * @param mode 主题模式
     */
    public void setThemeMode(@NonNull ThemeMode mode) {
        if (mThemeMode != mode) {
            mThemeMode = mode;
            mFollowSystem = (mode == ThemeMode.AUTO);
            saveThemePreferences();
            applyTheme();
        }
    }

    /**
     * 设置是否跟随系统主题
     * @param followSystem 是否跟随系统
     */
    public void setFollowSystem(boolean followSystem) {
        if (mFollowSystem != followSystem) {
            mFollowSystem = followSystem;
            if (followSystem) {
                mThemeMode = ThemeMode.AUTO;
            }
            saveThemePreferences();
            applyTheme();
        }
    }

    /**
     * 获取当前主题模式
     */
    @NonNull
    public ThemeMode getThemeMode() {
        return mThemeMode;
    }

    /**
     * 检查当前是否为暗黑模式
     * @return 是否为暗黑模式
     */
    public boolean isDarkModeActive() {
        Context context = getContext();
        if (context == null) return false;

        int currentNightMode = context.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES;
    }
    
    /**
     * 检查当前是否为暗黑模式（兼容方法）
     * @return 是否为暗黑模式
     */
    public boolean isDarkMode() {
        return isDarkModeActive();
    }

    /**
     * 检查是否跟随系统主题
     */
    public boolean isFollowingSystem() {
        return mFollowSystem;
    }

    /**
     * 注册主题观察者，使用WeakReference避免内存泄漏
     */
    public void registerObserver(@NonNull ThemeObserver observer) {
        // 移除已被回收的观察者
        cleanupObservers();
        
        // 检查是否已存在该观察者
        for (WeakReference<ThemeObserver> ref : mObservers) {
            ThemeObserver existing = ref.get();
            if (existing == observer) {
                return; // 已存在，不需要重复添加
            }
        }
        
        mObservers.add(new WeakReference<>(observer));
    }

    /**
     * 注销主题观察者
     */
    public void unregisterObserver(@NonNull ThemeObserver observer) {
        for (int i = mObservers.size() - 1; i >= 0; i--) {
            ThemeObserver existing = mObservers.get(i).get();
            if (existing == null || existing == observer) {
                mObservers.remove(i);
            }
        }
    }

    /**
     * 清理被回收的观察者引用
     */
    private void cleanupObservers() {
        for (int i = mObservers.size() - 1; i >= 0; i--) {
            if (mObservers.get(i).get() == null) {
                mObservers.remove(i);
            }
        }
    }

    /**
     * 通知所有观察者主题已更改
     */
    private void notifyThemeChanged() {
        cleanupObservers();
        boolean isDarkMode = isDarkModeActive();
        
        for (WeakReference<ThemeObserver> ref : mObservers) {
            ThemeObserver observer = ref.get();
            if (observer != null) {
                observer.onThemeChanged(isDarkMode);
            }
        }
    }
    
    /**
     * 刷新主题状态
     * 当系统主题变化时调用此方法来更新状态并通知观察者
     */
    public void refreshThemeState() {
        notifyThemeChanged();
    }
}