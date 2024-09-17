package com.side.framework.auth.constants;

import lombok.Getter;

/**
 * 内置token请求来源标识枚举
 *
 * @author LGJN
 * @date 2024/09/16 17
 **/
@Getter
public enum TokenSourceEnum {

    // 以下来源为系统内置
    admin("admin", "后台管理"),
    web("web", "web端"),
    app("app", "app端"),
    wechat("wechat", "微信端"),

    // 以下来源为第三方授权给我们使用
    qq("qq", "QQ端"),
    alipay("alipay", "支付宝端"),
    dingtalk("dingtalk", "钉钉端"),
    wecom("wecom", "企业微信端"),
    github("github", "GitHub登录"),

    // 我们授权给第三方使用
    oauth2("oauth2", "授权码模式"),
    unknown("unknown", "未知来源"),
    ;


    private String sourceType;

    private String description;

    TokenSourceEnum(String sourceType, String description) {
        this.sourceType = sourceType;
        this.description = description;
    }

    /**
     * 根据scope获取枚举值
     *
     * @param sourceType
     * @return
     */
    public TokenSourceEnum getBySourceType(String sourceType) {
        for (TokenSourceEnum sourceEnum : TokenSourceEnum.values()) {
            if (sourceEnum.getSourceType().equalsIgnoreCase(sourceType)) {
                return sourceEnum;
            }
        }
        return TokenSourceEnum.unknown;
    }
}
