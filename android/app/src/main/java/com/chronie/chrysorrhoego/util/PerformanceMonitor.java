package com.chronie.chrysorrhoego.util;

import android.os.Build;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Choreographer;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 性能监控工具类
 * 用于监控应用性能指标并提供优化建议
 */
public class PerformanceMonitor {

    private static final String TAG = "PerformanceMonitor";
    private static final PerformanceMonitor INSTANCE = new PerformanceMonitor();
    
    // 监控帧率阈值 (fps)
    private static final int FPS_THRESHOLD = 55;
    // 监控方法执行时间阈值 (ms)
    private static final long METHOD_EXECUTION_THRESHOLD = 100;
    // UI渲染超时阈值 (ms)
    private static final long UI_RENDER_TIMEOUT = 16; // 约60fps
    
    // 方法执行时间记录
    private final Map<String, Long> methodStartTimes = new HashMap<>();
    private final Map<String, Long> methodExecutionCounts = new HashMap<>();
    private final Map<String, Long> methodTotalExecutionTime = new HashMap<>();
    
    // FPS监控相关
    private long lastFrameTimeNanos = 0;
    private int frameCount = 0;
    private long fpsStartTimeMillis = 0;
    private int fps = 0;
    private FpsCallback fpsCallback;
    
    // 内存使用监控
    private long lastMemoryUsage = 0;
    
    private PerformanceMonitor() {
        // 私有构造函数
    }
    
    public static PerformanceMonitor getInstance() {
        return INSTANCE;
    }
    
    /**
     * 开始监控方法执行时间
     * @param methodName 方法名称
     */
    public void startMethodMonitoring(String methodName) {
        methodStartTimes.put(methodName, System.currentTimeMillis());
    }
    
    /**
     * 结束监控方法执行时间
     * @param methodName 方法名称
     * @return 执行时间（毫秒）
     */
    public long endMethodMonitoring(String methodName) {
        Long startTime = methodStartTimes.remove(methodName);
        if (startTime == null) {
            Log.e(TAG, "No start time recorded for method: " + methodName);
            return -1;
        }
        
        long executionTime = System.currentTimeMillis() - startTime;
        
        // 更新统计信息
        methodExecutionCounts.put(methodName, methodExecutionCounts.getOrDefault(methodName, 0L) + 1);
        methodTotalExecutionTime.put(methodName, methodTotalExecutionTime.getOrDefault(methodName, 0L) + executionTime);
        
        // 检查是否超过阈值
        if (executionTime > METHOD_EXECUTION_THRESHOLD) {
            Log.w(TAG, "Slow method detected: " + methodName + " executed in " + executionTime + "ms");
        }
        
        return executionTime;
    }
    
    /**
     * 记录方法执行时间（方便链式调用）
     * @param methodName 方法名称
     * @return PerformanceMonitor实例
     */
    public PerformanceMonitor monitor(String methodName) {
        endMethodMonitoring(methodName);
        return this;
    }
    
    /**
     * 开始FPS监控
     * @param callback FPS变化回调
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void startFpsMonitoring(final FpsCallback callback) {
        this.fpsCallback = callback;
        this.frameCount = 0;
        this.fpsStartTimeMillis = System.currentTimeMillis();
        
        Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback() {
            @Override
            public void doFrame(long frameTimeNanos) {
                calculateFPS(frameTimeNanos);
                // 继续监控
                Choreographer.getInstance().postFrameCallback(this);
            }
        });
    }
    
    /**
     * 计算FPS
     */
    private void calculateFPS(long frameTimeNanos) {
        if (lastFrameTimeNanos != 0) {
            frameCount++;
            long intervalMillis = System.currentTimeMillis() - fpsStartTimeMillis;
            
            if (intervalMillis >= 1000) { // 每秒更新一次
                fps = (int) (frameCount * 1000 / intervalMillis);
                
                // 检查FPS是否低于阈值
                if (fps < FPS_THRESHOLD) {
                    Log.w(TAG, "Low FPS detected: " + fps + "fps (threshold: " + FPS_THRESHOLD + "fps)");
                }
                
                if (fpsCallback != null) {
                    fpsCallback.onFpsUpdated(fps);
                }
                
                frameCount = 0;
                fpsStartTimeMillis = System.currentTimeMillis();
            }
            
            // 检查单帧渲染时间
            long frameTimeMs = (frameTimeNanos - lastFrameTimeNanos) / 1000000;
            if (frameTimeMs > UI_RENDER_TIMEOUT) {
                Log.w(TAG, "Slow frame rendering detected: " + frameTimeMs + "ms (target: 16ms)");
            }
        }
        
        lastFrameTimeNanos = frameTimeNanos;
    }
    
