package com.chronie.chrysorrhoego.ui.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.Editable;
import android.text.InputType;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.chronie.chrysorrhoego.R;

/**
 * Custom input component with label, error state, and validation
 */
public class CustomInput extends LinearLayout {

    private String mLabel = "";
    private String mHint = "";
    private String mError = "";
    private boolean mIsRequired = false;
    private int mBackgroundColor;
    private int mTextColor;
    private int mBorderColor;
    private int mErrorColor;
    private float mCornerRadius;
    
    private TextView mLabelView;
    private EditText mInputView;
    private TextView mErrorView;
    private boolean mIsDarkMode;

    public CustomInput(Context context) {
        super(context);
        init(null);
    }

    public CustomInput(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CustomInput(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        mBorderColor = ContextCompat.getColor(getContext(), mIsDarkMode ? 
                R.color.color_border : R.color.color_border);
        mErrorColor = ContextCompat.getColor(getContext(), mIsDarkMode ? 
                R.color.color_danger : R.color.color_danger);
        mCornerRadius = getResources().getDimension(R.dimen.radius_md);

        // Load custom attributes if available
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomInput);
            
            mLabel = a.getString(R.styleable.CustomInput_inputLabel) != null ? 
                    a.getString(R.styleable.CustomInput_inputLabel) : "";
            mHint = a.getString(R.styleable.CustomInput_inputHint) != null ? 
                    a.getString(R.styleable.CustomInput_inputHint) : "";
            mError = a.getString(R.styleable.CustomInput_inputError) != null ? 
                    a.getString(R.styleable.CustomInput_inputError) : "";
            mIsRequired = a.getBoolean(R.styleable.CustomInput_isRequired, false);
            
            if (a.hasValue(R.styleable.CustomInput_inputBackgroundColor)) {
                mBackgroundColor = a.getColor(R.styleable.CustomInput_inputBackgroundColor, mBackgroundColor);
            }
            if (a.hasValue(R.styleable.CustomInput_inputTextColor)) {
                mTextColor = a.getColor(R.styleable.CustomInput_inputTextColor, mTextColor);
            }
            if (a.hasValue(R.styleable.CustomInput_inputBorderColor)) {
                mBorderColor = a.getColor(R.styleable.CustomInput_inputBorderColor, mBorderColor);
            }
            if (a.hasValue(R.styleable.CustomInput_inputCornerRadius)) {
                mCornerRadius = a.getDimension(R.styleable.CustomInput_inputCornerRadius, mCornerRadius);
            }
            
            a.recycle();
        }

