package com.chronie.chrysorrhoego.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Locale;

/**
 * 语言管理类
 * 负责应用的国际化和语言切换功能
 */
public class LanguageManager {

    private static final String TAG = "LanguageManager";
    private static final String PREF_LANGUAGE_KEY = "app_language";
    private static final String PREF_LANGUAGE_DEFAULT = "";
    
    private static LanguageManager instance;
    private final Context context;
    private final SharedPreferences preferences;
    
    // 支持的语言列表
    public enum SupportedLanguage {
        ENGLISH("en", "English"),
        CHINESE_SIMPLIFIED("zh", "中文"),
        CHINESE_TRADITIONAL("zh-rTW", "繁體中文"),
        JAPANESE("ja", "日本語"),
        KOREAN("ko", "한국어");
        
        private final String languageCode;
        private final String displayName;
        
        SupportedLanguage(String languageCode, String displayName) {
            this.languageCode = languageCode;
            this.displayName = displayName;
        }
        
        public String getLanguageCode() {
            return languageCode;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        /**
         * 根据语言代码获取语言枚举
         */
        public static SupportedLanguage fromLanguageCode(String languageCode) {
            for (SupportedLanguage lang : values()) {
                if (lang.languageCode.equalsIgnoreCase(languageCode)) {
                    return lang;
                }
            }
            // 特殊处理中文
            if (languageCode != null) {
                if (languageCode.startsWith("zh")) {
                    if (languageCode.contains("TW") || languageCode.contains("tw")) {
                        return CHINESE_TRADITIONAL;
                    }
                    return CHINESE_SIMPLIFIED;
                }
            }
            return ENGLISH; // 默认返回英语
        }
    }
    
    /**
     * 私有构造函数
     */
    private LanguageManager(Context context) {
        this.context = context.getApplicationContext();
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }
    
    /**
     * 获取单例实例
     */
    public static synchronized LanguageManager getInstance(Context context) {
        if (instance == null) {
            instance = new LanguageManager(context);
        }
        return instance;
    }
    
    /**
     * 获取当前语言设置
     */
    public String getCurrentLanguageCode() {
        String languageCode = preferences.getString(PREF_LANGUAGE_KEY, PREF_LANGUAGE_DEFAULT);
        if (languageCode.isEmpty()) {
            // 如果没有设置，返回系统语言
            return getSystemLanguageCode();
        }
        return languageCode;
    }
    
    /**
     * 获取系统语言代码
     */
    private String getSystemLanguageCode() {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = Resources.getSystem().getConfiguration().getLocales().get(0);
        } else {
            locale = Resources.getSystem().getConfiguration().locale;
        }
        return locale.toString();
    }
    
    /**
     * 设置应用语言
     */
    public void setLanguage(String languageCode) {
        Log.d(TAG, "Setting language to: " + languageCode);
        preferences.edit().putString(PREF_LANGUAGE_KEY, languageCode).apply();
    }
    
    /**
     * 重置为系统语言
     */
    public void resetToSystemLanguage() {
        preferences.edit().putString(PREF_LANGUAGE_KEY, PREF_LANGUAGE_DEFAULT).apply();
    }
    
    /**
     * 获取当前语言的Locale对象
     */
    public Locale getCurrentLocale() {
        String languageCode = getCurrentLanguageCode();
        return createLocaleFromCode(languageCode);
    }
    
    /**
     * 根据语言代码创建Locale对象
     */
    private Locale createLocaleFromCode(String languageCode) {
        try {
            // 处理zh-rTW格式
            if (languageCode.contains("-r") || languageCode.contains("_")) {
                String[] parts;
                if (languageCode.contains("-r")) {
                    parts = languageCode.split("-r");
                } else {
                    parts = languageCode.split("_");
                }
                
                if (parts.length >= 2) {
                    return new Locale(parts[0], parts[1]);
                }
            }
            return new Locale(languageCode);
        } catch (Exception e) {
            Log.e(TAG, "Error creating locale: " + e.getMessage());
            return Locale.ENGLISH;
        }
    }
    
