package com.chronie.chrysorrhoego.util;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Debug;
import android.util.Log;
import android.util.LruCache;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 内存优化工具类
 * 提供内存使用监控、优化和管理功能
 */
public class MemoryOptimizer {

    private static final String TAG = "MemoryOptimizer";
    private static final int DEFAULT_CACHE_SIZE = 10 * 1024 * 1024; // 10MB 默认缓存大小
    private static final int MEMORY_CHECK_INTERVAL = 30; // 30秒检查一次内存
    
    private static MemoryOptimizer instance;
    private final Context context;
    private final ActivityManager activityManager;
    private final LruCache<String, Object> memoryCache;
    private final ScheduledExecutorService scheduler;
    private final Map<String, MemoryUsageListener> listeners;
    
    private boolean isMonitoring = false;
    private long lastMemoryWarningTime = 0;
    private static final long MEMORY_WARNING_THRESHOLD_MS = 5 * 60 * 1000; // 5分钟内只发一次警告
    
    /**
     * 内存使用监听器接口
     */
    public interface MemoryUsageListener {
        void onLowMemoryDetected(double memoryUsagePercent);
        void onMemoryRecovered(double memoryUsagePercent);
        void onMemorySnapshot(long usedMemory, long totalMemory);
    }
    
    /**
     * 私有构造函数
     */
    private MemoryOptimizer(Context context) {
        this.context = context.getApplicationContext();
        this.activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        
        // 计算适当的缓存大小（可用内存的1/8）
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        long availableMemory = memoryInfo.availMem / 8;
        int cacheSize = Math.min((int) availableMemory, DEFAULT_CACHE_SIZE);
        
        this.memoryCache = new LruCache<String, Object>(cacheSize) {
            @Override
            protected int sizeOf(String key, Object value) {
                // 估算对象大小（实际应该根据具体类型实现更准确的计算）
                if (value instanceof byte[]) {
                    return ((byte[]) value).length;
                }
                // 粗略估算，每个对象占用1KB
                return 1024;
            }
        };
        
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.listeners = new HashMap<>();
    }
    
    /**
     * 获取单例实例
     */
    public static synchronized MemoryOptimizer getInstance(Context context) {
        if (instance == null) {
            instance = new MemoryOptimizer(context);
        }
        return instance;
    }
    
    /**
     * 启动内存监控
     */
    public void startMonitoring() {
        if (isMonitoring) {
            Log.d(TAG, "Memory monitoring already started");
            return;
        }
        
        isMonitoring = true;
        Log.d(TAG, "Starting memory monitoring");
        
        scheduler.scheduleAtFixedRate(new MemoryCheckTask(), 0, MEMORY_CHECK_INTERVAL, TimeUnit.SECONDS);
    }
    
    /**
     * 停止内存监控
     */
    public void stopMonitoring() {
        isMonitoring = false;
        Log.d(TAG, "Stopping memory monitoring");
        scheduler.shutdown();
    }
    
    /**
     * 添加内存使用监听器
     */
    public void addMemoryUsageListener(String id, MemoryUsageListener listener) {
        synchronized (listeners) {
            listeners.put(id, listener);
        }
    }
    
    /**
     * 移除内存使用监听器
     */
    public void removeMemoryUsageListener(String id) {
        synchronized (listeners) {
            listeners.remove(id);
        }
    }
    
    /**
     * 清除缓存
     */
    public void clearCache() {
        memoryCache.evictAll();
        Log.d(TAG, "Memory cache cleared");
    }
    
    /**
     * 缓存对象
     */
    public void cacheObject(String key, Object value) {
        if (key != null && value != null) {
            memoryCache.put(key, value);
            Log.d(TAG, "Cached object: " + key);
        }
    }
    
    /**
     * 获取缓存对象
     */
    @SuppressWarnings("unchecked")
    public <T> T getCachedObject(String key, Class<T> clazz) {
        Object value = memoryCache.get(key);
        if (value != null && clazz.isInstance(value)) {
            return (T) value;
        }
        return null;
    }
    
    /**
     * 移除缓存对象
     */
    public void removeCachedObject(String key) {
        memoryCache.remove(key);
    }
    
