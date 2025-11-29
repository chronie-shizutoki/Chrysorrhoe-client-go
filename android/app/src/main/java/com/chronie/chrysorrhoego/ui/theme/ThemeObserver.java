package com.chronie.chrysorrhoego.ui.theme;

/**
 * 主题观察者接口，用于监听主题变化
 */
public interface ThemeObserver {
    /**
     * 当主题发生变化时调用
     */
    void onThemeChanged();
}