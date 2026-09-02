package com.gao.demo32.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ChatExceptionHandler {

    @ExceptionHandler(ChatException.class)
    public ResponseEntity<Map<String, Object>> handleChatException(ChatException e) {
        log.error("ChatException occurred | errorCode={}, message={}", e.getErrorCode(), e.getMessage(), e);
        Map<String, Object> error = new HashMap<>();
        error.put("code", e.getErrorCode());
        error.put("message", e.getMessage());
        error.put("timestamp", System.currentTimeMillis());
        // 根据错误码决定 HTTP 状态，可自定义
        HttpStatus status = "AI_ERR".equals(e.getErrorCode()) ? HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception e) {
        log.error("Unexpected exception", e);
        Map<String, Object> error = new HashMap<>();
        error.put("code", "SYSTEM_ERR");
        error.put("message", "系统内部错误，请稍后重试");
        error.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
