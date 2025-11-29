package com.chronie.chrysorrhoego.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.chronie.chrysorrhoego.R;
import com.chronie.chrysorrhoego.util.LanguageManager;
import com.chronie.chrysorrhoego.util.LanguageManager.SupportedLanguage;

/**
 * 设置活动，提供应用设置功能，包括语言选择等
 */
public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";
    private LanguageManager languageManager;
    private TextView currentLanguageText;
    private LinearLayout languageOptionsContainer;
    private Button saveLanguageButton;
    private String selectedLanguageCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 应用语言设置
        languageManager = LanguageManager.getInstance(this);
        applyLanguage();
        
        setContentView(R.layout.activity_settings);
        
        // 设置工具栏
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.settings_title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        // 初始化UI组件
        currentLanguageText = findViewById(R.id.current_language_text);
        languageOptionsContainer = findViewById(R.id.language_options_container);
        saveLanguageButton = findViewById(R.id.save_language_button);
        
        // 获取当前语言设置
        selectedLanguageCode = languageManager.getCurrentLanguageCode();
        updateCurrentLanguageDisplay();
        
        // 初始化语言选项
        initializeLanguageOptions();
        
        // 设置保存按钮点击事件
        saveLanguageButton.setOnClickListener(v -> saveLanguageSetting());
    }
    
    /**
     * 应用语言设置到当前活动
     */
    private void applyLanguage() {
        // 移除对不存在方法的调用
        // 语言设置将在应用重启时生效
    }
    
    /**
     * 更新当前语言显示
     */
    private void updateCurrentLanguageDisplay() {
        String displayText = languageManager.getLanguageSettingSummary();
        currentLanguageText.setText(displayText);
    }
    
    /**
     * 初始化语言选项
     */
    private void initializeLanguageOptions() {
        // 清除现有的选项
        languageOptionsContainer.removeAllViews();
        
        // 添加系统语言选项
        addLanguageOption("", getString(R.string.use_system_language));
        
        // 直接添加所有支持的语言选项
        addLanguageOption(SupportedLanguage.ENGLISH.getLanguageCode(), SupportedLanguage.ENGLISH.getDisplayName());
        addLanguageOption(SupportedLanguage.CHINESE_SIMPLIFIED.getLanguageCode(), SupportedLanguage.CHINESE_SIMPLIFIED.getDisplayName());
        addLanguageOption(SupportedLanguage.CHINESE_TRADITIONAL.getLanguageCode(), SupportedLanguage.CHINESE_TRADITIONAL.getDisplayName());
        addLanguageOption(SupportedLanguage.JAPANESE.getLanguageCode(), SupportedLanguage.JAPANESE.getDisplayName());
        addLanguageOption(SupportedLanguage.KOREAN.getLanguageCode(), SupportedLanguage.KOREAN.getDisplayName());
    }
    
    /**
     * 添加单个语言选项
     */
    private void addLanguageOption(String languageCode, String displayName) {
        LinearLayout languageOption = (LinearLayout) getLayoutInflater().inflate(
                R.layout.language_option_item, languageOptionsContainer, false);
        
        TextView languageText = languageOption.findViewById(R.id.language_option_text);
        View selectionIndicator = languageOption.findViewById(R.id.selection_indicator);
        
        languageText.setText(displayName);
        
        // 检查是否为当前选中的语言
        boolean isSelected = languageCode.equals(selectedLanguageCode);
        selectionIndicator.setVisibility(isSelected ? View.VISIBLE : View.INVISIBLE);
        
        // 设置点击事件
        languageOption.setOnClickListener(v -> {
            // 更新选中状态
            selectedLanguageCode = languageCode;
            
            // 刷新语言选项显示
            initializeLanguageOptions();
        });
        
        languageOptionsContainer.addView(languageOption);
    }
    
    /**
     * 保存语言设置
     */
    private void saveLanguageSetting() {
        try {
            if (selectedLanguageCode.isEmpty()) {
                // 重置为系统语言
                languageManager.resetToSystemLanguage();
            } else {
                // 设置选择的语言
                languageManager.saveLanguageSetting(selectedLanguageCode);
            }
            
            // 显示保存成功消息
            Toast.makeText(this, R.string.language_saved, Toast.LENGTH_SHORT).show();
            
            // 重新启动活动以应用新语言
            recreate();
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to save language setting", e);
            Toast.makeText(this, R.string.error_saving_language, Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    
    /**
     * 当活动恢复时，刷新语言设置
     */
    @Override
    protected void onResume() {
        super.onResume();
        // 刷新语言显示，以防在其他地方更改了语言设置
        selectedLanguageCode = languageManager.getCurrentLanguageCode();
        updateCurrentLanguageDisplay();
    }
}
