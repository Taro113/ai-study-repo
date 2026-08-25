package com.gao.demo06.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;

@Slf4j
public class RefillRedisAdvisor implements CallAdvisor, StreamAdvisor {

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        Object conversationId = chatClientRequest.context().getOrDefault("conversationId", "");
        // // 调用链中的下一个 Advisor 或最终调用 LLM
        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);
        saveRedisLog(conversationId, chatClientResponse.chatResponse());
        return chatClientResponse;
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
        Object conversationId = chatClientRequest.context().getOrDefault("conversationId", "");
        // // 调用链中的下一个 Advisor 或最终调用 LLM
        Flux<ChatClientResponse> chatClientResponseFlux = streamAdvisorChain.nextStream(chatClientRequest);
        chatClientResponseFlux.doOnNext(this::saveRedisLog);
        return chatClientResponseFlux;
    }

    @Override
    public String getName() {
        return "RefillRedisAdvisor";
    }

    /**
     * 值越小优先级越高
     */
    @Override
    public int getOrder() {
        return -1;
    }

    private void saveRedisLog(Object conversationId, ChatResponse chatResponse) {
        // 保存数据到 Redis 中
        log.info("保存数据到 Redis 中");
    }

    private void saveRedisLog(ChatClientResponse chatClientResponse) {
        // 保存数据到 Redis 中
        log.info("保存数据到 Redis 中");
    }
}