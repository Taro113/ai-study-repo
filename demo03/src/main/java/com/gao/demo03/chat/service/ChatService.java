package com.gao.demo03.chat.service;

import com.gao.demo03.chat.vo.param.ChatParam;
import com.gao.demo03.chat.vo.result.ChatResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ChatService {

    private final ChatModel chatModel;

    public ChatService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public ChatResult chat(ChatParam request) {
        log.info("收到聊天请求，消息长度: {}", request.getMessage().length());

        // 构建消息列表
        List<Message> messages = new ArrayList<>();

        // 添加系统提示词（如果有）
        if (request.getSystemPrompt() != null && !request.getSystemPrompt().isBlank()) {
            messages.add(new SystemMessage(request.getSystemPrompt()));
        }
        messages.add(new UserMessage(request.getMessage()));

        // 构建运行时 ChatOptions（仅在请求中有覆盖值时生效）
        ChatOptions options = ChatOptions.builder()
                .temperature(request.getTemperature() != null ? request.getTemperature() : 0.7)
                .maxTokens(request.getMaxTokens() != null ? request.getMaxTokens() : 1000)
                .build();

        try {
            long startTime = System.currentTimeMillis();
            log.info("请求开始，message: {}, options: {}", messages, options);
            ChatResponse response = chatModel.call(new Prompt(messages, options));
            long elapsed = System.currentTimeMillis() - startTime;

            var usage = response.getMetadata().getUsage();
            log.info("请求完成，耗时: {}ms，Token 用量: {}", elapsed, usage.getTotalTokens());

            return new ChatResult(
                    response.getResult().getOutput().getText(),
                    usage.getPromptTokens(),
                    usage.getCompletionTokens(),
                    response.getResult().getMetadata().getFinishReason()
            );
        } catch (Exception e) {
            log.error("ChatModel 调用失败: {}", e.getMessage(), e);
            throw new RuntimeException("AI 服务暂时不可用，请稍后重试", e);
        }
    }
}
