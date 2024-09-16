package com.side.framework.core.domain;

/**
 * id生成器抽象接口
 *
 * @author yxfl
 * @date 2024/08/31 19
 **/
public interface IdMaker extends Comparable<IdMaker> {
    /**
     * 获取下一个id
     *
     * @return
     */
    Object nextId();

    /**
     * 是否支持验证
     *
     * @return
     */
    Boolean isSupportVerify();

    /**
     * 获取id类型
     *
     * @return
     */
    Class<?> getIdType();
}
