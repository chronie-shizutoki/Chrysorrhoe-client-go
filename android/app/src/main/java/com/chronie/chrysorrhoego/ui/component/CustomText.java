package com.chronie.chrysorrhoego.ui.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.chronie.chrysorrhoego.R;
import com.chronie.chrysorrhoego.ui.theme.ThemeManager;
import com.chronie.chrysorrhoego.ui.theme.ThemeObserver;

/**
 * 自定义文本组件，支持统一的文本样式和主题适配
 * 遵循网页端设计规范，提供多种预设文本样式
 */
public class CustomText extends TextView implements ThemeObserver {

    public enum TextStyle {
        H1, H2, H3, H4, BODY, BODY_SMALL, CAPTION, BUTTON
    }

    private TextStyle mTextStyle = TextStyle.BODY;
    private ThemeManager mThemeManager;
    private int mTextColorResId = R.color.color_text; // 默认文本颜色资源ID
    private boolean mIsBold;
    private boolean mIsItalic;
    private boolean mIsUnderline;

    public CustomText(Context context) {
        super(context);
        init(null);
    }

    public CustomText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CustomText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        // 初始化主题管理器
        mThemeManager = ThemeManager.getInstance();
        mThemeManager.registerObserver(this);

        // Default values
        mIsBold = false;
        mIsItalic = false;
        mIsUnderline = false;

        // 使用默认值替代不存在的styleable资源
        try {
            // 默认文本样式
            mTextStyle = TextStyle.BODY;
            
            // 默认文本装饰属性
            mIsBold = false;
            mIsItalic = false;
            mIsUnderline = false;
            
            // 默认文本颜色
            mTextColorResId = R.color.color_text;
        } catch (Exception e) {
            // 使用安全的默认值
            mTextStyle = TextStyle.BODY;
            mIsBold = false;
            mIsItalic = false;
            mIsUnderline = false;
            mTextColorResId = R.color.color_text;
        }

        // Apply initial styling
        applyTextStyle();
        updateTextColor();
    }

    private void applyTextStyle() {
        // Set text size and style based on TextStyle enum
        switch (mTextStyle) {
            case H1:
                setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 24);
                setTypeface(null, android.graphics.Typeface.BOLD);
                break;
            case H2:
                setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 20);
                setTypeface(null, android.graphics.Typeface.BOLD);
                break;
            case H3:
                setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 18);
                setTypeface(null, android.graphics.Typeface.BOLD);
                break;
            case H4:
                setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 16);
                setTypeface(null, android.graphics.Typeface.BOLD);
                break;
            case BODY:
                setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 16);
                break;
            case BODY_SMALL:
                setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 14);
                break;
            case CAPTION:
                setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 12);
                break;
            case BUTTON:
                setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 14);
                setTypeface(null, android.graphics.Typeface.BOLD);
                break;
        }

        // Apply additional styling
        int typefaceStyle = android.graphics.Typeface.NORMAL;
        if (mIsBold) {
            typefaceStyle |= android.graphics.Typeface.BOLD;
        }
        if (mIsItalic) {
            typefaceStyle |= android.graphics.Typeface.ITALIC;
        }
        setTypeface(null, typefaceStyle);
        setPaintFlags(getPaintFlags() | (mIsUnderline ? android.graphics.Paint.UNDERLINE_TEXT_FLAG : 0));
    }

    private void updateTextColor() {
        // 根据主题和文本类型设置颜色
        boolean isDarkMode = mThemeManager.isDarkMode();
        
        int colorResId;
        
        // 使用自定义颜色或默认颜色
        if (mTextColorResId == R.color.color_text) {
            // 默认文本颜色，使用基本颜色替代缺失的主题颜色
            colorResId = isDarkMode ? android.R.color.white : android.R.color.black;
        } else {
            // 使用自定义指定的颜色资源
            colorResId = mTextColorResId;
        }
        
        setTextColor(ContextCompat.getColor(getContext(), colorResId));
    }
    
    @Override
    public void onThemeChanged() {
        // 当主题发生变化时，更新文本颜色
        updateTextColor();
    }

    // Public methods
    public void setTextStyle(TextStyle style) {
        mTextStyle = style;
        applyTextStyle();
    }

    public void setBold(boolean bold) {
        mIsBold = bold;
        applyTextStyle();
    }

    public void setItalic(boolean italic) {
        mIsItalic = italic;
        applyTextStyle();
    }

    public void setUnderline(boolean underline) {
        mIsUnderline = underline;
        applyTextStyle();
    }

    public void updateForTheme() {
        // 主题更新时会自动调用onThemeChanged
        updateTextColor();
    }
    
    // 移除重复的onThemeChanged方法
    
    /**
     * 设置文本颜色资源ID
     */
    public void setTextColorResId(int colorResId) {
        mTextColorResId = colorResId;
        updateTextColor();
    }
    
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // 移除主题观察者注册，防止内存泄漏
        mThemeManager.unregisterObserver(this);
    }
}