    /**
     * 获取当前FPS
     * @return FPS值
     */
    public int getCurrentFps() {
        return fps;
    }
    
    /**
     * 监控内存使用情况
     */
    public void monitorMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long usedMemoryMB = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
        
        // 检查内存增长是否异常
        if (lastMemoryUsage > 0 && usedMemoryMB > lastMemoryUsage * 1.5) {
            Log.w(TAG, "Memory usage increased significantly: " + lastMemoryUsage + "MB -> " + usedMemoryMB + "MB");
        }
        
        Log.d(TAG, "Current memory usage: " + usedMemoryMB + "MB");
        lastMemoryUsage = usedMemoryMB;
    }
    
    /**
     * 分析内存堆信息
     */
    public void dumpMemoryInfo() {
        Debug.MemoryInfo memInfo = new Debug.MemoryInfo();
        Debug.getMemoryInfo(memInfo);
        
        Log.d(TAG, "Memory info - Native: " + memInfo.nativePss / 1024 + "MB, Dalvik: " + memInfo.dalvikPss / 1024 + "MB, Total: " + memInfo.getTotalPss() / 1024 + "MB");
    }
    
    /**
     * 检测视图层次深度
     * @param view 根视图
     * @return 视图层次深度
     */
    public int checkViewHierarchyDepth(View view) {
        if (view == null) return 0;
        int maxDepth = 0;
        
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                int depth = checkViewHierarchyDepth(viewGroup.getChildAt(i)) + 1;
                maxDepth = Math.max(maxDepth, depth);
            }
        }
        
        // 视图层次过深会影响性能
        if (maxDepth > 10) {
            Log.w(TAG, "Deep view hierarchy detected: " + maxDepth + " levels (recommended max: 10)");
        }
        
        return maxDepth;
    }
    
    /**
     * 获取性能优化建议
     * @return 性能优化建议字符串
     */
    public String getOptimizationSuggestions() {
        StringBuilder suggestions = new StringBuilder();
        
        // 分析方法执行时间
        suggestions.append("===== Performance Optimization Suggestions =====\n");
        
        for (Map.Entry<String, Long> entry : methodTotalExecutionTime.entrySet()) {
            String methodName = entry.getKey();
            long totalTime = entry.getValue();
            long count = methodExecutionCounts.getOrDefault(methodName, 1L);
            long avgTime = totalTime / count;
            
            if (avgTime > METHOD_EXECUTION_THRESHOLD) {
                suggestions.append("- Method \"").append(methodName).append("\" is slow: avg ")
                        .append(avgTime).append("ms (executed ").append(count).append(" times)\n");
            }
        }
        
        // 检查FPS
        if (fps > 0 && fps < FPS_THRESHOLD) {
            suggestions.append("- Current FPS is low: " + fps + "fps. Check for UI rendering issues.\n");
        }
        
        // 提供通用建议
        suggestions.append("- Consider using background threads for long-running operations\n");
        suggestions.append("- Optimize image loading and consider using image compression\n");
        suggestions.append("- Minimize UI updates on the main thread\n");
        suggestions.append("- Use view recycling in lists and grids\n");
        suggestions.append("- Avoid creating unnecessary objects in frequently called methods\n");
        
        return suggestions.toString();
    }
    
    /**
     * 清理性能监控数据
     */
    public void clear() {
        methodStartTimes.clear();
        methodExecutionCounts.clear();
        methodTotalExecutionTime.clear();
        lastFrameTimeNanos = 0;
        frameCount = 0;
        fpsStartTimeMillis = 0;
        fps = 0;
        fpsCallback = null;
    }
    
    /**
     * FPS监控回调接口
     */
    public interface FpsCallback {
        void onFpsUpdated(int fps);
    }
    
    /**
     * UI渲染超时检测辅助类
     */
    public static class UiRenderTimeoutDetector implements Runnable {
        
        private final String operationName;
        private final Handler mainHandler;
        private final Runnable onTimeout;
        
        public UiRenderTimeoutDetector(String operationName, Runnable onTimeout) {
            this.operationName = operationName;
            this.mainHandler = new Handler(Looper.getMainLooper());
            this.onTimeout = onTimeout;
        }
        
        /**
         * 开始监控UI渲染
         * @param timeoutMs 超时时间（毫秒）
         */
        public void start(long timeoutMs) {
            mainHandler.postDelayed(this, timeoutMs);
        }
        
        /**
         * 取消监控
         */
        public void cancel() {
            mainHandler.removeCallbacks(this);
        }
        
        @Override
        public void run() {
            Log.w(TAG, "UI operation might be blocking: " + operationName);
            if (onTimeout != null) {
                onTimeout.run();
            }
        }
    }
}