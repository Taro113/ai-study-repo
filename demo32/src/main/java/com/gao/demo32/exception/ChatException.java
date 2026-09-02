package com.gao.demo32.exception;

import lombok.Getter;

/**
 * AI 服务自定义异常，继承 RuntimeException 为非受检异常，
 * 抛出后无需业务层显式处理，由全局异常处理器统一返回前端。
 */
@Getter
public class ChatException extends RuntimeException {

    // 可选：错误码，便于前端识别
    private final String errorCode;

    public ChatException(String message) {
        super(message);
        this.errorCode = "AI_ERR";
    }

    public ChatException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "AI_ERR";
    }

    public ChatException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ChatException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
