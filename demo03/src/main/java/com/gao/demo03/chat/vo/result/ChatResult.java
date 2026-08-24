package com.gao.demo03.chat.vo.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResult {

    /**
     * 返回信息
     */
    private String content;

    /**
     * 输入 token 数
     */
    private long promptTokens;

    /**
     * 输出 token 数
     */
    private long completionTokens;

    /**
     * 结束原因
     */
    private String finishReason;
}
