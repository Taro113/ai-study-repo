package com.gao.demo05.client.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ChatClientService {

    private final ChatClient chatClient;

    public ChatClientService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * 最简单的 ChatClient 调用
     * .call().content() 直接返回文本字符串
     */
    public String simpleCall(String userMessage) {
        return chatClient
                .prompt()
                .user(userMessage)
                .call()
                .content();  // 等价于 response.getResult().getOutput().getText()
    }

    /**
     * 完整的 ChatClient 链式调用
     * 展示所有可用的配置项
     */
    public String fullChainCall(String userMessage, String contextInfo) {
        return chatClient
                .prompt()
                // 在调用时动态覆盖默认系统提示词
                .system(sp -> sp
                        .text("你是专业的 {domain} 顾问，背景信息：{context}")
                        .param("domain", "Java 技术")
                        .param("context", contextInfo))
                // 用户消息，支持模板变量
                .user(u -> u
                        .text("请回答：{question}")
                        .param("question", userMessage))
                // 添加运行时 Options（覆盖默认值）
                .options(ChatOptions.builder()
                        .temperature(0.5)
                        .build())
                .call()
                .content();
    }

    /**
     * ChatClient 流式调用
     * 返回 Flux<String>，每个元素是一个 token
     */
    public Flux<String> streamCall(String userMessage) {
        return chatClient
                .prompt()
                .user(userMessage)
                .stream()           // 切换到流式模式
                .content();         // 返回 Flux<String>
    }

    /**
     * 使用 ChatClient 获取完整 ChatResponse（需要元数据时）
     */
    public ChatResponse callWithMetadata(String userMessage) {
        return chatClient
                .prompt()
                .user(userMessage)
                .call()
                .chatResponse();    // 返回完整 ChatResponse 对象
    }
}

