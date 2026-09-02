package com.gao.demo32.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Slf4j
public class ConversationRepository {

    // 存储每个会话的完整消息列表（按时间顺序）
    private final Map<String, List<Message>> sessionMap = new HashMap<>();

    /**
     * 保存一次问答（用户提问 + AI 回复）
     */
    public void save(String sessionId, String userQuestion, String assistantResponse) {
        List<Message> messages = sessionMap.getOrDefault(sessionId, new ArrayList<>());
        // 按顺序添加：先用户消息，后助手消息
        messages.add(new UserMessage(userQuestion));
        messages.add(new AssistantMessage(assistantResponse));
        sessionMap.put(sessionId, messages);
        log.info("save: sessionId={}, userQuestion={}, response={}, timestamp={}",
                sessionId, userQuestion, assistantResponse, System.currentTimeMillis());
    }

    /**
     * 获取某个会话的完整历史消息（不含系统提示）
     */
    public List<Message> findBySessionId(String sessionId) {
        return sessionMap.getOrDefault(sessionId, Collections.emptyList());
    }

    /**
     * 可选：清空会话
     */
    public void clear(String sessionId) {
        sessionMap.remove(sessionId);
    }
}
