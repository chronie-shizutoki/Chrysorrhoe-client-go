package com.chronie.chrysorrhoego.ui.theme;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import java.util.ArrayList;
import java.util.List;

/**
 * 主题管理器，单例模式，负责管理应用的主题状态并通知观察者
 */
public class ThemeManager {
    private static ThemeManager sInstance;
    private List<ThemeObserver> mObservers;
    private boolean mIsDarkMode;
    private Context mContext;

    // 私有构造函数
    private ThemeManager() {
        mObservers = new ArrayList<>();
        mIsDarkMode = false;
    }

    /**
     * 获取主题管理器的单例实例
     */
    public static synchronized ThemeManager getInstance() {
        if (sInstance == null) {
            sInstance = new ThemeManager();
        }
        return sInstance;
    }

    /**
     * 初始化主题管理器
     */
    public void init(Context context) {
        mContext = context;
        // 从SharedPreferences加载主题设置
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        mIsDarkMode = prefs.getBoolean("theme_dark_mode", false);
    }

    /**
     * 注册主题观察者
     */
    public void registerObserver(ThemeObserver observer) {
        if (!mObservers.contains(observer)) {
            mObservers.add(observer);
        }
    }

    /**
     * 注销主题观察者
     */
    public void unregisterObserver(ThemeObserver observer) {
        mObservers.remove(observer);
    }

    /**
     * 设置暗黑模式
     */
    public void setDarkMode(boolean isDarkMode) {
        if (mIsDarkMode != isDarkMode) {
            mIsDarkMode = isDarkMode;
            // 保存设置
            if (mContext != null) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
                prefs.edit().putBoolean("theme_dark_mode", isDarkMode).apply();
            }
            // 通知所有观察者
            notifyThemeChanged();
        }
    }

    /**
     * 检查是否为暗黑模式
     */
    public boolean isDarkMode() {
        return mIsDarkMode;
    }

    /**
     * 通知所有观察者主题已更改
     */
    private void notifyThemeChanged() {
        for (ThemeObserver observer : mObservers) {
            observer.onThemeChanged();
        }
    }
}