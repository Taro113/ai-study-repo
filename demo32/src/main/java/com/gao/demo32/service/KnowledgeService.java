package com.gao.demo32.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class KnowledgeService {

    private final VectorStore vectorStore;
    private final DocumentReaderService documentReaderService;
    private final TokenTextSplitter splitter;

    /**
     * 完整 ETL Pipeline：
     * Extract（读取文件）→ Transform（分块+增强）→ Load（写入向量库）
     */
    public int ingest(Resource resource, Map<String, Object> extraMetadata) {
        // 读取文件为文档
        List<Document> rawDocs = extractDocuments(resource);

        // 文档分块
        List<Document> chunks = transformDocuments(rawDocs, extraMetadata);

        // 写入向量库
        loadDocuments(chunks);

        return chunks.size();
    }

    private List<Document> extractDocuments(Resource resource) {
        String filename = resource.getFilename();
        if (filename == null) {
            log.warn("资源文件名为空，返回空文档列表");
            return List.of();
        }

        DocumentReader reader = documentReaderService.getDocumentReader(filename, resource);
        List<Document> documents = reader.get();
        log.info("提取原始文档：{} 个", documents.size());
        return documents;
    }

    private List<Document> transformDocuments(List<Document> rawDocs, Map<String, Object> extraMetadata) {
        if (CollectionUtils.isEmpty(rawDocs)) {
            log.warn("原始文档为空，返回空文档列表");
            return List.of();
        }
        List<Document> chunks = splitter.transform(rawDocs);
        if (MapUtils.isNotEmpty(extraMetadata)) {
            chunks.forEach(chunk -> chunk.getMetadata().putAll(extraMetadata));
        }
        log.info("分块后总数量：{} 个", chunks.size());
        return chunks;
    }

    private void loadDocuments(List<Document> chunks) {
        if (CollectionUtils.isEmpty(chunks)) {
            log.info("分块数量为空，不进行向量库写入");
            return;
        }
        vectorStore.add(chunks);
        log.info("成功写入向量库: size={}", chunks.size());
    }

    public void clear() {
        FilterExpressionBuilder b = new FilterExpressionBuilder();

//        // 构建表达式: genre == "drama" AND year >= 2020
//        Filter.Expression expression = b.and(
//                b.eq("genre", "drama"),
//                b.gte("year", 2020)
//        ).build();
        // 构建表达式: format = md
        Filter.Expression expression = b.eq("format", "md").build();

        vectorStore.delete(expression);
    }
}



