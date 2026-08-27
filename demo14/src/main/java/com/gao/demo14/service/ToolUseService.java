package com.gao.demo14.service;

import com.gao.demo14.tools.WeatherTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class ToolUseService {

    private final ChatClient chatClient;

    public ToolUseService(
            ChatClient.Builder builder,
            WeatherTools weatherTools) {

        this.chatClient = builder
                .defaultSystem("你是一个智能业务助手，可以查天气。请根据问题灵活组合使用工具，给出完整的回答。")
                .defaultTools(weatherTools)
                .build();
    }

    public String answer(String question) {
        return chatClient.prompt()
                .user(question)
                .call()
                .content();
    }
}
