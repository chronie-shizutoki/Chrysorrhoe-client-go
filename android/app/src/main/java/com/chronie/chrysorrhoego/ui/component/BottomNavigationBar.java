package com.chronie.chrysorrhoego.ui.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.chronie.chrysorrhoego.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义底部导航栏组件，实现与网页端设计一致的导航结构
 */
public class BottomNavigationBar extends FrameLayout {

    public interface OnTabSelectedListener {
        void onTabSelected(int position);
    }

    private class TabItem {
        int iconResId;
        int selectedIconResId;
        String title;
        TextView titleView;
        ImageView iconView;
        LinearLayout tabContainer;
        boolean isSelected;

        TabItem(int iconResId, int selectedIconResId, String title) {
            this.iconResId = iconResId;
            this.selectedIconResId = selectedIconResId;
            this.title = title;
            this.isSelected = false;
        }
    }

    private List<TabItem> mTabs = new ArrayList<>();
    private LinearLayout mTabContainer;
    private OnTabSelectedListener mListener;
    private int mSelectedPosition = -1;
    private boolean mIsDarkMode;
    
    private int mBackgroundColor;
    private int mSelectedColor;
    private int mUnselectedColor;
    private int mBorderColor;
    private float mBorderRadius;

    public BottomNavigationBar(Context context) {
        super(context);
        init(null);
    }

    public BottomNavigationBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public BottomNavigationBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        mSelectedColor = ContextCompat.getColor(getContext(), R.color.color_primary);
        mUnselectedColor = ContextCompat.getColor(getContext(), mIsDarkMode ? 
                R.color.color_text_secondary : R.color.color_text_secondary);
        mBorderColor = ContextCompat.getColor(getContext(), mIsDarkMode ? 
                R.color.color_border : R.color.color_border);
        mBorderRadius = 0f; // No top radius by default

