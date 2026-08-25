package com.gao.demo08.resume.service;

import com.gao.demo08.resume.model.ResumeInfo;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

// Service 层
@Service
public class ResumeExtractionService {

    private final ChatClient chatClient;

    public ResumeExtractionService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Retryable(maxAttempts = 2, backoff = @Backoff(delay = 500))
    public ResumeInfo extractResume(String resumeText) {
        return chatClient
                .prompt()
                .system("""
                        你是专业的 HR 数据处理专家。
                        请精确提取简历中的所有结构化信息。
                        规则：
                        - 日期格式统一为 YYYY-MM
                        - 如果某字段信息不存在，返回 null 而非空字符串
                        - 技能列表要归一化（如 "Java开发" → "Java"）
                        """)
                .user("请提取以下简历的结构化信息：\n\n" + resumeText)
                .call()
                .entity(ResumeInfo.class);
    }
}

