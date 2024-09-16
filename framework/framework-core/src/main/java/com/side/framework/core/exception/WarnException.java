package com.side.framework.core.exception;

import lombok.Getter;

/**
 * 警告异常
 *
 * @author yxfl
 * @date 2024/05/03 12
 **/

public class WarnException extends BasicException {
    private static final long serialVersionUID = 1059192204758083629L;

    private Enum<WarnLevel> level;

    private Integer warnCode;

    private String warnMsg;

    public WarnException(BasicException e, Enum<WarnLevel> warnLevel, Integer warnCode, String warnMsg) {
        super(e.getErrorCode(), e.getMessage());
        this.level = warnLevel;
        this.warnCode = warnCode;
        this.warnMsg = warnMsg;
    }

    @Getter
    public enum WarnLevel {
        WARN_LEVEL_LOG(1, "日志级别"),
        WARN_LEVEL_CONFLICT(2, "冲突级别"),
        WARN_LEVEL_WARN(3, "错误级别"),
        WARN_LEVEL_ERROR(4, "告警级别"),
        ;

        private Integer level;
        private String description;

        WarnLevel(Integer level, String description) {
            this.level = level;
            this.description = description;
        }
    }


}
