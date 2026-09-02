package com.gao.demo32.config;

import com.gao.demo32.advisor.ChatMemoryManageAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//@EnableRetry
public class AiConfig {

    /**
     * 创建带默认配置的 ChatClient Bean
     * ChatClient.Builder 由 Spring AI Auto-Configuration 自动提供
     */
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder, ChatMemoryManageAdvisor chatMemoryManageAdvisor) {
        return builder
                // 设置默认系统提示词（所有通过此 ChatClient 发出的请求都会附带）
                .defaultSystem("""
                你是一个专业的企业 AI 问答助手。你具备以下能力：
                1. 基于公司知识库回答问题（会自动检索相关文档）
                2. 访问公司业务数据
                3. 联网查询回答问题所需要的数据
                4. 记住本次对话的上下文

                行为准则：
                - 对于知识库/网络中有明确记录的问题，引用数据内容并注明来源
                - 对于不确定的内容，明确告知用户，不要编造信息
                - 保持回答简洁专业，必要时使用 Markdown 格式
                """)
                // 设置默认 ChatOptions（可在调用时覆盖）
                .defaultOptions(ChatOptions.builder()
                        .temperature(0.75)
                        .maxTokens(1000)
                        .build())
                .defaultAdvisors(chatMemoryManageAdvisor)
                .build();
    }
}
