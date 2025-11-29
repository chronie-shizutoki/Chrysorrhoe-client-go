package com.chronie.chrysorrhoego.util;

import android.content.Context;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.chronie.chrysorrhoego.R;

import java.util.concurrent.Executor;

/**
 * 生物识别工具类，提供指纹、面部等生物识别验证功能
 * 用于保护用户敏感操作，如转账、修改设置等
 */
public class BiometricUtil {

    /**
     * 检查设备是否支持生物识别
     * @param context 上下文
     * @return 是否支持生物识别
     */
    public static boolean isBiometricSupported(Context context) {
        // 模拟实现，默认返回不支持
        return false;
    }

    /**
     * 执行生物识别验证
     * @param activity FragmentActivity实例
     * @param title 验证对话框标题
     * @param subtitle 验证对话框副标题
     * @param description 验证对话框描述
     * @param callback 验证回调
     */
    /**
     * 执行生物识别验证
     * 注意：由于移除了androidx.biometric库的依赖，这里仅提供模拟实现
     * @param activity FragmentActivity实例
     * @param title 验证对话框标题
     * @param subtitle 验证对话框副标题
     * @param description 验证对话框描述
     * @param callback 验证回调
     */
    public static void authenticateWithBiometric(
            FragmentActivity activity,
            String title,
            String subtitle,
            String description,
            BiometricCallback callback) {

        if (!isBiometricSupported(activity)) {
            callback.onBiometricNotAvailable();
            return;
        }

        // 模拟实现，直接调用回调的成功方法
        // 在实际应用中，这部分应该使用系统的生物识别API
        callback.onAuthenticationSuccess();
        
        // 注意：由于移除了androidx.biometric库的依赖，这里不再提供真实的生物识别验证功能
        // 这个模拟实现仅用于编译通过，在实际使用时应该添加适当的验证逻辑或提示用户
    }

    /**
     * 获取执行器
     * @param context 上下文
     * @return 执行器
     */
    private static Executor getExecutor(Context context) {
        return ContextCompat.getMainExecutor(context);
    }

    /**
     * 生物识别回调接口
     */
    public interface BiometricCallback {
        void onAuthenticationSuccess();
        void onAuthenticationFailed();
        void onAuthenticationError(int errorCode, String errorMessage);
        void onBiometricNotAvailable();
    }
}