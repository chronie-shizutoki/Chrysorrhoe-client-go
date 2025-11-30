package com.chronie.chrysorrhoego.ui.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.chronie.chrysorrhoego.R;

/**
 * Custom card component with title and content area
 */
public class CustomCard extends FrameLayout {

    private String mCardTitle = "";
    private int mBackgroundColor;
    private int mBorderColor;
    private int mTextColor;
    private float mCornerRadius;
    private boolean mIsDarkMode;
    
    private TextView mTitleView;
    private FrameLayout mContentContainer;
    private Paint mBackgroundPaint;
    private Paint mBorderPaint;
    private RectF mCardRect;

    public CustomCard(Context context) {
        super(context);
        init(null);
    }

    public CustomCard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CustomCard(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        // Check if dark mode is enabled
        int nightModeFlags = getContext().getResources().getConfiguration().uiMode & 
                android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        mIsDarkMode = nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES;

        // Default values
        mBackgroundColor = ContextCompat.getColor(getContext(), R.color.color_surface);
        mBorderColor = ContextCompat.getColor(getContext(), R.color.color_border);
        mTextColor = ContextCompat.getColor(getContext(), R.color.color_text);
        mCornerRadius = getResources().getDimension(R.dimen.radius_lg);

        // Load custom attributes if available
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomCard);
            
            mCardTitle = a.getString(R.styleable.CustomCard_cardTitle) != null ? 
                    a.getString(R.styleable.CustomCard_cardTitle) : "";
            
            if (a.hasValue(R.styleable.CustomCard_cardBackgroundColor)) {
                mBackgroundColor = a.getColor(R.styleable.CustomCard_cardBackgroundColor, mBackgroundColor);
            }
            if (a.hasValue(R.styleable.CustomCard_cardBorderColor)) {
                mBorderColor = a.getColor(R.styleable.CustomCard_cardBorderColor, mBorderColor);
            }
            if (a.hasValue(R.styleable.CustomCard_cardCornerRadius)) {
                mCornerRadius = a.getDimension(R.styleable.CustomCard_cardCornerRadius, mCornerRadius);
            }
            
            a.recycle();
        }

        // Initialize paints
        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(1);
        
        // Create layout
        createLayout();
    }

    private void createLayout() {
        // Set padding for the card
        int padding = (int) getResources().getDimension(R.dimen.card_padding);
        setPadding(padding, padding, padding, padding);
        
        // Create title view if title is provided
        if (!mCardTitle.isEmpty()) {
            mTitleView = new TextView(getContext());
            LayoutParams titleParams = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            mTitleView.setLayoutParams(titleParams);
            mTitleView.setText(mCardTitle);
            mTitleView.setTextColor(mTextColor);
            mTitleView.setTextSize(18);
            mTitleView.setTypeface(null, android.graphics.Typeface.BOLD);
            
            // Add bottom margin to title
            titleParams.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen.space_md));
            
            addView(mTitleView);
        }
        
        // Create content container
        mContentContainer = new FrameLayout(getContext());
        LayoutParams contentParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mContentContainer.setLayoutParams(contentParams);
        // 使用setTag替代setId，避免对不存在资源的引用
        mContentContainer.setTag("card_content_container");
        
        addView(mContentContainer);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCardRect = new RectF(0, 0, w, h);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        // Draw card background
        mBackgroundPaint.setColor(mBackgroundColor);
        canvas.drawRoundRect(mCardRect, mCornerRadius, mCornerRadius, mBackgroundPaint);
        
        // Draw card border
        mBorderPaint.setColor(mBorderColor);
        canvas.drawRoundRect(mCardRect, mCornerRadius, mCornerRadius, mBorderPaint);
        
        super.dispatchDraw(canvas);
    }

    @Override
    public void addView(android.view.View child, int index, ViewGroup.LayoutParams params) {
        // If this is not the title or content container, add it to the content container
        if (child != mTitleView && child != mContentContainer && mContentContainer != null) {
            mContentContainer.addView(child, index, params);
        } else {
            super.addView(child, index, params);
        }
    }

    // Public methods
    public void setTitle(String title) {
        mCardTitle = title;
        if (mTitleView == null && !title.isEmpty()) {
            // Create title view if it doesn't exist and title is provided
            mTitleView = new TextView(getContext());
            LayoutParams titleParams = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            titleParams.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen.space_md));
            mTitleView.setLayoutParams(titleParams);
            mTitleView.setTextColor(mTextColor);
            mTitleView.setTextSize(18);
            mTitleView.setTypeface(null, android.graphics.Typeface.BOLD);
            
            // Add before content container
            removeView(mContentContainer);
            addView(mTitleView, 0);
            addView(mContentContainer, 1);
        }
        
        if (mTitleView != null) {
            mTitleView.setText(title);
            mTitleView.setVisibility(title.isEmpty() ? GONE : VISIBLE);
        }
    }

    public FrameLayout getContentContainer() {
        return mContentContainer;
    }

    public void updateForTheme() {
        mIsDarkMode = (getContext().getResources().getConfiguration().uiMode & 
                android.content.res.Configuration.UI_MODE_NIGHT_MASK) == 
                android.content.res.Configuration.UI_MODE_NIGHT_YES;
        
        // Update colors - 系统会根据当前主题自动选择正确的资源
        mBackgroundColor = ContextCompat.getColor(getContext(), R.color.color_surface);
        mBorderColor = ContextCompat.getColor(getContext(), R.color.color_border);
        mTextColor = ContextCompat.getColor(getContext(), R.color.color_text);
        
        // Update title text color
        if (mTitleView != null) {
            mTitleView.setTextColor(mTextColor);
        }
        
        invalidate();
    }
}
