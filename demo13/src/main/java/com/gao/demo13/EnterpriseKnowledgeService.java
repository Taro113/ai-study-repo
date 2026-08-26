package com.gao.demo13;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnterpriseKnowledgeService {

    private final VectorStore vectorStore;
    private final ChatClient chatClient;

    public EnterpriseKnowledgeService(
            VectorStore vectorStore,
            ChatClient.Builder clientBuilder) {
        this.vectorStore = vectorStore;

        // 配置系统提示词，指导 LLM 的行为
        String systemPrompt = """
            你是企业内部知识库助手。
            请严格基于提供的参考资料回答问题，不要编造信息。
            如果参考资料中没有相关内容，请明确告知用户"知识库中未找到相关信息"。
            回答时请引用资料来源（文件名和页码）。
            """;

        this.chatClient = clientBuilder
                .defaultSystem(systemPrompt)
                .defaultAdvisors(
                        QuestionAnswerAdvisor.builder(vectorStore)
                                .searchRequest(SearchRequest.builder().topK(5).similarityThreshold(0.72).build())
                                .build()
                )
                .build();
    }

    /**
     * 问答接口：返回答案和引用来源
     */
    public RagAnswer ask(String question, String department) {
        // 元数据过滤：只在指定部门的文档中搜索
        SearchRequest searchRequest = SearchRequest.builder().query(question)
                .topK(5)
                .similarityThreshold(0.72)
                .filterExpression("department == '" + department + "'").build();

        // 先手动搜索，获取来源信息
        List<Document> sources = vectorStore.similaritySearch(searchRequest);

        // 向 LLM 提问（Advisor 会自动注入上下文）
        String answer = chatClient.prompt()
                .user(question)
                .advisors(advisor ->
                        advisor.param(QuestionAnswerAdvisor.FILTER_EXPRESSION,
                                "department == '" + department + "'")
                )
                .call()
                .content();

        // 提取引用来源
        List<String> citations = sources.stream()
                .map(doc -> {
                    String source = (String) doc.getMetadata().getOrDefault("source", "未知来源");
                    Object page = doc.getMetadata().get("page_number");
                    return page != null ? source + " (第 " + page + " 页)" : source;
                })
                .distinct()
                .toList();

        return new RagAnswer(answer, citations);
    }
}

// 返回结构
record RagAnswer(String answer, List<String> citations) {}