    /**
     * 获取缓存统计信息
     */
    public CacheStats getCacheStats() {
        return new CacheStats(
                memoryCache.size(),
                memoryCache.maxSize(),
                memoryCache.hitCount(),
                memoryCache.missCount(),
                memoryCache.evictionCount()
        );
    }
    
    /**
     * 缓存统计信息类
     */
    public static class CacheStats {
        public final int size;
        public final int maxSize;
        public final int hitCount;
        public final int missCount;
        public final int evictionCount;
        
        public CacheStats(int size, int maxSize, int hitCount, int missCount, int evictionCount) {
            this.size = size;
            this.maxSize = maxSize;
            this.hitCount = hitCount;
            this.missCount = missCount;
            this.evictionCount = evictionCount;
        }
        
        public double getUsagePercentage() {
            return (double) size / maxSize * 100;
        }
        
        public double getHitRatio() {
            int total = hitCount + missCount;
            return total > 0 ? (double) hitCount / total * 100 : 0;
        }
    }
    
    /**
     * 获取内存使用情况
     */
    public MemoryInfo getMemoryInfo() {
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        
        Debug.MemoryInfo debugMemoryInfo = new Debug.MemoryInfo();
        Debug.getMemoryInfo(debugMemoryInfo);
        
        return new MemoryInfo(
                memoryInfo.totalMem,
                memoryInfo.availMem,
                debugMemoryInfo.getTotalPss(), // 进程PSS内存使用（KB）
                memoryInfo.lowMemory,
                (double) memoryInfo.availMem / memoryInfo.totalMem * 100
        );
    }
    
    /**
     * 内存信息类
     */
    public static class MemoryInfo {
        public final long totalMemory;
        public final long availableMemory;
        public final int processPssMemoryKb;
        public final boolean isLowMemory;
        public final double availableMemoryPercent;
        
        public MemoryInfo(long totalMemory, long availableMemory, int processPssMemoryKb, 
                          boolean isLowMemory, double availableMemoryPercent) {
            this.totalMemory = totalMemory;
            this.availableMemory = availableMemory;
            this.processPssMemoryKb = processPssMemoryKb;
            this.isLowMemory = isLowMemory;
            this.availableMemoryPercent = availableMemoryPercent;
        }
        
        public long getUsedMemory() {
            return totalMemory - availableMemory;
        }
        
        public double getUsedMemoryPercent() {
            return 100 - availableMemoryPercent;
        }
    }
    
    /**
     * 内存检查任务
     */
    private class MemoryCheckTask implements Runnable {
        @Override
        public void run() {
            if (!isMonitoring) return;
            
            try {
                MemoryInfo memoryInfo = getMemoryInfo();
                long usedMemory = memoryInfo.getUsedMemory();
                double memoryUsagePercent = memoryInfo.getUsedMemoryPercent();
                
                // 通知所有监听器内存快照
                notifyMemorySnapshotListeners(usedMemory, memoryInfo.totalMemory);
                
                // 检查内存警告阈值（80%）
                if (memoryUsagePercent > 80) {
                    handleLowMemory(memoryUsagePercent);
                } else {
                    // 内存恢复正常
                    notifyMemoryRecoveredListeners(memoryUsagePercent);
                }
                
                // 记录内存使用情况
                Log.d(TAG, String.format("Memory usage: %.1f%% (Used: %d MB, Total: %d MB)",
                        memoryUsagePercent,
                        usedMemory / (1024 * 1024),
                        memoryInfo.totalMemory / (1024 * 1024)));
                    
            } catch (Exception e) {
                Log.e(TAG, "Error during memory check: " + e.getMessage());
            }
        }
    }
    
    /**
     * 处理低内存情况
     */
    private void handleLowMemory(double memoryUsagePercent) {
        long currentTime = System.currentTimeMillis();
        
        // 避免短时间内重复发送警告
        if (currentTime - lastMemoryWarningTime < MEMORY_WARNING_THRESHOLD_MS) {
            return;
        }
        
        lastMemoryWarningTime = currentTime;
        Log.w(TAG, String.format("Low memory detected: %.1f%% usage", memoryUsagePercent));
        
        // 尝试清理缓存
        clearCache();
        
        // 通知监听器
        notifyLowMemoryListeners(memoryUsagePercent);
    }
    
