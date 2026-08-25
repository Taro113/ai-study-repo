package com.gao.demo06.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

import java.util.List;

@Slf4j
@Configuration
public class ChatClientWithAdvisorsConfig {

    @Bean
    public ChatClient advisedChatClient(
            ChatClient.Builder builder,
            ChatMemory chatMemory) {  // ChatMemory 由 Spring AI 提供

        return builder
                .defaultSystem("你是一个记得上下文的智能助手。")
                .defaultAdvisors(
                        // 自动管理多轮对话记忆（将历史消息注入 Prompt）
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        // 自定义 Redis 记录
                        new RefillRedisAdvisor(),
                        // 记录所有请求和响应的日志（开发调试很有用）
                        new SimpleLoggerAdvisor()
                )
                .build();
    }

    //  Redis 获取
    @Bean
    public ChatMemory chatMemory() {
        return new ChatMemory() {
            @Override
            public void add(@NonNull String conversationId, @NonNull List<Message> messages) {
                log.info("add, conversationId = {}, message = {}", conversationId, messages);
            }

            @Override
            public List<Message> get(@NonNull String conversationId) {
                log.info("get, conversationId = {}", conversationId);
                return List.of();
            }

            @Override
            public void clear(@NonNull String conversationId) {
                log.info("clear, conversationId = {}", conversationId);
            }
        };
    }
}