    /**
     * 更新上下文的语言配置
     */
    public Context updateResources(Context context) {
        String languageCode = getCurrentLanguageCode();
        Locale locale = createLocaleFromCode(languageCode);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Configuration configuration = new Configuration(context.getResources().getConfiguration());
            configuration.setLocale(locale);
            return context.createConfigurationContext(configuration);
        } else {
            // 对于旧版本Android，直接修改Configuration
            Resources resources = context.getResources();
            Configuration configuration = resources.getConfiguration();
            configuration.locale = locale;
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
            return context;
        }
    }
    
    /**
     * 检查是否是当前系统语言
     */
    public boolean isSystemLanguage() {
        String savedLanguage = preferences.getString(PREF_LANGUAGE_KEY, PREF_LANGUAGE_DEFAULT);
        return savedLanguage.isEmpty();
    }
    
    /**
     * 获取支持的语言列表
     */
    public SupportedLanguage[] getSupportedLanguages() {
        return SupportedLanguage.values();
    }
    
    /**
     * 获取当前支持的语言枚举
     */
    public SupportedLanguage getCurrentSupportedLanguage() {
        String languageCode = getCurrentLanguageCode();
        return SupportedLanguage.fromLanguageCode(languageCode);
    }
    
    /**
     * 检查是否支持指定语言
     */
    public boolean isLanguageSupported(String languageCode) {
        for (SupportedLanguage lang : SupportedLanguage.values()) {
            if (lang.getLanguageCode().equalsIgnoreCase(languageCode)) {
                return true;
            }
        }
        // 特殊处理中文
        if (languageCode != null && languageCode.startsWith("zh")) {
            return true;
        }
        return false;
    }
    
    /**
     * 获取语言显示名称
     */
    public String getDisplayLanguageName(String languageCode) {
        SupportedLanguage lang = SupportedLanguage.fromLanguageCode(languageCode);
        return lang.getDisplayName();
    }
    
    /**
     * 获取当前语言的显示名称
     */
    public String getCurrentDisplayLanguageName() {
        SupportedLanguage lang = getCurrentSupportedLanguage();
        return lang.getDisplayName();
    }
    
    /**
     * 保存语言设置
     */
    public void saveLanguageSetting(String languageCode) {
        Log.d(TAG, "Saving language setting: " + languageCode);
        preferences.edit().putString(PREF_LANGUAGE_KEY, languageCode).apply();
    }
    
    /**
     * 获取语言设置的显示文本（用于UI）
     */
    public String getLanguageDisplayText(String languageCode) {
        if (languageCode.isEmpty()) {
            return "System Default";
        }
        return getDisplayLanguageName(languageCode);
    }
    
    /**
     * 创建应用的基础上下文（在Application类中使用）
     */
    public Context createBaseContext(Context baseContext) {
        return updateResources(baseContext);
    }
    
    /**
     * 获取当前Locale的语言代码（用于资源文件）
     */
    public String getResourceLanguageCode() {
        String languageCode = getCurrentLanguageCode();
        // 处理中文简体和繁体的特殊情况
        if (languageCode.contains("TW") || languageCode.contains("tw")) {
            return "zh-rTW";
        }
        if (languageCode.startsWith("zh")) {
            return "zh";
        }
        return languageCode.split("-|")[0]; // 返回语言代码的第一部分
    }
    
    /**
     * 验证语言代码是否有效
     */
    public boolean isValidLanguageCode(String languageCode) {
        try {
            createLocaleFromCode(languageCode);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 获取语言设置的摘要信息
     */
    public String getLanguageSettingSummary() {
        String languageCode = getCurrentLanguageCode();
        if (languageCode.isEmpty()) {
            return "Using system language: " + getDisplayLanguageName(getSystemLanguageCode());
        }
        return "Current language: " + getDisplayLanguageName(languageCode);
    }
}
