package com.gao.demo32.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    /**
     * 会话 ID
     */
    private String sessionId;

    /**
     * 响应数据
     */
    private String content;

    /**
     * 时间戳
     */
    private Long timestamp;
}
