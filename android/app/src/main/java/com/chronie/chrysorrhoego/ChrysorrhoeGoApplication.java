package com.chronie.chrysorrhoego;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Process;
import android.util.Log;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 应用程序主类，作为应用的入口点
 */
public class ChrysorrhoeGoApplication extends Application {
    private static final String TAG = "ChrysorrhoeGoApp";
    private static ChrysorrhoeGoApplication instance;
    private static Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        setupUncaughtExceptionHandler();
        Log.d(TAG, "Application initialized");
    }

    /**
     * 设置全局未捕获异常处理器
     * 用于捕获应用崩溃并向系统报告错误
     */
    private void setupUncaughtExceptionHandler() {
        // 保存默认的异常处理器
        defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        
        // 设置自定义的异常处理器
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                Log.e(TAG, "Uncaught exception detected", ex);
                
                // 记录崩溃信息
                logCrash(thread, ex);
                
                // 将异常交给默认处理器，让系统报告错误
                if (defaultUncaughtExceptionHandler != null) {
                    defaultUncaughtExceptionHandler.uncaughtException(thread, ex);
                } else {
                    // 如果没有默认处理器，强制终止进程
                    Process.killProcess(Process.myPid());
                }
            }
        });
    }

    /**
     * 记录崩溃信息
     * @param thread 发生异常的线程
     * @param ex 异常对象
     */
    private void logCrash(Thread thread, Throwable ex) {
        try {
            // 获取应用信息
            PackageInfo packageInfo = getPackageManager().getPackageInfo(
                    getPackageName(), 0);
            String versionName = packageInfo.versionName;
            int versionCode = packageInfo.versionCode;
            
            // 构建崩溃报告内容
            StringBuilder crashReport = new StringBuilder();
            crashReport.append("=== CRASH REPORT ===")
                      .append("\nTime: ")
                      .append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", 
                              Locale.getDefault()).format(new Date()))
                      .append("\nApp Version: ")
                      .append(versionName).append(" (")
                      .append(versionCode).append(")")
                      .append("\nAndroid Version: ")
                      .append(Build.VERSION.RELEASE)
                      .append(" (API ").append(Build.VERSION.SDK_INT).append(")")
                      .append("\nDevice: ")
                      .append(Build.MANUFACTURER).append(" ")
                      .append(Build.MODEL)
                      .append("\nThread: ")
                      .append(thread.getName())
                      .append(" (ID: ").append(thread.getId()).append(")")
                      .append("\nException: ")
                      .append(ex.toString())
                      .append("\nStack Trace:\n");
            
            // 添加堆栈跟踪
            for (StackTraceElement element : ex.getStackTrace()) {
                crashReport.append("    at ")
                          .append(element.toString())
                          .append("\n");
            }
            
            // 添加原因异常
            Throwable cause = ex.getCause();
            if (cause != null) {
                crashReport.append("\nCaused by: ")
                          .append(cause.toString())
                          .append("\nCause Stack Trace:\n");
                for (StackTraceElement element : cause.getStackTrace()) {
                    crashReport.append("    at ")
                              .append(element.toString())
                              .append("\n");
                }
            }
            
            Log.e(TAG, "Crash report:\n" + crashReport.toString());
            
            // 保存崩溃报告到文件（可选）
            saveCrashReportToFile(crashReport.toString());
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to log crash information", e);
        }
    }

    /**
     * 保存崩溃报告到文件
     * @param report 崩溃报告内容
     */
    private void saveCrashReportToFile(String report) {
        try {
            File crashDir = new File(getExternalCacheDir(), "crashes");
            if (!crashDir.exists()) {
                crashDir.mkdirs();
            }
            
            String fileName = "crash_" + 
                    new SimpleDateFormat("yyyyMMdd_HHmmss", 
                            Locale.getDefault()).format(new Date()) + 
                    ".txt";
            
            File crashFile = new File(crashDir, fileName);
            try (FileWriter writer = new FileWriter(crashFile)) {
                writer.write(report);
            }
            
            Log.d(TAG, "Crash report saved to " + crashFile.getAbsolutePath());
        } catch (IOException e) {
            Log.e(TAG, "Failed to save crash report to file", e);
        }
    }

    /**
     * 获取应用程序实例
     * @return 应用程序实例
     */
    public static ChrysorrhoeGoApplication getInstance() {
        return instance;
    }
    
    /**
     * 获取应用上下文
     * @return 应用上下文
     */
    public static Context getAppContext() {
        return instance != null ? instance.getApplicationContext() : null;
    }
}