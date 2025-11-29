package com.chronie.chrysorrhoego.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 应用版本信息管理类
 * 提供应用版本号、构建信息等统一访问接口
 */
public class VersionInfo {

    // 应用版本号常量
    public static final int MAJOR_VERSION = 1;
    public static final int MINOR_VERSION = 0;
    public static final int PATCH_VERSION = 0;
    public static final int BUILD_NUMBER = 100;
    
    // 版本状态
    public static final String VERSION_STATUS = "正式版";
    
    // 应用版本名称格式
    public static final String VERSION_NAME = MAJOR_VERSION + "." + MINOR_VERSION + "." + PATCH_VERSION;
    
    // 完整版本号（包含构建号）
    public static final String FULL_VERSION = VERSION_NAME + "." + BUILD_NUMBER + " " + VERSION_STATUS;
    
    // 构建日期（编译时自动生成）
    public static final String BUILD_DATE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    
    // 构建环境
    public static final String BUILD_ENVIRONMENT = "production";
    
    /**
     * 获取应用的版本名称（来自PackageManager）
     * @param context 上下文
     * @return 应用版本名称
     */
    public static String getAppVersionName(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return VERSION_NAME;
        }
    }
    
    /**
     * 获取应用的版本号（来自PackageManager）
     * @param context 上下文
     * @return 应用版本号
     */
    public static int getAppVersionCode(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                return (int) packageInfo.getLongVersionCode();
            } else {
                return packageInfo.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return BUILD_NUMBER;
        }
    }
    
    /**
     * 获取版本信息字符串，用于显示在关于页面
     * @param context 上下文
     * @return 格式化的版本信息
     */
    public static String getVersionInfoString(Context context) {
        StringBuilder sb = new StringBuilder();
        sb.append("版本: ").append(getAppVersionName(context)).append(" (Build ").append(getAppVersionCode(context)).append(")");
        sb.append("\n状态: ").append(VERSION_STATUS);
        sb.append("\n构建日期: ").append(BUILD_DATE);
        return sb.toString();
    }
    
    /**
     * 检查是否为开发版本
     * @return 是否为开发版本
     */
    public static boolean isDevelopmentVersion() {
        return BUILD_ENVIRONMENT.equals("development") || VERSION_STATUS.contains("开发") || VERSION_STATUS.contains("Debug");
    }
    
    /**
     * 检查是否为测试版本
     * @return 是否为测试版本
     */
    public static boolean isBetaVersion() {
        return BUILD_ENVIRONMENT.equals("beta") || VERSION_STATUS.contains("测试") || VERSION_STATUS.contains("Beta");
    }
    
    /**
     * 检查是否为正式版本
     * @return 是否为正式版本
     */
    public static boolean isReleaseVersion() {
        return BUILD_ENVIRONMENT.equals("production") && VERSION_STATUS.equals("正式版");
    }
    
    /**
     * 获取构建环境信息
     * @return 构建环境
     */
    public static String getBuildEnvironment() {
        return BUILD_ENVIRONMENT;
    }
    
    /**
     * 比较版本号，判断是否需要更新
     * @param currentVersion 当前版本号
     * @param latestVersion 最新版本号
     * @return 是否需要更新
     */
    public static boolean isUpdateRequired(String currentVersion, String latestVersion) {
        try {
            if (currentVersion == null || latestVersion == null) {
                return false;
            }
            
            String[] currentParts = currentVersion.split("\\.");
            String[] latestParts = latestVersion.split("\\.");
            
            int minLength = Math.min(currentParts.length, latestParts.length);
            
            for (int i = 0; i < minLength; i++) {
                int current = Integer.parseInt(currentParts[i].replaceAll("\\D+", ""));
                int latest = Integer.parseInt(latestParts[i].replaceAll("\\D+", ""));
                
                if (latest > current) {
                    return true;
                } else if (latest < current) {
                    return false;
                }
                // 相等则继续比较下一部分
            }
            
            // 如果前面部分都相等，检查长度
            return latestParts.length > currentParts.length;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 获取应用发布说明
     * @return 发布说明
     */
    public static String getReleaseNotes() {
        return "Chronie钱包 v" + VERSION_NAME + " 更新内容:\n" +
                "1. 实现钱包信息管理与余额查询功能\n" +
                "2. 支持交易历史记录查询与筛选\n" +
                "3. 提供用户间安全转账服务\n" +
                "4. 新增CDK码兑换功能\n" +
                "5. 支持交易状态实时验证\n" +
                "6. 多语言支持（中文、英文、日文、韩文）\n" +
                "7. 优化应用性能与用户体验\n" +
                "8. 增强应用安全性\n";
    }
}
