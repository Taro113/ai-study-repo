package com.gao.demo32.common;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {
    /**
     * 会话 ID
     */
    private String sessionId;

    /**
     * 用户当前输入
     */
    @NotBlank
    private String message;

    /**
     * 指定的任务执行策略
     */
    private Integer taskStrategy;
}
