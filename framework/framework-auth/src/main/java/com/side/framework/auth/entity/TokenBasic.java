package com.side.framework.auth.entity;

import com.side.framework.auth.constants.TokenModeTypeEnum;
import com.side.framework.auth.constants.TokenSourceEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author: yxfl
 * @date: 2024/1/19 -10
 * @description TokenBasic
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenBasic implements Serializable {

    @Serial
    private static final long serialVersionUID = 2402426107666579720L;

    /**
     * 短期token
     */
    private String accessToken;

    /**
     * 长期token
     */
    private String refreshToken;

    /**
     * 参考过期时间
     */
    private long expireTime;

    /**
     * token类型
     *
     * @see TokenModeTypeEnum
     */
    private String modelType;

    /**
     * 权限范围
     *
     * @see TokenSourceEnum
     */
    private String sourceType;
}