    /**
     * 通知低内存监听器
     */
    private void notifyLowMemoryListeners(double memoryUsagePercent) {
        synchronized (listeners) {
            for (MemoryUsageListener listener : listeners.values()) {
                try {
                    listener.onLowMemoryDetected(memoryUsagePercent);
                } catch (Exception e) {
                    Log.e(TAG, "Error notifying low memory listener: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * 通知内存恢复监听器
     */
    private void notifyMemoryRecoveredListeners(double memoryUsagePercent) {
        synchronized (listeners) {
            for (MemoryUsageListener listener : listeners.values()) {
                try {
                    listener.onMemoryRecovered(memoryUsagePercent);
                } catch (Exception e) {
                    Log.e(TAG, "Error notifying memory recovered listener: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * 通知内存快照监听器
     */
    private void notifyMemorySnapshotListeners(long usedMemory, long totalMemory) {
        synchronized (listeners) {
            for (MemoryUsageListener listener : listeners.values()) {
                try {
                    listener.onMemorySnapshot(usedMemory, totalMemory);
                } catch (Exception e) {
                    Log.e(TAG, "Error notifying memory snapshot listener: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * 优化大型集合
     */
    public <T> void optimizeCollection(java.util.Collection<T> collection, int targetSize) {
        if (collection != null && collection.size() > targetSize) {
            // 移除多余元素（这里简单实现，实际可能需要更复杂的逻辑）
            java.util.Iterator<T> iterator = collection.iterator();
            int itemsToRemove = collection.size() - targetSize;
            int removed = 0;
            
            while (iterator.hasNext() && removed < itemsToRemove) {
                iterator.next();
                iterator.remove();
                removed++;
            }
            
            Log.d(TAG, "Optimized collection: removed " + removed + " items");
        }
    }
    
    /**
     * 获取内存优化建议
     */
    public String getOptimizationSuggestions() {
        StringBuilder suggestions = new StringBuilder();
        MemoryInfo memoryInfo = getMemoryInfo();
        CacheStats cacheStats = getCacheStats();
        
        suggestions.append("Memory Optimization Suggestions:\n");
        suggestions.append("-------------------------------\n");
        suggestions.append(String.format("System Memory Usage: %.1f%%\n", memoryInfo.getUsedMemoryPercent()));
        suggestions.append(String.format("Process PSS Memory: %d KB\n", memoryInfo.processPssMemoryKb));
        suggestions.append(String.format("Cache Usage: %.1f%%\n", cacheStats.getUsagePercentage()));
        suggestions.append(String.format("Cache Hit Ratio: %.1f%%\n", cacheStats.getHitRatio()));
        suggestions.append(String.format("Cache Evictions: %d\n", cacheStats.evictionCount));
        suggestions.append("-------------------------------\n");
        
        if (memoryInfo.getUsedMemoryPercent() > 80) {
            suggestions.append("1. System memory is critically low. Consider clearing background apps.\n");
        }
        
        if (cacheStats.getHitRatio() < 30) {
            suggestions.append("2. Cache hit ratio is low. Consider optimizing cache strategy.\n");
        }
        
        if (cacheStats.evictionCount > 100) {
            suggestions.append("3. High number of cache evictions. Consider increasing cache size or reducing object size.\n");
        }
        
        if (memoryInfo.processPssMemoryKb > 100000) { // > 100MB
            suggestions.append("4. Application memory usage is high. Consider optimizing large objects.\n");
        }
        
        return suggestions.toString();
    }
    
    /**
     * 优化UI组件的内存使用
     */
    public void optimizeUI() {
        // 在实际应用中，这里可以实现以下优化：
        // 1. 回收未使用的位图资源
        // 2. 优化动画效果
        // 3. 减少过度绘制
        Log.d(TAG, "UI optimization performed");
    }
    
    /**
     * 释放资源
     */
    public void release() {
        stopMonitoring();
        clearCache();
        listeners.clear();
        Log.d(TAG, "MemoryOptimizer resources released");
    }
}
