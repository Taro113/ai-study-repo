package com.gao.demo32.controller;

import com.gao.demo32.common.ChatRequest;
import com.gao.demo32.common.ChatResponse;
import com.gao.demo32.service.ChatService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
@Validated
@Slf4j
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * 普通对话接口
     * 入参示例：
     * {
     *     "message": "hello 呀，请简短介绍一下你是谁，50字以内",
     *     "taskStrategy": 10
     * }
     */
    @PostMapping
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        String sessionId = request.getSessionId() != null
                ? request.getSessionId()
                : UUID.randomUUID().toString();

        String response = chatService.chat(sessionId, request.getMessage(), request.getTaskStrategy());

        return ResponseEntity.ok(new ChatResponse(sessionId, response, System.currentTimeMillis()));
    }

    /**
     * 流式对话接口（SSE）
     * 前端通过 EventSource 或 fetch streaming 接收
     */
//    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public Flux<ServerSentEvent<String>> streamChat(@Valid @RequestBody ChatRequest request) {
//        String sessionId = request.sessionId() != null
//                ? request.sessionId()
//                : UUID.randomUUID().toString();
//
//        return chatService.streamChat(sessionId, request.message(), request.model())
//                // 每个 token 作为一个 SSE 事件发送
//                .map(token -> ServerSentEvent.<String>builder()
//                        .data(token)
//                        .event("message")
//                        .build())
//                // 发送完成事件
//                .concatWith(Flux.just(ServerSentEvent.<String>builder()
//                        .data("[DONE]")
//                        .event("done")
//                        .build()))
//                // 错误处理：发送错误事件而不是断开连接
//                .onErrorResume(e -> {
//                    log.error("流式响应出错", e);
//                    return Flux.just(ServerSentEvent.<String>builder()
//                            .data("AI 服务暂时不可用，请稍后重试")
//                            .event("error")
//                            .build());
//                });
//    }

    /**
     * 获取对话历史
     */
//    @GetMapping("/history/{sessionId}")
//    public ResponseEntity<List<ConversationMessage>> getHistory(
//            @PathVariable String sessionId,
//            @RequestParam(defaultValue = "20") int limit
//    ) {
//        return ResponseEntity.ok(chatService.getHistory(sessionId, limit));
//    }

    /**
     * 清除对话历史（开始新对话）
     */
//    @DeleteMapping("/history/{sessionId}")
//    public ResponseEntity<Void> clearHistory(@PathVariable String sessionId) {
//        chatService.clearHistory(sessionId);
//        return ResponseEntity.noContent().build();
//    }
}

