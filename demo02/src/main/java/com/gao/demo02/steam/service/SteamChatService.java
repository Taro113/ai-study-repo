package com.gao.demo02.steam.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;

@Service
public class SteamChatService {

    // 注入具体实现以获得流式能力（或通过 ChatClient 更优雅地处理）
    private final ChatModel chatModel;

    public SteamChatService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    /**
     * 流式调用示例
     * StreamingChatModel 接口的 stream() 方法返回 Flux<ChatResponse>
     * 每个 ChatResponse 包含一个 token 片段（非完整响应）
     */
    public Flux<String> streamChat(String message) {
        assert chatModel != null;
        return chatModel.stream(new Prompt(message))
                .map(chatResponse -> {
                    // 每次 onNext 包含一小段文本（通常是 1-3 个 token）
                    String token = chatResponse.getResult().getOutput().getText();
                    return token != null ? token : "";
                })
                .filter(token -> !token.isEmpty());
    }

    /**
     * 流式调用示例（使用 ChatClient）
     * ChatClient 的 stream() 方法返回 Flux<ChatResponse>，
     * 每个 ChatResponse 包含一个 token 片段（非完整响应）。
     * 若底层模型不支持流式，会自动降级为单次完整响应。
     */
    public Flux<String> streamClientChat(String message) {
        // 这一步可通过配置类注入，当前这样写只为了测试
        ChatClient chatClient = ChatClient.builder(chatModel).build();
        return chatClient.prompt()          // 1. 开始构建一个提示请求
                .user(message)              // 2. 设置用户消息
                .stream()                   // 3. 发起流式调用
                .content();                 // 4. 直接提取内容为 Flux<String>
    }
}

