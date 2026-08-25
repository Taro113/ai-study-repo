package com.gao.demo08.resume.controller;

import com.gao.demo08.resume.model.ResumeInfo;
import com.gao.demo08.resume.service.ResumeExtractionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;

// Controller 层
@RestController
@RequestMapping("/api/resume")
public class ResumeController {

    private final ResumeExtractionService extractionService;

    public ResumeController(ResumeExtractionService extractionService) {
        this.extractionService = extractionService;
    }

    /**
     * 简历信息提取接口
     * POST /api/resume/extract
     * Body: { "resumeText": "..." }
     * <p>
     * 示例：
     * 请求：
     * {
     *     "resumeText": "张三，Java 高级工程师，邮箱：zhang@example.com，工作经历：2020-2024 在阿里云担任后端工程师，负责分布式系统设计，完成了 QPS 从 1000 提升到 50000 的性能优化项目。教育背景：清华大学，计算机科学，2016年毕业。技能：Java, Spring Boot, MySQL, Redis, Kafka, Docker。"
     * }
     * 响应：
     * {
     *     "personal": {
     *         "name": "张三",
     *         "email": null,
     *         "phone": null,
     *         "location": null,
     *         "linkedinUrl": null
     *     },
     *     "workExperiences": [
     *         {
     *             "company": "阿里云",
     *             "title": "后端工程师",
     *             "startDate": "2020-12",
     *             "endDate": null,
     *             "achievements": [
     *                 "QPS 从 1000 提升到 50000 的性能优化项目"
     *             ]
     *         }
     *     ],
     *     "education": [
     *         {
     *             "institution": "清华大学",
     *             "degree": "计算机科学",
     *             "major": "计算机科学",
     *             "graduationYear": "2016"
     *         }
     *     ],
     *     "skills": [
     *         "Java",
     *         "Docker",
     *         "Kafka",
     *         "MySQL",
     *         "Redis",
     *         "Spring Boot"
     *     ],
     *     "certifications": null,
     *     "summary": null
     * }
     */
    @PostMapping("/extract")
    public ResponseEntity<ResumeInfo> extractResume(
            @RequestBody Map<String, String> body) {
        String resumeText = body.get("resumeText");

        if (Objects.isNull(resumeText) || resumeText.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        ResumeInfo result = extractionService.extractResume(resumeText);
        return ResponseEntity.ok(result);
    }
}

