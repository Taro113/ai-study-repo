package com.gao.demo12;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j
public class DocumentIngestionService {
    private static final int DEFAULT_CHUNK_SIZE = 400;
    private static final int DEFAULT_MIN_CHUNK_SIZE_CHARS = 200;

    private final VectorStore vectorStore;
    private final TokenTextSplitter splitter;

    public DocumentIngestionService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
        this.splitter = TokenTextSplitter.builder()
                .withChunkSize(DEFAULT_CHUNK_SIZE)
                .withMinChunkSizeChars(DEFAULT_MIN_CHUNK_SIZE_CHARS)
                .build();
    }

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

    private List<Document> transformDocuments(List<Document> rawDocs, Map<String, Object> extraMetadata) {
        List<Document> chunks = splitter.transform(rawDocs);
        chunks.forEach(chunk -> chunk.getMetadata().putAll(extraMetadata));
        log.info("分块后数量：{} 个", chunks.size());
        return chunks;
    }

    private void loadDocuments(List<Document> chunks) {
        vectorStore.add(chunks);
        log.info("成功写入向量库！");
    }

    private List<Document> extractDocuments(Resource resource) {
        String filename = resource.getFilename();
        if (filename == null) {
            log.warn("资源文件名为空，返回空文档列表");
            return List.of();
        }

        DocumentReader reader = createDocumentReader(filename, resource);
        List<Document> documents = reader.get();
        log.info("提取原始文档：{} 个", documents.size());
        return documents;
    }

    private DocumentReader createDocumentReader(String filename, Resource resource) {
        String lowerFilename = filename.toLowerCase();

        if (lowerFilename.endsWith(".pdf")) {
            return new PagePdfDocumentReader(resource);
        }
        if (lowerFilename.endsWith(".docx") || lowerFilename.endsWith(".doc")) {
            return new TikaDocumentReader(resource);
        }
        return new TextReader(resource);
    }
}
