package com.side.framework.core.exception;


import com.side.framework.core.constants.CodeCustomInterface;
import lombok.Getter;

import java.io.Serial;

/**
 * @author : yxfl
 * @date : 2022/5/1
 * {@code @description} :
 */
@Getter
public class BasicException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 6782259839208412575L;
    private final Integer errorCode;
    private final String errorMsg;

    public BasicException(Integer errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public BasicException(CodeCustomInterface codeCustomInterface) {
        this.errorCode = codeCustomInterface.getCode();
        this.errorMsg = codeCustomInterface.getMsg();
    }

    public BasicException(CodeCustomInterface codeCustomInterface, String errorMsg) {
        this.errorCode = codeCustomInterface.getCode();
        this.errorMsg = errorMsg;
    }

}
