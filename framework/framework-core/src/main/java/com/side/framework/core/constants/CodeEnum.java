package com.side.framework.core.constants;

/**
 * 用枚举的方式定义http返回码和返回信息
 *
 * @author yxfl
 */
public enum CodeEnum implements CodeCustomInterface {

    // 系统级错误

    /**
     * 服务器初始化错误
     */
    SERVER_LOADING_ERROR(101, "server loading error"),

    /**
     * 服务器助手初始化错误
     */
    SERVER_HELPER_INIT_ERROR(102, "server helper init error"),

    // 业务级错误

    /**
     * 成功
     */
    OK(200, "OK"),
    /**
     * 新增或更新操作成功
     */
    CREATE_OK(201, "save ok"),
    /**
     * 业务操作失败
     */
    BUSINESS_FAIL(202, "business fail"),
    /**
     * 重定向
     */
    REDIRECT(203, "redirect"),

    /**
     * 参数错误
     */
    PARAM_ERROR(400, "param error"),
    /**
     * 未找到
     */
    NOT_FOUND(404, "not found"),

    //服务内捕获异常

    /**
     * 服务器内部错误
     */
    SERVER_ERROR(500, "server error"),

    /**
     * 异常来源
     */
    ABNORMAL_SOURCE(508, "abnormal source"),

    /**
     * 请求过于频繁
     */
    REQUEST_TOO_FAST(509, "request too fast"),

    /**
     * 请求重放攻击
     */
    REPLAY_ATTACK(510, "replay attack"),

    //文件错误

    /**
     * 文件下载错误（输出到前端时发生错误）
     */
    WRITE_FILE_ERROR(531, "write file error"),
    /**
     * 文件上传错误
     */
    UPLOAD_FILE_ERROR(532, "upload file error"),
    /**
     * 文件搜索过程未查询到
     */
    NOT_FOUNT_FILE(533, "not fount file"),


    /**
     * 工具类异常
     */
    HELPER_ERROR(701, "helper error"),

    /**
     * 短信发送失败
     */
    SMS_FAIL(801, "sms fail"),

    ;

    /**
     * 返回码
     */
    private Integer code;
    /**
     * 返回信息
     */
    private String msg;

    CodeEnum(Integer code, String msg) {
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
