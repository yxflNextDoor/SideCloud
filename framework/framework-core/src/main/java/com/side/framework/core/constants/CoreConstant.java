package com.side.framework.core.constants;

/**
 * 公用常量集合
 *
 * @author yxfl
 * @date 2023/12/09 11
 **/
public class CoreConstant {
    private CoreConstant() {
    }

    /**
     * 运行环境标识：正式环境（生产环境）
     */
    public final static String ACTIVE_PROD = "prod";

    /**
     * 运行环境标识：测试环境
     */
    public final static String ACTIVE_UAT = "uat";

    /**
     * 运行环境标识：开发环境
     */
    public final static String ACTIVE_DEV = "dev";

    /**
     * 短链key
     */
    public final static String SHORT_LINK_KEY = "short_link:";

    /**
     * 默认分割符号
     */
    public final static String DEFAULT_SPLIT_SYMBOL = ":";

    /**
     * 默认日期格式
     */
    public final static String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 默认时区
     */
    public final static String TIME_ZONE = "GMT+8";

    /**
     * 默认编码
     */
    public final static String DEFAULT_CHARSET = "UTF-8";

    /**
     * 默认查询一条结尾语句
     */
    public final static String SQL_LIMIT_ONE = "limit 1";

    /**
     * 线程池名称：通用线程池
     */
    public final static String THREAD_POOL_NAME_COMMON = "common-thread-pool";

}
