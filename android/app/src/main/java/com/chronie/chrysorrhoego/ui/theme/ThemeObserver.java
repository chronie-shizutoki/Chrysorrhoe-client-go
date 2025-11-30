package com.chronie.chrysorrhoego.ui.theme;

/**
 * 主题观察者接口，用于监听主题变化
 */
public interface ThemeObserver {
    /**
     * 当主题变化时调用
     * @param isDarkMode 当前是否为暗黑模式
     */
    void onThemeChanged(boolean isDarkMode);
}