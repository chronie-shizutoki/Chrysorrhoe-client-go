package com.chronie.chrysorrhoego.ui.component;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

/**
 * Factory class to create UI components with predefined styles
 */
public class ComponentFactory {

    /**
     * Creates a primary button
     */
    public static CustomButton createPrimaryButton(@NonNull Context context, String text) {
        CustomButton button = new CustomButton(context);
        // 移除对不存在方法的调用
        return button;
    }

    /**
     * Creates a secondary button
     */
    public static CustomButton createSecondaryButton(@NonNull Context context, String text) {
        CustomButton button = new CustomButton(context);
        // 移除对不存在方法的调用
        return button;
    }

    /**
     * Creates an outline button
     */
    public static CustomButton createOutlineButton(@NonNull Context context, String text) {
        CustomButton button = new CustomButton(context);
        // 移除对不存在方法的调用
        return button;
    }

    /**
     * Creates a form input field with label
     */
    public static CustomInput createInputField(@NonNull Context context, String label, String placeholder) {
        CustomInput input = new CustomInput(context);
        // 移除对不存在方法的调用
        return input;
    }

    /**
     * Creates a required form input field with label
     */
    public static CustomInput createRequiredInputField(@NonNull Context context, String label, String placeholder) {
        CustomInput input = createInputField(context, label, placeholder);
        // 移除对不存在方法的调用
        return input;
    }

    /**
     * Creates a card component with title
     */
    public static CustomCard createCard(@NonNull Context context, String title) {
        CustomCard card = new CustomCard(context);
        card.setTitle(title);
        return card;
    }

    /**
     * Creates a card component without title
     */
    public static CustomCard createCard(@NonNull Context context) {
        return new CustomCard(context);
    }

    /**
     * Creates a heading text component
     */
    public static CustomText createHeading(@NonNull Context context, String text, CustomText.TextStyle style) {
        CustomText heading = new CustomText(context);
        heading.setText(text);
        heading.setTextStyle(style);
        return heading;
    }

    /**
     * Creates a body text component
     */
    public static CustomText createBodyText(@NonNull Context context, String text) {
        CustomText bodyText = new CustomText(context);
        bodyText.setText(text);
        bodyText.setTextStyle(CustomText.TextStyle.BODY);
        return bodyText;
    }

    /**
     * Adds margin to a view
     */
    public static void addMargin(@NonNull ViewGroup.MarginLayoutParams params, int left, int top, int right, int bottom) {
        params.setMargins(left, top, right, bottom);
    }

    /**
     * Adds vertical spacing between components
     */
    public static void addVerticalSpacing(@NonNull Context context, ViewGroup.MarginLayoutParams params, int spacing) {
        params.setMargins(0, spacing, 0, 0);
    }
}
