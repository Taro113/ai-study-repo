package com.gao.demo08.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    /**
     * 创建带默认配置的 ChatClient Bean
     * ChatClient.Builder 由 Spring AI Auto-Configuration 自动提供
     */
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
                // 设置默认系统提示词（所有通过此 ChatClient 发出的请求都会附带）
                .defaultSystem("""
                        你是一位友善、专业的 AI 助手，名叫 Aria。
                        始终用简洁清晰的语言回答，避免冗长的废话。
                        如果不确定答案，请明确说明不知道，不要瞎编。
                        """)
                // 设置默认 ChatOptions（可在调用时覆盖）
                .defaultOptions(ChatOptions.builder()
                        .temperature(0.7)
                        .maxTokens(1000)
                        .build())
                .build();
    }
}

