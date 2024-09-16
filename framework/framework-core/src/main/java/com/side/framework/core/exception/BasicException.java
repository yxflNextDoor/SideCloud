package com.side.framework.core.exception;


import com.side.framework.core.constants.CodeCustomInterface;

/**
 * @param :
 * @author : yxfl
 * @date : 2022/5/1
 * @description :
 */
public class BasicException extends RuntimeException {
    private static final long serialVersionUID = 6782259839208412575L;
    private Integer errorCode;
    private String errorMsg;

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

    public Integer getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
