package com.gao.demo11.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class KnowledgeBaseService {

    private final VectorStore vectorStore;

    public KnowledgeBaseService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    // ===== 写入文档（Embedding 在 add 内部自动完成）=====
    public void addDocuments(List<Document> documents) {
        // add() 内部：自动调用 EmbeddingModel → 生成向量 → 存入数据库
        vectorStore.add(documents);
        System.out.println("成功写入 " + documents.size() + " 条文档");
    }

    // ===== 一般普通语义搜索 =====
    public List<Document> search(String question) {
        return vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(question)
                        .topK(5)
                        .similarityThreshold(0.70)
                        .build()
        );
    }

    // ===== 按 ID 删除 =====
    public void deleteDocuments(List<String> ids) {
        vectorStore.delete(ids);
    }

    /**
     * 元数据过滤（需要给文档添加对应元数据信息，才支持过滤）
     */
    // 场景：构建文档时，同时给文档填充元数据
    public Document buildDocument() {
        return new Document(
                "Spring Boot 3.0 连接池配置详解...",
                Map.of(
                        "category", "technical",
                        "version", 3.0,
                        "language", "zh",
                        "source", "official-docs",
                        "page", 42
                )
        );
    }
    // 场景：只在"技术文档"类别中搜索，且版本大于等于 2.0
    public List<Document> filterSearch(String question) {
        return vectorStore.similaritySearch(
                SearchRequest.builder().query(question)
                        .topK(3)
                        .similarityThreshold(0.75)
                        .filterExpression(
                                // Filter Expression 语法
                                "category == 'technical' && version >= 2.0 && language == 'zh'"
                        )
                        .build());
                        // Filter Expression 支持的操作符：
                        // ==  !=  >  >=  <  <=           比较操作符
                        // &&  ||  !                       逻辑操作符
                        // in ['a', 'b', 'c']             IN 列表
                        // nin ['x', 'y']                 NOT IN 列表
    }

}

