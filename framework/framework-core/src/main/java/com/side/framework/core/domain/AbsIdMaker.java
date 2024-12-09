package com.side.framework.core.domain;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author yxfl
 * @date 2024/08/31 19
 **/
@Slf4j
public abstract class AbsIdMaker implements IdMaker {


    private static final int BENCH_SIZE = 100000; //批量生成id的数量 10万


    /**
     * 验证id是否可用
     *
     * @param id
     * @return
     */
    abstract boolean verifyId(String id);

    /**
     * 生成批量id所需要的时间
     *
     * @return
     */
    long generateBatchIdTime() {  
        StopWatch stopWatch = new StopWatch();
        for (int i = 0; i < BENCH_SIZE; i++) {
            this.nextId();
        }
        stopWatch.stop();
        return stopWatch.getTime(TimeUnit.MILLISECONDS);
    }


    @Override
    public int compareTo(IdMaker maker) {
        if (Objects.isNull(maker) || !(maker instanceof AbsIdMaker absIdMaker)) {
            return 0;
        }
        return Long.compare(this.generateBatchIdTime(), absIdMaker.generateBatchIdTime());
    }
}
