package com.chronie.chrysorrhoego.ui.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.chronie.chrysorrhoego.R;

/**
 * Custom button component with multiple styles matching web design
 */
public class CustomButton extends BaseComponent {

    public enum ButtonType {
        PRIMARY,
        SECONDARY,
        OUTLINE
    }

    private ButtonType mButtonType = ButtonType.PRIMARY;
    private String mButtonText = "Button";
    private Paint mButtonPaint;
    private TextPaint mTextPaint;
    private RectF mButtonRect;
    private boolean mIsPressed = false;
    private AttributeSet mAttributeSet; // Save AttributeSet for later use

    public CustomButton(Context context) {
        super(context);
    }

    public CustomButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mAttributeSet = attrs;
    }

    public CustomButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mAttributeSet = attrs;
    }

    @Override
    protected void loadAttributes(TypedArray a) {
        // Load button type
        int buttonTypeValue = a.getInt(R.styleable.CustomButton_buttonType, 0);
        switch (buttonTypeValue) {
            case 1:
                mButtonType = ButtonType.SECONDARY;
                break;
            case 2:
                mButtonType = ButtonType.OUTLINE;
                break;
            default:
                mButtonType = ButtonType.PRIMARY;
        }

        // First, try to get standard android:text attribute directly from saved AttributeSet
        String androidText = null;
        if (mAttributeSet != null) {
            androidText = mAttributeSet.getAttributeValue("http://schemas.android.com/apk/res/android", "text");
        }

        // Then try to get custom buttonText attribute
        mButtonText = a.getString(R.styleable.CustomButton_buttonText);
        
        // Prefer custom attribute if both are set
        if (mButtonText == null && androidText != null) {
            mButtonText = androidText;
        }
        
        // Default text if neither is set
        if (mButtonText == null) {
            mButtonText = "Button";
        }

        // Load custom colors if provided
        if (a.hasValue(R.styleable.CustomButton_buttonBackgroundColor)) {
            mBackgroundColor = a.getColor(R.styleable.CustomButton_buttonBackgroundColor, mBackgroundColor);
        }

        if (a.hasValue(R.styleable.CustomButton_buttonTextColor)) {
            mTextColor = a.getColor(R.styleable.CustomButton_buttonTextColor, mTextColor);
        }

        // Load corner radius
        if (a.hasValue(R.styleable.CustomButton_buttonCornerRadius)) {
            mCornerRadius = a.getDimension(R.styleable.CustomButton_buttonCornerRadius, mCornerRadius);
        }
    }

    @Override
    protected void applyStyling() {
        // Initialize paints
        mButtonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(getResources().getDimension(R.dimen.font_size_base));
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        // Set button colors based on type - let the system automatically handle dark/light theme colors
        switch (mButtonType) {
            case PRIMARY:
                mBackgroundColor = ContextCompat.getColor(getContext(), R.color.color_primary);
                mTextColor = ContextCompat.getColor(getContext(), R.color.color_white);
                break;
            case SECONDARY:
                mBackgroundColor = ContextCompat.getColor(getContext(), R.color.color_secondary);
                mTextColor = ContextCompat.getColor(getContext(), R.color.color_white);
                break;
            case OUTLINE:
                mBackgroundColor = ContextCompat.getColor(getContext(), android.R.color.transparent);
                mTextColor = ContextCompat.getColor(getContext(), R.color.color_primary);
                break;
        }

        mButtonPaint.setColor(mBackgroundColor);
        mTextPaint.setColor(mTextColor);

        // Set minimum height
        setMinimumHeight((int) getResources().getDimension(R.dimen.button_height));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mButtonRect = new RectF(0, 0, w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw button background
        if (mButtonType == ButtonType.OUTLINE) {
            // Draw outline only
            mButtonPaint.setStyle(Paint.Style.STROKE);
            mButtonPaint.setStrokeWidth(2);
            mButtonPaint.setColor(mTextColor);
        } else {
            // Draw filled button
            mButtonPaint.setStyle(Paint.Style.FILL);
            mButtonPaint.setColor(mIsPressed ? 
                    adjustColorBrightness(mBackgroundColor, -20) : mBackgroundColor);
        }

        // Draw rounded rectangle
        canvas.drawRoundRect(mButtonRect, mCornerRadius, mCornerRadius, mButtonPaint);

        // Draw text
        float textY = (mButtonRect.height() / 2) - 
                ((mTextPaint.descent() + mTextPaint.ascent()) / 2);
        canvas.drawText(mButtonText, mButtonRect.centerX(), textY, mTextPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIsPressed = true;
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsPressed = false;
                invalidate();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    performClick();
                }
                return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    /**
     * Adjust color brightness
     */
    private int adjustColorBrightness(int color, float factor) {
        float[] hsv = new float[3];
        android.graphics.Color.colorToHSV(color, hsv);
        hsv[2] = Math.max(0, Math.min(1, hsv[2] + factor / 100));
        return android.graphics.Color.HSVToColor(hsv);
    }

    /**
     * Public methods to update button properties
     */
    public void setButtonType(ButtonType type) {
        mButtonType = type;
        applyStyling();
        invalidate();
    }

    public void setButtonText(String text) {
        mButtonText = text;
        invalidate();
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        setAlpha(enabled ? 1.0f : 0.6f);
    }
}
