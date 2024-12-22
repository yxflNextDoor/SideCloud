package com.side.framework.spring.config;

import cn.hippo4j.core.executor.DynamicThreadPool;
import cn.hippo4j.core.executor.support.ThreadPoolBuilder;
import com.side.framework.core.constants.CoreConstant;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAspectJAutoProxy
public class DefaultConfig {

    @Bean()
    @DynamicThreadPool
    public ThreadPoolExecutor messageConsumeDynamicExecutor() {
        BasicThreadFactory.Builder builder = new BasicThreadFactory.Builder();
        return ThreadPoolBuilder.builder()
                .threadFactory(builder
                        .namingPattern(CoreConstant.THREAD_POOL_NAME_COMMON)
                        .daemon(false)
                        .namingPattern("").build())
                .threadPoolId(CoreConstant.THREAD_POOL_NAME_COMMON)
                .dynamicPool()
                .build();
    }

}