        // Load custom attributes if available
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.BottomNavigationBar);
            
            if (a.hasValue(R.styleable.BottomNavigationBar_navigationBackgroundColor)) {
                mBackgroundColor = a.getColor(R.styleable.BottomNavigationBar_navigationBackgroundColor, mBackgroundColor);
            }
            if (a.hasValue(R.styleable.BottomNavigationBar_navigationSelectedColor)) {
                mSelectedColor = a.getColor(R.styleable.BottomNavigationBar_navigationSelectedColor, mSelectedColor);
            }
            if (a.hasValue(R.styleable.BottomNavigationBar_navigationUnselectedColor)) {
                mUnselectedColor = a.getColor(R.styleable.BottomNavigationBar_navigationUnselectedColor, mUnselectedColor);
            }
            if (a.hasValue(R.styleable.BottomNavigationBar_navigationBorderColor)) {
                mBorderColor = a.getColor(R.styleable.BottomNavigationBar_navigationBorderColor, mBorderColor);
            }
            if (a.hasValue(R.styleable.BottomNavigationBar_navigationBorderRadius)) {
                mBorderRadius = a.getDimension(R.styleable.BottomNavigationBar_navigationBorderRadius, mBorderRadius);
            }
            
            a.recycle();
        }

        // Create tab container
        mTabContainer = new LinearLayout(getContext());
        mTabContainer.setOrientation(LinearLayout.HORIZONTAL);
        mTabContainer.setLayoutParams(new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        mTabContainer.setWeightSum(3); // 默认3个标签均匀分布
        
        addView(mTabContainer);
    }

    /**
     * 添加导航标签
     */
    public void addTab(int iconResId, int selectedIconResId, String title) {
        TabItem tabItem = new TabItem(iconResId, selectedIconResId, title);
        mTabs.add(tabItem);
        
        // 创建标签视图
        createTabView(tabItem);
        
        // 更新权重总和
        mTabContainer.setWeightSum(mTabs.size());
    }

    private void createTabView(TabItem tabItem) {
        int position = mTabs.indexOf(tabItem);
        
        // 创建标签容器
        tabItem.tabContainer = new LinearLayout(getContext());
        tabItem.tabContainer.setOrientation(LinearLayout.VERTICAL);
        tabItem.tabContainer.setGravity(android.view.Gravity.CENTER);
        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.MATCH_PARENT,
                1f
        );
        tabItem.tabContainer.setLayoutParams(containerParams);
        tabItem.tabContainer.setClickable(true);
        tabItem.tabContainer.setFocusable(true);
        
        // 设置点击事件
        tabItem.tabContainer.setOnClickListener(v -> {
            setSelectedTab(position);
            if (mListener != null) {
                mListener.onTabSelected(position);
            }
        });
        
        // 创建图标视图
        tabItem.iconView = new ImageView(getContext());
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        iconParams.bottomMargin = (int) getResources().getDimension(R.dimen.space_xs);
        tabItem.iconView.setLayoutParams(iconParams);
        tabItem.iconView.setImageResource(tabItem.iconResId);
        tabItem.iconView.setColorFilter(mUnselectedColor);
        
        // 创建标题视图
        tabItem.titleView = new TextView(getContext());
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        tabItem.titleView.setLayoutParams(titleParams);
        tabItem.titleView.setText(tabItem.title);
        tabItem.titleView.setTextSize(12);
        tabItem.titleView.setTextColor(mUnselectedColor);
        
        // 添加视图到容器
        tabItem.tabContainer.addView(tabItem.iconView);
        tabItem.tabContainer.addView(tabItem.titleView);
        mTabContainer.addView(tabItem.tabContainer);
        
        // 默认选中第一个标签
        if (position == 0 && mSelectedPosition == -1) {
            setSelectedTab(0);
        }
    }

    /**
     * 设置选中的标签
     */
    public void setSelectedTab(int position) {
        if (position < 0 || position >= mTabs.size() || position == mSelectedPosition) {
            return;
        }
        
        // 取消之前选中的标签
        if (mSelectedPosition >= 0 && mSelectedPosition < mTabs.size()) {
            TabItem previousTab = mTabs.get(mSelectedPosition);
            previousTab.isSelected = false;
            previousTab.iconView.setImageResource(previousTab.iconResId);
            previousTab.iconView.setColorFilter(mUnselectedColor);
            previousTab.titleView.setTextColor(mUnselectedColor);
        }
        
        // 设置新选中的标签
        TabItem selectedTab = mTabs.get(position);
        selectedTab.isSelected = true;
        selectedTab.iconView.setImageResource(selectedTab.selectedIconResId);
        selectedTab.iconView.setColorFilter(mSelectedColor);
        selectedTab.titleView.setTextColor(mSelectedColor);
        
        mSelectedPosition = position;
    }

    /**
     * 获取当前选中的标签位置
     */
    public int getSelectedTabPosition() {
        return mSelectedPosition;
    }

    /**
     * 设置标签选中监听器
     */
    public void setOnTabSelectedListener(OnTabSelectedListener listener) {
        mListener = listener;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        // 绘制背景
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(mBackgroundColor);
        
        // 绘制带顶部圆角的背景
        float[] radii = {mBorderRadius, mBorderRadius, mBorderRadius, mBorderRadius, 0, 0, 0, 0};
        canvas.drawRoundRect(0, 0, getWidth(), getHeight(), mBorderRadius, mBorderRadius, paint);
        
        // 绘制顶部边框
        paint.setColor(mBorderColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
        canvas.drawLine(0, 0, getWidth(), 0, paint);
        
        super.dispatchDraw(canvas);
    }

    /**
     * 更新主题样式
     */
    public void updateForTheme() {
        mIsDarkMode = (getContext().getResources().getConfiguration().uiMode & 
                android.content.res.Configuration.UI_MODE_NIGHT_MASK) == 
                android.content.res.Configuration.UI_MODE_NIGHT_YES;
        
        // 更新颜色
        mBackgroundColor = ContextCompat.getColor(getContext(), mIsDarkMode ? 
                R.color.color_surface : R.color.color_surface);
        mUnselectedColor = ContextCompat.getColor(getContext(), mIsDarkMode ? 
                R.color.color_text_secondary : R.color.color_text_secondary);
        mBorderColor = ContextCompat.getColor(getContext(), mIsDarkMode ? 
                R.color.color_border : R.color.color_border);
        
        // 更新所有标签的颜色
        for (TabItem tabItem : mTabs) {
            if (tabItem.isSelected) {
                tabItem.titleView.setTextColor(mSelectedColor);
                tabItem.iconView.setColorFilter(mSelectedColor);
            } else {
                tabItem.titleView.setTextColor(mUnselectedColor);
                tabItem.iconView.setColorFilter(mUnselectedColor);
            }
        }
        
        invalidate();
    }
}
