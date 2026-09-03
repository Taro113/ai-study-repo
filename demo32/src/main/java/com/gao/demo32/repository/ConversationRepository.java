package com.gao.demo32.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Slf4j
public class ConversationRepository {

    /**
     * 落库一次问答（用户提问 + AI 回复）
     */
    public void save(String sessionId, String userQuestion, String assistantResponse) {
        log.info("save: sessionId={}, userQuestion={}, response={}, timestamp={}",
                sessionId, userQuestion, assistantResponse, System.currentTimeMillis());
    }
}
