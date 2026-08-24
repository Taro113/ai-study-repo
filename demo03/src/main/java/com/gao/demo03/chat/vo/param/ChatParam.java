package com.gao.demo03.chat.vo.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatParam {

    /**
     * 用户输入信息
     */
    private String message;

    /**
     * 可选：自定义系统提示词
     */
    private String systemPrompt;

    /**
     * 可选：运行时覆盖温度
     */
    private Double temperature;

    /**
     * 可选：运行时覆盖最大 token
     */
    private Integer maxTokens;

}
