package com.gao.demo02.steam.controller;

import com.gao.demo02.steam.service.SteamChatService;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/chat")
public class SteamChatController {

    @Resource
    private SteamChatService chatService;

    /**
     * SSE 流式接口 — 前端使用 EventSource 或 fetch + ReadableStream 接收
     * GET /api/chat/stream?message=写一首关于 Spring AI 的诗
     * <p>
     * produces = text/event-stream 是 SSE 的 MIME 类型
     */
    @CrossOrigin(origins = "*")
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat(
            @RequestParam(defaultValue = "你好，请介绍一下你自己") String message) {
        return chatService.streamChat(message);
    }

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/streamV2", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChatV2(
            @RequestParam(defaultValue = "你好，请介绍一下你自己") String message) {
        return chatService.streamClientChat(message);
    }

}
