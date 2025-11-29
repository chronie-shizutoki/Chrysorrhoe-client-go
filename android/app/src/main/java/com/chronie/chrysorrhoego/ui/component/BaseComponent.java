package com.chronie.chrysorrhoego.ui.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.chronie.chrysorrhoego.R;

/**
 * Base component class that provides common functionality for all custom UI components
 */
public abstract class BaseComponent extends View {

    // Common attributes
    protected int mBackgroundColor;
    protected int mTextColor;
    protected float mCornerRadius;
    protected boolean mIsDarkMode;

    public BaseComponent(Context context) {
        super(context);
        init(null);
    }

    public BaseComponent(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public BaseComponent(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        // Check if dark mode is enabled
        int nightModeFlags = getContext().getResources().getConfiguration().uiMode & 
                android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        mIsDarkMode = nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES;

        // Default values
        mBackgroundColor = ContextCompat.getColor(getContext(), mIsDarkMode ? 
                R.color.color_surface : R.color.color_surface);
        mTextColor = ContextCompat.getColor(getContext(), mIsDarkMode ? 
                R.color.color_text : R.color.color_text);
        mCornerRadius = getResources().getDimension(R.dimen.radius_md);

        // Load custom attributes if available
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.BaseComponent);
            
            // Load custom attributes for this component
            loadAttributes(a);
            
            a.recycle();
        }

        // Apply styling
        applyStyling();
    }

    /**
     * Load component-specific attributes
     */
    protected abstract void loadAttributes(TypedArray a);

    /**
     * Apply styling to the component
     */
    protected abstract void applyStyling();

    /**
     * Update component styling when theme changes
     */
    public void updateForTheme() {
        mIsDarkMode = (getContext().getResources().getConfiguration().uiMode & 
                android.content.res.Configuration.UI_MODE_NIGHT_MASK) == 
                android.content.res.Configuration.UI_MODE_NIGHT_YES;
        applyStyling();
    }
}
