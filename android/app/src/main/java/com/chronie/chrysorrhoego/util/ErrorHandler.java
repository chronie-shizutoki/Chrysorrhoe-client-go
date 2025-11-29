package com.chronie.chrysorrhoego.util;

import com.chronie.chrysorrhoego.ChrysorrhoeGoApplication;
import android.content.Context;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import com.chronie.chrysorrhoego.R;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 错误处理工具类，用于统一管理应用中的异常和错误情况
 */
public class ErrorHandler {
    private static final String TAG = "ErrorHandler";
    private static final String ERROR_LOG_FILE = "error_log.txt";
    // 调试模式标志
    private static final boolean DEBUG = true;

    /**
     * 处理网络错误
     * @param context 上下文
     * @param e 异常对象
     */
    public static void handleNetworkError(Context context, Exception e) {
        String errorMessage = context.getString(R.string.error_network_connection);
        logError(TAG, "Network error: " + e.getMessage(), e);
        showToast(context, errorMessage);
    }

    /**
     * 处理认证错误
     * @param context 上下文
     * @param e 异常对象
     */
    public static void handleAuthError(Context context, Exception e) {
        String errorMessage = context.getString(R.string.error_authentication_failed);
        logError(TAG, "Authentication error: " + e.getMessage(), e);
        showToast(context, errorMessage);
    }

    /**
     * 处理API错误
     * @param context 上下文
     * @param errorCode 错误码
     * @param errorMessage 错误信息
     */
    public static void handleApiError(Context context, int errorCode, String errorMessage) {
        String displayMessage = errorMessage != null ? errorMessage : context.getString(R.string.error_server);
        logError(TAG, "API error [" + errorCode + "]: " + displayMessage, null);
        
        switch (errorCode) {
            case 401:
                showErrorDialog(context, context.getString(R.string.error_session_expired),
                        context.getString(R.string.message_please_login_again));
                break;
            case 403:
                showErrorDialog(context, context.getString(R.string.error_access_denied),
                        context.getString(R.string.message_insufficient_permissions));
                break;
            case 404:
                showErrorDialog(context, context.getString(R.string.error_not_found),
                        context.getString(R.string.message_resource_not_available));
                break;
            case 500:
                showErrorDialog(context, context.getString(R.string.error_server),
                        context.getString(R.string.message_server_error_retry));
                break;
            default:
                showToast(context, displayMessage);
        }
    }

    /**
     * 处理表单验证错误
     * @param context 上下文
     * @param errorMessage 错误信息
     */
    public static void handleValidationError(Context context, String errorMessage) {
        logError(TAG, "Validation error: " + errorMessage, null);
        showToast(context, errorMessage);
    }

    /**
     * 处理未知错误
     * @param context 上下文
     * @param e 异常对象
     */
    public static void handleUnknownError(Context context, Exception e) {
        String errorMessage = context.getString(R.string.error_unknown);
        logError(TAG, "Unknown error: " + e.getMessage(), e);
        
        // 在开发模式下显示详细错误信息
        if (DEBUG) {
            showErrorDialog(context, errorMessage, e.getMessage());
        } else {
            showToast(context, errorMessage);
            // 可以在这里添加错误报告机制
        }
    }

    /**
     * 记录错误到日志文件
     * @param tag 日志标签
     * @param message 错误信息
     * @param e 异常对象（可选）
     */
    public static void logError(String tag, String message, Exception e) {
        // 打印到Android日志
        if (e != null) {
            Log.e(tag, message, e);
        } else {
            Log.e(tag, message);
        }

        // 写入到错误日志文件（仅在调试模式下）
        if (DEBUG) {
            writeToLogFile(tag, message, e);
        }
    }

    /**
     * 写入错误到本地日志文件
     */
    private static void writeToLogFile(String tag, String message, Exception e) {
        try {
            // 获取应用的缓存目录
            File logDir = new File(ChrysorrhoeGoApplication.getAppContext().getCacheDir(), "logs");
            if (!logDir.exists()) {
                logDir.mkdirs();
            }

            File logFile = new File(logDir, ERROR_LOG_FILE);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
            String timestamp = dateFormat.format(new Date());

            StringBuilder logEntry = new StringBuilder();
            logEntry.append("[").append(timestamp).append("] [").append(tag).append("]: ")
                   .append(message).append("\n");

            if (e != null) {
                logEntry.append("Exception: ").append(e.getMessage()).append("\n");
                for (StackTraceElement element : e.getStackTrace()) {
                    logEntry.append("    at ").append(element.toString()).append("\n");
                }
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true));
            writer.append(logEntry.toString());
            writer.close();
        } catch (IOException ex) {
            Log.e(TAG, "Failed to write to log file", ex);
        }
    }

    /**
     * 显示错误对话框
     * @param context 上下文
     * @param title 标题
     * @param message 消息内容
     */
    public static void showErrorDialog(Context context, String title, String message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }

    /**
     * 显示确认对话框
     * @param context 上下文
     * @param title 标题
     * @param message 消息内容
     * @param positiveAction 确认按钮动作
     * @param negativeAction 取消按钮动作
     */
    public static void showConfirmDialog(Context context, String title, String message,
                                       DialogInterface.OnClickListener positiveAction,
                                       DialogInterface.OnClickListener negativeAction) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.button_confirm), positiveAction)
                .setNegativeButton(context.getString(R.string.button_cancel), negativeAction)
                .setCancelable(true);
        
        builder.show();
    }

    /**
     * 显示Toast消息
     * @param context 上下文
     * @param message 消息内容
     */
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示长时间Toast消息
     * @param context 上下文
     * @param message 消息内容
     */
    public static void showLongToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}