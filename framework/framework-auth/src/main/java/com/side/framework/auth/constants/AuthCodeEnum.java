package com.side.framework.auth.constants;

import com.side.framework.core.constants.CodeCustomInterface;

/**
 * 认证和鉴权enum
 *
 * @author LGJN
 * @date 2024/09/16 21
 **/
public enum AuthCodeEnum implements CodeCustomInterface {

    // 认证和鉴权相关枚举

    /**
     * 未认证
     */
    NOT_AUTHENTICATED(501, "not authenticated"),

    /**
     * 权限不足
     */
    ACCESS_DENIED(502, "access denied"),

    /**
     * 登录失败
     */
    LOGIN_FAIL(601, "login failure"),

    /**
     * 登录验证码不匹配
     */
    LOGIN_CAPTCHA_NOT_MATCH(602, "login captcha not match"),

    /**
     * 登录验证码过期或不存在
     */
    LOGIN_CAPTCHA_EXPIRE(603, "login captcha expired or does not exist"),

    /**
     * 登录并发错误（同时发起登录）
     */
    LOGIN_CONCURRENT_FAIL(604, "Frequent login"),

    /**
     * 登录环境异常
     */
    LOGIN_ENVIRONMENT_FAIL(605, "Abnormal login environment"),

    /**
     * 登录模型不支持
     */
    LOGIN_MODEL_NOT_SUPPORT(606, "Login model not support"),

    /**
     * 登录信息刷新错误
     */
    LOGIN_REFRESH_FAIL(607, "login refresh fail"),
    ;

    /**
     * 返回码
     */
    private final Integer code;
    /**
     * 返回信息
     */
    private final String msg;

    AuthCodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }


    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