        // Create UI components
        createComponents();
    }

    private void createComponents() {
        // Set orientation to vertical
        setOrientation(VERTICAL);
        
        // Create label
        if (!mLabel.isEmpty()) {
            mLabelView = new TextView(getContext());
            LayoutParams labelParams = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            labelParams.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen.space_sm));
            mLabelView.setLayoutParams(labelParams);
            mLabelView.setTextColor(mTextColor);
            mLabelView.setTextSize(14);
            mLabelView.setText(mLabel + (mIsRequired ? " *" : ""));
            addView(mLabelView);
        }
        
        // Create input field
        mInputView = new EditText(getContext());
        LayoutParams inputParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (int) getResources().getDimension(R.dimen.input_height));
        mInputView.setLayoutParams(inputParams);
        mInputView.setHint(mHint);
        mInputView.setTextColor(mTextColor);
        mInputView.setHintTextColor(ContextCompat.getColor(getContext(), mIsDarkMode ? 
                R.color.color_text_subtle : R.color.color_text_subtle));
        mInputView.setInputType(InputType.TYPE_CLASS_TEXT);
        mInputView.setPadding(
                (int) getResources().getDimension(R.dimen.space_md),
                0,
                (int) getResources().getDimension(R.dimen.space_md),
                0);
        
        // Apply custom styling
        mInputView.setBackground(null); // Remove default background
        mInputView.setTag("customInput");
        
        // Add text change listener to clear error when user types
        mInputView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!mError.isEmpty()) {
                    setError("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        
        addView(mInputView);
        
        // Create error view
        mErrorView = new TextView(getContext());
        LayoutParams errorParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        errorParams.setMargins(0, (int) getResources().getDimension(R.dimen.space_xs), 0, 0);
        mErrorView.setLayoutParams(errorParams);
        mErrorView.setTextColor(mErrorColor);
        mErrorView.setTextSize(12);
        mErrorView.setText(mError);
        mErrorView.setVisibility(mError.isEmpty() ? GONE : VISIBLE);
        addView(mErrorView);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        // Draw the custom background for the input
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        
        if (mInputView != null && mInputView.getTag() == "customInput") {
            RectF inputRect = new RectF(
                    0,
                    mLabelView != null ? mLabelView.getHeight() + getResources().getDimension(R.dimen.space_sm) : 0,
                    getWidth(),
                    mLabelView != null ? 
                            mLabelView.getHeight() + getResources().getDimension(R.dimen.space_sm) + getResources().getDimension(R.dimen.input_height) :
                            getResources().getDimension(R.dimen.input_height));
            
            // Draw background
            paint.setColor(mBackgroundColor);
            canvas.drawRoundRect(inputRect, mCornerRadius, mCornerRadius, paint);
            
            // Draw border
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1);
            paint.setColor(mError.isEmpty() ? mBorderColor : mErrorColor);
            canvas.drawRoundRect(inputRect, mCornerRadius, mCornerRadius, paint);
        }
        
        super.dispatchDraw(canvas);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // Focus on input when touched
        if (mInputView != null && ev.getAction() == MotionEvent.ACTION_DOWN) {
            mInputView.requestFocus();
        }
        return super.onInterceptTouchEvent(ev);
    }

    // Public methods
    public String getText() {
        return mInputView != null ? mInputView.getText().toString() : "";
    }

    public void setText(String text) {
        if (mInputView != null) {
            mInputView.setText(text);
        }
    }

    public void setError(String error) {
        mError = error;
        if (mErrorView != null) {
            mErrorView.setText(error);
            mErrorView.setVisibility(error.isEmpty() ? GONE : VISIBLE);
            invalidate(); // Redraw to update border color
        }
    }

    public void setInputType(int inputType) {
        if (mInputView != null) {
            mInputView.setInputType(inputType);
        }
    }

    public EditText getInputView() {
        return mInputView;
    }

    public boolean isValid() {
        // Basic validation: check if required field is empty
        if (mIsRequired && getText().trim().isEmpty()) {
            setError("This field is required"); // 使用硬编码字符串替代不存在的资源
            return false;
        }
        return mError.isEmpty();
    }

    public void updateForTheme() {
        mIsDarkMode = (getContext().getResources().getConfiguration().uiMode & 
                android.content.res.Configuration.UI_MODE_NIGHT_MASK) == 
                android.content.res.Configuration.UI_MODE_NIGHT_YES;
        
        // Update colors based on current theme
        mBackgroundColor = ContextCompat.getColor(getContext(), mIsDarkMode ? 
                R.color.color_surface : R.color.color_surface);
        mTextColor = ContextCompat.getColor(getContext(), mIsDarkMode ? 
                R.color.color_text : R.color.color_text);
        mBorderColor = ContextCompat.getColor(getContext(), mIsDarkMode ? 
                R.color.color_border : R.color.color_border);
        mErrorColor = ContextCompat.getColor(getContext(), mIsDarkMode ? 
                R.color.color_danger : R.color.color_danger);
        
        // Update views
        if (mLabelView != null) {
            mLabelView.setTextColor(mTextColor);
        }
        if (mInputView != null) {
            mInputView.setTextColor(mTextColor);
            mInputView.setHintTextColor(ContextCompat.getColor(getContext(), mIsDarkMode ? 
                    R.color.color_text_subtle : R.color.color_text_subtle));
        }
        if (mErrorView != null) {
            mErrorView.setTextColor(mErrorColor);
        }
        
        invalidate();
    }
}
