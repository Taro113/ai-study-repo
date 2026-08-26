package com.gao.demo11.service;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BatchEmbeddingService {

    private final EmbeddingModel embeddingModel;

    // 每批最多处理的文本数（根据模型限制调整）
    private static final int BATCH_SIZE = 100;

    public BatchEmbeddingService(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    /**
     * 批量将文本列表转换为向量，自动按 BATCH_SIZE 分批处理
     *
     * @param texts 待嵌入的文本列表（可以非常大，方法内部自动分批）
     * @return 与输入顺序一一对应的向量列表
     */
    public List<float[]> embedAll(List<String> texts) {
        List<float[]> allVectors = new ArrayList<>();

        // 按批次处理，避免单次请求过大
        for (int i = 0; i < texts.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, texts.size());
            List<String> batch = texts.subList(i, end);

            // Spring AI 会自动并发处理同一批次内的多个请求
            List<float[]> batchVectors = embeddingModel.embed(batch);
            allVectors.addAll(batchVectors);

            // 打印进度（生产环境改用日志）
            System.out.printf("已处理 %d / %d 条%n", end, texts.size());
        }

        return allVectors;
    }

    /**
     * 计算两个向量的余弦相似度（值域 [-1, 1]，越接近 1 越相似）
     */
    public double cosineSimilarity(float[] a, float[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("向量维度不一致：" + a.length + " vs " + b.length);
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }

        // 防止除零
        if (normA == 0 || normB == 0) return 0.0;

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}

