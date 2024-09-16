package com.side.framework.core.tools;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * @author: yxfl
 * @date: 2022/11/4 -14
 * @description ThreadHelper
 */
@Slf4j
public class ThreadHelper {
    private ThreadHelper() {
    }

    private static final ThreadLocal<Map<String, Object>> defaultThreadLocal = new InheritableThreadLocal<>();

    /**
     * 默认线程池
     */
    private static final ThreadPoolExecutor threadPoolExecutor;

    /**
     * 轻量化的线程池，重写拒绝策略
     */
    private static final ThreadPoolExecutor minThreadPoolExecutor;

    static {
        Runtime runtime = Runtime.getRuntime();
        int processors = runtime.availableProcessors();
        threadPoolExecutor = new ThreadPoolExecutor(processors,
                processors * 2,
                60L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(64),
                Executors.defaultThreadFactory(), ((r, e) -> {
            //参考CallerRunsPolicy
            if (!e.isShutdown()) {
                r.run();
            }
        }));
        // 预创建线程池
        threadPoolExecutor.prestartAllCoreThreads();


        minThreadPoolExecutor = new ThreadPoolExecutor(1,
                processors,
                0L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(processors),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.DiscardOldestPolicy()
        );
    }

    /**
     * 获取线程池
     *
     * @return
     */
    public static ThreadPoolExecutor getInstance() {
        return threadPoolExecutor;
    }

    /**
     * 获取轻量化线程池
     * @return
     */
    public static ThreadPoolExecutor getMinThreadPoolExecutor() {
        return minThreadPoolExecutor;
    }

    /**
     * 当前线程设置value
     *
     * @param key
     * @param value
     */
    public static void setValue(String key, Object value) {
        Map<String, Object> map = defaultThreadLocal.get();
        if (Objects.isNull(map)) {
            map = new ConcurrentHashMap<>();
        }
        map.put(key, value);
        defaultThreadLocal.set(map);
    }


    /**
     * 根据key从当前线程获取值
     *
     * @param key
     * @return
     */
    public static Object getValue(String key) {
        Map<String, Object> map = defaultThreadLocal.get();
        if (Objects.isNull(map)) {
            defaultThreadLocal.set(new HashMap<>());
            return null;
        }
        return map.getOrDefault(key, null);
    }

    /**
     * 移除键值对
     *
     * @param key
     * @return
     */
    public static Object remove(String key) {
        Map<String, Object> map = defaultThreadLocal.get();
        return map.remove(key);
    }

    /**
     * 获取当前线程的值，如果不存在则设置默认值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public static Object getValueOrSet(String key, Object defaultValue) {
        Object value = getValue(key);
        if (Objects.nonNull(value)) {
            return value;
        }
        setValue(key, defaultValue);
        return defaultValue;
    }

    /**
     * 获取当前线程的值，如果不存在则设置默认值
     *
     * @param key
     * @param objectSupplier
     * @return
     */
    public static Object getValueOrSet(String key, Supplier<Object> objectSupplier) {
        Object value = getValue(key);
        if (Objects.nonNull(value)) {
            return value;
        }
        setValue(key, objectSupplier.get());
        return objectSupplier.get();
    }

    /**
     * 获取当前运行环境下建议的线程池最大线程数
     *
     * @return
     */
    public static Integer getSuggestionMaxThreadSize() {
        Runtime runtime = Runtime.getRuntime();
        //可调度的核心数量
        int processors = runtime.availableProcessors();
        //通常一个核心支持双线程（酷睿小核心除外）
        return processors * 2;
    }
}
