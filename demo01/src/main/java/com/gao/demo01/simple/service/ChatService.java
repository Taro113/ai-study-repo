package com.gao.demo01.simple.service;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    // 注入 ChatModel 接口——不是 OpenAiChatModel，体现可移植性
    private final ChatModel chatModel;

    // 构造器注入（推荐方式，便于单元测试）
    public ChatService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    /**
     * 最简单的调用方式：直接传入字符串，返回字符串
     * 适用于简单问答场景，Spring AI 内部自动创建 UserMessage
     */
    public String simpleChat(String userMessage) {
        return chatModel.call(userMessage);
    }

    /**
     * 完整调用方式：使用 Prompt 对象，可以获取完整响应元数据
     * 包括 token 用量、finish reason、模型名称等
     */
    public ChatResponse fullChat(String userMessage) {
        Prompt prompt = new Prompt(userMessage);
        return chatModel.call(prompt);
    }
}
