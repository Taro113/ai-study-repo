package com.gao.demo01.simple.controller;

import com.gao.demo01.simple.service.ChatService;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * 简单问答接口
     * GET /api/chat/simple?message=你好
     */
    @GetMapping("/simple")
    public Map<String, String> simpleChat(
            @RequestParam(defaultValue = "你好，介绍一下 Spring AI") String message) {
        String response = chatService.simpleChat(message);
        return Map.of("answer", response);
    }

    /**
     * 完整响应接口，含 token 用量
     * GET /api/chat/full?message=你好
     */
    @GetMapping("/full")
    public Map<String, Object> fullChat(
            @RequestParam(defaultValue = "用一句话介绍 Spring AI") String message) {
        ChatResponse response = chatService.fullChat(message);

        // 从响应中提取文本内容和元数据
        String content = response.getResult().getOutput().getText();
        var usage = response.getMetadata().getUsage();

        assert content != null;
        return Map.of(
                "answer", content,
                "promptTokens", usage.getPromptTokens(),
                "completionTokens", usage.getCompletionTokens(),
                "totalTokens", usage.getTotalTokens()
        );
    }
}

