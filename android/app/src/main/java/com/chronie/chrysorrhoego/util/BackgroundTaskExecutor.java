package com.chronie.chrysorrhoego.util;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 后台任务执行器
 * 优化的线程池实现，用于处理各种异步任务
 */
public class BackgroundTaskExecutor {
    private static final String TAG = "BackgroundTaskExecutor";
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    private static final int MAX_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE_SECONDS = 30;
    
    private static final BackgroundTaskExecutor INSTANCE = new BackgroundTaskExecutor();
    
    private final ExecutorService networkExecutor;
    private final ExecutorService computationExecutor;
    private final ExecutorService singleExecutor;
    private final Handler mainThreadHandler;
    
    // 线程工厂，用于创建命名线程
    private static final ThreadFactory threadFactory = new ThreadFactory() {
        private final AtomicInteger counter = new AtomicInteger(1);
        
        @Override
        public Thread newThread(@NonNull Runnable r) {
            Thread thread = new Thread(r, "BackgroundThread-" + counter.getAndIncrement());
            thread.setPriority(Thread.NORM_PRIORITY - 1); // 降低优先级，避免影响UI线程
            return thread;
        }
    };
    
    private BackgroundTaskExecutor() {
        // 创建线程队列
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(128);
        
        // 创建线程池
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_SECONDS,
                TimeUnit.SECONDS,
                workQueue,
                threadFactory
        );
        
        // 允许核心线程超时，在空闲时回收
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        
        // 初始化各种执行器
        networkExecutor = threadPoolExecutor;
        computationExecutor = Executors.newFixedThreadPool(Math.max(1, CPU_COUNT / 2));
        singleExecutor = Executors.newSingleThreadExecutor(threadFactory);
        
        // 主线程Handler
        mainThreadHandler = new Handler(Looper.getMainLooper());
        
        Log.d(TAG, "BackgroundTaskExecutor initialized with " + CPU_COUNT + " CPU cores");
    }
    
    /**
     * 获取单例实例
     */
    public static BackgroundTaskExecutor getInstance() {
        return INSTANCE;
    }
    
    /**
     * 在网络线程池执行任务
     */
    public void executeOnNetworkThread(Runnable runnable) {
        networkExecutor.execute(wrapRunnable(runnable, "NetworkTask"));
    }
    
    /**
     * 在计算线程池执行任务
     */
    public void executeOnComputationThread(Runnable runnable) {
        computationExecutor.execute(wrapRunnable(runnable, "ComputationTask"));
    }
    
    /**
     * 在单线程执行器中执行任务（保证顺序）
     */
    public void executeOnSingleThread(Runnable runnable) {
        singleExecutor.execute(wrapRunnable(runnable, "SingleThreadTask"));
    }
    
    /**
     * 在主线程执行任务
     */
    public void executeOnMainThread(Runnable runnable) {
        if (isMainThread()) {
            runnable.run();
        } else {
            mainThreadHandler.post(runnable);
        }
    }
    
    /**
     * 在主线程延迟执行任务
     */
    public void executeOnMainThreadDelayed(Runnable runnable, long delayMillis) {
        mainThreadHandler.postDelayed(runnable, delayMillis);
    }
    
    /**
     * 检查当前是否在主线程
     */
    public boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }
    
    /**
     * 包装Runnable，添加错误处理和性能监控
     */
    private Runnable wrapRunnable(Runnable runnable, String taskType) {
        return () -> {
            String taskName = taskType + ":" + runnable.getClass().getSimpleName();
            PerformanceMonitor.getInstance().startMethodMonitoring(taskName);
            
            try {
                runnable.run();
            } catch (Exception e) {
                Log.e(TAG, "Error executing background task: " + e.getMessage(), e);
            } finally {
                long executionTime = PerformanceMonitor.getInstance().endMethodMonitoring(taskName);
                
                // 记录长时间运行的任务
                if (executionTime > 200) { // 200ms
                    Log.d(TAG, "Long running task: " + taskName + " took " + executionTime + "ms");
                }
            }
        };
    }
    
    /**
     * 关闭所有执行器（仅在应用退出时调用）
     */
    public void shutdown() {
        try {
            networkExecutor.shutdown();
            computationExecutor.shutdown();
            singleExecutor.shutdown();
            
            if (!networkExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                networkExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Log.e(TAG, "Error shutting down executors: " + e.getMessage());
        }
    }
    
    /**
     * 获取执行器状态
     */
    public String getStatus() {
        StringBuilder status = new StringBuilder();
        status.append("BackgroundTaskExecutor Status:\n");
        status.append("- CPU Count: " + CPU_COUNT + "\n");
        status.append("- Core Pool Size: " + CORE_POOL_SIZE + "\n");
        status.append("- Max Pool Size: " + MAX_POOL_SIZE + "\n");
        
        if (networkExecutor instanceof ThreadPoolExecutor) {
            ThreadPoolExecutor executor = (ThreadPoolExecutor) networkExecutor;
            status.append("- Active Threads: " + executor.getActiveCount() + "\n");
            status.append("- Queue Size: " + executor.getQueue().size() + "\n");
        }
        
        return status.toString();
    }
}