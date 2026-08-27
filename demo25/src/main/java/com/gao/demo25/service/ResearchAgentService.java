package com.gao.demo25.service;

import com.gao.demo25.tools.ResearchTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ResearchAgentService {

    private final ChatClient agentClient;

    // Agent 的系统提示词——定义其角色、能力边界和行为准则
    private static final String AGENT_SYSTEM_PROMPT = """
        你是一个专业的企业研究助手，具备以下能力：
        1. 搜索互联网获取最新信息（使用 searchWeb 工具）
        2. 查询公司内部业务数据库（使用 queryDatabase 工具）
        3. 将研究成果保存为正式报告（使用 saveReport 工具）

        工作原则：
        - 先理解用户需求，制定研究计划
        - 多角度收集信息（内部数据 + 外部资讯）
        - 综合分析后给出有洞察力的结论
        - 重要发现主动建议保存为报告
        - 如果工具调用失败，优先停顿3秒后重试一次，还是失败的话，说明原因并尝试其他方式

        当前日期：{current_date}
        """;

    public ResearchAgentService(ChatClient.Builder chatClientBuilder,
                                ResearchTools researchTools) {
        // 使用持久化 Chat Memory 保存 Agent 状态
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .maxMessages(300)  // Agent 可能需要更长的上下文窗口
                .build();

        this.agentClient = chatClientBuilder
                .defaultSystem(AGENT_SYSTEM_PROMPT)
                // 注册所有工具——Spring AI 会自动处理工具调用循环
                .defaultTools(researchTools)
                // 添加记忆 Advisor，让 Agent 记住之前的操作
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .build();
    }

    /**
     * 同步执行 Agent 任务（适合短任务）
     */
    public String runTask(String sessionId, String userGoal) {
        return agentClient.prompt()
                .system(s -> s.param("current_date", java.time.LocalDate.now().toString()))
                .user(userGoal)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, sessionId))
                .call()
                .content();
    }

    /**
     * 流式执行 Agent 任务（适合长时间运行任务，实时显示进度）
     */
    public Flux<String> runTaskStream(String sessionId, String userGoal) {
        return agentClient.prompt()
                .system(s -> s.param("current_date", java.time.LocalDate.now().toString()))
                .user(userGoal)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, sessionId))
                .stream()
                .content();
    }
}
