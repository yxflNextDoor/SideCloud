package com.side.framework.core.tools;


import com.side.framework.core.constants.CodeCustomInterface;
import com.side.framework.core.constants.CodeEnum;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Objects;

/**
 * @athor: yxfl
 * @date: 2022/5/1
 * @param:
 * @description:
 */
@Data
@ToString
public class ApiResult<T> implements Serializable {

    private Integer apiCode;
    private String apiMsg;
    private T data;
    private Long timestamp;

    /**
     * 使用默认构造方法，不对外暴露
     */
    protected ApiResult() {
    }

    /**
     * 使用customInterface枚举和数据、时间戳构建ApiResult实例
     *
     * @param customInterface 包含HTTP状态码和消息的枚举
     * @param data            返回的具体业务数据
     * @param timestamp       时间戳
     */
    protected ApiResult(CodeCustomInterface customInterface, T data, Long timestamp) {
        this.apiCode = customInterface.getCode();
        this.apiMsg = customInterface.getMsg();
        this.data = data;
        this.timestamp = timestamp;
    }

    /**
     * 使用自定义通用状态码、消息、数据、时间戳构建ApiResult实例
     *
     * @param apiCode   HTTP状态码
     * @param apiMsg    HTTP状态消息
     * @param data      返回的具体业务数据
     * @param timestamp 时间戳
     */
    protected ApiResult(Integer apiCode, String apiMsg, T data, Long timestamp) {
        this.apiCode = apiCode;
        this.apiMsg = apiMsg;
        this.data = data;
        this.timestamp = timestamp;
    }

    /**
     * 成功构建
     *
     * @return
     */
    public static ApiResult<?> of() {
        return new ApiResult<>(CodeEnum.OK, null, System.currentTimeMillis());
    }

    /**
     * 成功构建
     *
     * @param data
     * @return
     */
    public static ApiResult<?> of(Object data) {
        return new ApiResult<>(CodeEnum.OK, data, System.currentTimeMillis());
    }

    /**
     * 手动构建
     *
     * @param customInterface
     * @return
     */
    public static ApiResult<?> build(CodeCustomInterface customInterface) {
        return new ApiResult<>(customInterface, null, System.currentTimeMillis());
    }

    /**
     * 手动构建
     *
     * @param apiCode
     * @param apiMsg
     * @param data
     * @return
     */
    public static ApiResult<?> build(Integer apiCode, String apiMsg, Object data) {
        return new ApiResult<>(apiCode, apiMsg, data, System.currentTimeMillis());
    }

    /**
     * 错误的返回，直接构建
     *
     * @return
     */
    public static ApiResult<?> error() {
        return new ApiResult<>(CodeEnum.SERVER_ERROR, null, System.currentTimeMillis());
    }

    /**
     * 错误的返回，直接构建
     *
     * @return
     */
    public static ApiResult<?> error(String httpMsg) {
        return new ApiResult<>(CodeEnum.SERVER_ERROR.getCode(), StringUtils.isBlank(httpMsg) ? CodeEnum.SERVER_ERROR.getMsg() : httpMsg, null, System.currentTimeMillis());
    }

    /**
     * 判断是否成功
     *
     * @param apiResult
     * @return
     */
    public static Boolean isSuccess(ApiResult<?> apiResult) {
        if (Objects.isNull(apiResult)) {
            return false;
        }
        return Objects.equals(CodeEnum.OK.getCode(), apiResult.getApiCode());
    }
}
