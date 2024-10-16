package com.side.framework.auth.constants;

import lombok.Getter;

/**
 * 内置token模式枚举
 *
 * @author LGJN
 * @date 2024/09/16 17
 **/
@Getter
public enum TokenModeTypeEnum {

    refresh("refresh", "刷新模式"),
    renewal("renewal", "续期模式"),
    accredit("accredit", "授权模式"),
    one("oneTime", "一次性模式"),
    unknown("unknown", "未知模式");

    private final String modelType;
    private final String modelDesc;

    TokenModeTypeEnum(String modelType, String modelDesc) {
        this.modelType = modelType;
        this.modelDesc = modelDesc;
    }

    /**
     * 根据modelType获取枚举值
     *
     * @param modelType
     * @return
     */
    public static TokenModeTypeEnum getByModelType(String modelType) {
        for (TokenModeTypeEnum tokenModeTypeEnum : TokenModeTypeEnum.values()) {
            if (tokenModeTypeEnum.getModelType().equalsIgnoreCase(modelType)) {
                return tokenModeTypeEnum;
            }
        }
        return TokenModeTypeEnum.unknown;
    }
}
