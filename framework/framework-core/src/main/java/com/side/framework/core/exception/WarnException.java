package com.side.framework.core.exception;

import lombok.Getter;

import java.io.Serial;

/**
 * 警告异常
 *
 * @author yxfl
 * @date 2024/05/03 12
 **/
@Getter
public class WarnException extends BasicException {
    @Serial
    private static final long serialVersionUID = 1059192204758083629L;

    private final Enum<WarnLevel> level;

    private final Integer warnCode;

    private final String warnMsg;

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

        private final Integer level;
        private final String description;

        WarnLevel(Integer level, String description) {
            this.level = level;
            this.description = description;
        }
    }


}
