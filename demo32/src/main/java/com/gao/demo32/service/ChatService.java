package com.gao.demo32.service;

import com.gao.demo32.exception.ChatException;
import com.gao.demo32.repository.ConversationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class ChatService {

    private final ChatClient chatClient;
//    private final ModelRoutingService routingService;
    private final ConversationRepository conversationRepository;

    public ChatService(
            ChatClient chatClient,
//            ModelRoutingService routingService,
            ConversationRepository conversationRepository
    ) {
        this.chatClient = chatClient;
//        this.routingService = routingService;
        this.conversationRepository = conversationRepository;
    }

    public String chat(String sessionId, String message, Integer taskStrategy) {
        log.info("Chat request | session={} | model={} | msgLen={}", sessionId, taskStrategy, message.length());

        // 根据请求路由到合适的模型
//        ChatClient client = routingService.selectClient(taskStrategy);

        try {
            String response = chatClient.prompt()
                    .user(message)
//                    .advisors(a -> a.param(
//                            MessageChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, sessionId
//                    ))
                    .call()
                    .content();

            // 异步持久化对话历史（不阻塞主流程）
            CompletableFuture.runAsync(() ->
                    conversationRepository.save(sessionId, message, response)
            );

            return response;

        } catch (Exception e) {
            log.error("Chat failed | session={}", sessionId, e);
            throw new ChatException("AI 服务暂时不可用", e);
        }
    }

    public Flux<String> streamChat(String sessionId, String message, Integer taskStrategy) {
//        ChatClient client = routingService.selectClient(preferredModel);

        // 每个请求独立的累积容器
        List<String> tokenAccumulator = Collections.synchronizedList(new ArrayList<>());

        // 收集每一个 token
        return chatClient.prompt()
                .user(message)
                .advisors(a -> a.param(
                        ChatMemory.CONVERSATION_ID, sessionId
                ))
                .stream()
                .content()
                .doOnNext(tokenAccumulator::add)
                // 收集完整响应用于持久化
                .doOnComplete(() -> {
                    String fullResponse = String.join("", tokenAccumulator);
                    // 将同步保存包装为响应式 Mono
                    Mono.fromRunnable(() -> conversationRepository.save(sessionId, message, fullResponse))
                            .subscribeOn(Schedulers.boundedElastic())  // 阻塞操作专用线程池
                            .subscribe(
                                    null,  // 正常完成无需操作
                                    error -> log.error("保存对话记录失败 | session={}", sessionId, error)
                            );
                    log.info("Stream completed, response length={}", fullResponse.length());
                });
    }

//    public List<ConversationMessage> getHistory(String sessionId, int limit) {
//        return conversationRepository.findBySessionId(sessionId, limit);
//    }

//    public void clearHistory(String sessionId) {
//        conversationRepository.deleteBySessionId(sessionId);
//    }
}
