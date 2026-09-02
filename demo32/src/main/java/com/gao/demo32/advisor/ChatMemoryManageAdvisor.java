package com.gao.demo32.advisor;

import com.gao.demo32.repository.ConversationRepository;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Component
public class ChatMemoryManageAdvisor implements CallAdvisor, StreamAdvisor {

    @Resource
    private ConversationRepository conversationRepository;

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        // 1. 获取会话ID
//        String sessionId = String.valueOf(chatClientRequest.context().get(ChatMemory.CONVERSATION_ID));
//
//        // 2. 获取历史记录（List<String>，每条为一条历史消息的文本，但缺失角色信息）
//        List<UserMessage> sessionHistoryList = conversationRepository.findBySessionId(sessionId);
//
//        // 3. 无历史则直接放行
//        if (CollectionUtils.isEmpty(sessionHistoryList)) {
//            return callAdvisorChain.nextCall(chatClientRequest);
//        }
//
//        // 4. 构建包含历史上下文的用户提问
//        String historyText = String.join(";", sessionHistoryList);
//
//        // 5. 在原请求中，追加历史记录
//        List<UserMessage> userMessages = chatClientRequest.prompt().getUserMessages();
//        userMessages.add(new UserMessage("这是会话历史记录，每一次对话都是'request:{},response{}'格式，多次对话之间用;分隔：" + historyText));

        // 6. 继续执行链
        return callAdvisorChain.nextCall(chatClientRequest);
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
        // 1. 获取会话ID
        String sessionId = String.valueOf(chatClientRequest.context().get(ChatMemory.CONVERSATION_ID));

        // 2. 获取历史记录（List<Message>，每条为一条历史消息的文本）
        List<Message> historyMessages = conversationRepository.findBySessionId(sessionId);

        if (CollectionUtils.isEmpty(historyMessages)) {
            return streamAdvisorChain.nextStream(chatClientRequest);
        }

        // 3. 获取原始 Prompt 中的所有消息
        Prompt originalPrompt = chatClientRequest.prompt();
        List<Message> originalMessages = originalPrompt.getInstructions();

        // 4. 构建新消息列表（顺序至关重要！）
        List<Message> newMessages = new ArrayList<>();

        // 4.1 保留系统提示（通常放在最前面）
        for (Message msg : originalMessages) {
            if (msg instanceof SystemMessage) {
                newMessages.add(msg);
                break; // 通常只有一个系统消息
            }
        }

        // 4.2 插入历史对话（User / Assistant 交替）
        newMessages.addAll(historyMessages);

        // 4.3 追加当前用户的问题（必须放在最后！）
        for (Message msg : originalMessages) {
            if (msg instanceof SystemMessage) {
                continue;
            }
            newMessages.add(msg); // 当前问题
        }

        // 5. 构建新 Prompt
        Prompt newPrompt = new Prompt(newMessages, originalPrompt.getOptions());

        // 6. 使用 mutate() 构建新请求
        ChatClientRequest newRequest = chatClientRequest.mutate()
                .prompt(newPrompt)
                .build();

        return streamAdvisorChain.nextStream(newRequest);
    }

    @Override
    public String getName() {
        return "记忆管理 Advisor";
    }

    @Override
    public int getOrder() {
        return 10;
    }
}
