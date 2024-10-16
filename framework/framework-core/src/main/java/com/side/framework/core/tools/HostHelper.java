package com.side.framework.core.tools;


import com.side.framework.core.constants.CodeEnum;
import com.side.framework.core.exception.BasicException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author: yxfl
 * @date: 2024/1/26 -10
 * @description HostHelper
 */
@Slf4j
public class HostHelper {

    private HostHelper() {
    }

    /**
     * 默认查询公网ip出口的Url
     */
    public static volatile String DefaultPublicIpUrl;

    /**
     * 慢响应的时间间隔 单位：毫秒
     */
    public static Long SlowMilliseconds = 500L;

    public static String cacheIp;

    public static AtomicInteger atomicInteger = new AtomicInteger(10);

    /**
     * 响应超时时间
     */
    public static Long TimeoutMilliseconds = 5000L;

    /**
     * 策略网址 bajiu.cn
     */
    public static String URL_bajiu = "https://bajiu.cn/ip/";

    /**
     * 策略网址 v6r.ipip.net
     */
    public static String URL_v6r_ipip = "https://v6r.ipip.net/?format=callback";

    /**
     * 策略网址 ip.900cha.com
     */
    public static String URL_ip_900cha = "https://ip.900cha.com/";

    /**
     * 本机公网ip出口地址策略集合
     */
    private static final Map<String, Supplier<String>> PUBLIC_IP_TACTICS = new ConcurrentHashMap<>() {{
        put(URL_bajiu, () -> {
            String ip = null;
            String objWebURL = URL_bajiu;
            BufferedReader br = null;
            try {
                URL url = new URL(objWebURL);
                URLConnection urlConnection = url.openConnection();
                urlConnection.setConnectTimeout(TimeoutMilliseconds.intValue());
                urlConnection.setReadTimeout(TimeoutMilliseconds.intValue());
                br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String s = "";
                while ((s = br.readLine()) != null) {
                    if (s.contains("互联网IP")) {
                        ip = s.substring(s.indexOf("'") + 1, s.lastIndexOf("'"));
                        break;
                    }
                }
            } catch (Exception e) {
                log.error("获取本机公网ip失败，使用策略:{}，错误信息:{}", URL_bajiu, e.getMessage());
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (Exception e) {
                        log.error("获取本机公网ip操作异常，使用策略:{}，错误信息:{}", URL_bajiu, e.getMessage());
                    }
                }
            }
            return ip;
        });

        put(URL_v6r_ipip, () -> {
            String ip = null;
            BufferedReader br = null;
            try {
                URL url = new URL(URL_v6r_ipip);
                URLConnection urlConnection = url.openConnection();
                urlConnection.setConnectTimeout(TimeoutMilliseconds.intValue());
                urlConnection.setReadTimeout(TimeoutMilliseconds.intValue());
                br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String s = "";
                StringBuilder sb = new StringBuilder();
                String webContent = "";
                while ((s = br.readLine()) != null) {
                    sb.append(s).append("\r\n");
                }
                webContent = sb.toString();
                int start = webContent.indexOf("(") + 2;
                int end = webContent.indexOf(")") - 1;
                webContent = webContent.substring(start, end);
                ip = webContent;
            } catch (Exception e) {
                log.error("获取本机公网ip失败，使用策略:{}，错误信息:{}", URL_v6r_ipip, e.getMessage());
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (Exception e) {
                        log.error("获取本机公网ip操作异常，使用策略:{}，错误信息:{}", URL_v6r_ipip, e.getMessage());
                    }
                }
            }
            return ip;
        });

        put(URL_ip_900cha, () -> {
            String ip = null;
            String objWebURL = URL_ip_900cha;
            BufferedReader br = null;
            try {
                URL url = new URL(objWebURL);
                URLConnection urlConnection = url.openConnection();
                urlConnection.setConnectTimeout(TimeoutMilliseconds.intValue());
                urlConnection.setReadTimeout(TimeoutMilliseconds.intValue());
                br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String s = "";
                String webContent = "";
                while ((s = br.readLine()) != null) {
                    if (s.contains("我的IP:")) {
                        ip = s.substring(s.indexOf(":") + 1);
                        break;
                    }
                }
            } catch (Exception e) {
                log.error("获取本机公网ip失败，使用策略:{}，错误信息:{}", URL_ip_900cha, e.getMessage());
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        log.error("获取本机公网ip操作异常，使用策略:{}，错误信息:{}", URL_ip_900cha, e.getMessage());
                    }
                }
            }
            return ip;
        });
    }};

    static {
        reloadDefaultUrl();
    }

    /**
     * 选取响应时间最短的
     */
    private static void reloadDefaultUrl() {
        log.info("重置公网ip获取默认策略 。。。");
        StopWatch stopWatch = new StopWatch();
        Map<String, Integer> timeMap = PUBLIC_IP_TACTICS.keySet()
                .stream()
                .collect(Collectors.toMap(key -> key, value -> 0, (key1, key2) -> key2));
        //遍历每个的策略，获取执行耗时
        timeMap.keySet().forEach(key -> {
            stopWatch.reset();
            stopWatch.start();
            String ip = PUBLIC_IP_TACTICS.get(key).get();
            stopWatch.stop();
            long time = stopWatch.getTime(TimeUnit.MILLISECONDS);
            time = StringUtils.isNotEmpty(ip) ? time : TimeoutMilliseconds + 1;
            timeMap.put(key, Long.valueOf(time).intValue());
        });

        //获取耗时最短的策略
        Optional<Map.Entry<String, Integer>> minOpt = timeMap.entrySet()
                .stream()
                .min(Comparator.comparingInt(Map.Entry::getValue));

        if (!Objects.equals(DefaultPublicIpUrl, minOpt.orElse(null))) {
            ReentrantLock lock = new ReentrantLock();
            try {
                if (lock.tryLock(100L, TimeUnit.MILLISECONDS)) {
                    DefaultPublicIpUrl = minOpt.orElseThrow(() -> new BasicException(CodeEnum.HELPER_ERROR)).getKey();
                }
            } catch (InterruptedException e) {
                log.error("获取本机公网ip失败，获取策略耗时超时，使用策略:{}，错误信息:{}", DefaultPublicIpUrl, e.getMessage());
            } finally {
                lock.unlock();
            }
        }
        cacheIp = getCurrentPublicIp();
        log.info("重置公网ip获取策略完成 。。。 使用策略为：{}", minOpt.isPresent() ? minOpt.get().getKey() : "");
    }

    /**
     * 获取本地公网ip出口
     *
     * @return
     */
    public static String getCurrentPublicIp() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String ip = PUBLIC_IP_TACTICS.get(DefaultPublicIpUrl).get();
        stopWatch.stop();
        if (stopWatch.getTime(TimeUnit.MILLISECONDS) > SlowMilliseconds) {
            log.info("默认策略响应时间超过阈值，异步刷新策略。。。");
            CompletableFuture.runAsync(HostHelper::reloadDefaultUrl);
        }
        return ip;
    }

    /**
     * 获取本地公网ip出口--优先缓存
     *
     * @return
     */
    public static String getCachePublicIp() {
        if (atomicInteger.decrementAndGet() < 1) {
            cacheIp = getCurrentPublicIp();
            atomicInteger.setRelease(10);
        }
        return StringUtils.isNotEmpty(cacheIp) ? cacheIp : getCurrentPublicIp();
    }

}
