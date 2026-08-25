package com.gao.demo07;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;


@Configuration
@EnableRetry  // 开启 Spring Retry 支持
class RetryConfig {}


@Service
@Slf4j
public class ChatClientStructureService {

    private final ChatClient chatClient;

    public ChatClientStructureService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * 最简洁的结构化输出：.call().entity(Class)
     * ChatClient 内部自动处理：生成 Schema → 注入 Prompt → 解析 JSON
     */
    public ProductInfo extractWithEntity(String description) {
        return chatClient
                .prompt()
                .user("分析以下产品描述并提取结构化信息：\n" + description)
                .call()
                .entity(ProductInfo.class);  // 一行代码完成所有转换！
    }

    /**
     * 提取列表：使用 ParameterizedTypeReference 处理泛型
     */
    public List<ProductInfo> extractMultipleProducts(String catalog) {
        return chatClient
                .prompt()
                .user("从以下产品目录中提取所有产品信息：\n" + catalog)
                .call()
                .entity(new ParameterizedTypeReference<List<ProductInfo>>() {});
    }

    /**
     * 生成复杂嵌套报告
     * Spring AI 会自动处理嵌套 Record 的 JSON Schema 生成和反序列化
     */
    public MarketReport generateComplexReport(String industry) {
        return chatClient
                .prompt()
                .system("""
                    你是顶级市场调研机构的首席分析师。
                    请提供准确、专业的市场分析数据。
                    对于不确定的数据，请提供合理的估计范围，取中间值。
                    """)
                .user("请对 " + industry + " 行业进行全面的市场分析，" +
                        "包括市场规模、主要玩家、发展趋势和风险评估。")
                .call()
                .entity(MarketReport.class);
    }

    /**
     * 带重试的结构化输出
     * 当模型输出不符合预期格式时，最多重试 3 次
     * <p>
     * 需要引入依赖：spring-retry + spring-aspects
     */
    @Retryable(
            retryFor = {Exception.class},  // 捕获 JSON 解析异常
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)  // 1s, 2s 退避
    )
    public ProductInfo extractWithRetry(String description) {
        return chatClient
                .prompt()
                .system("请严格按照 JSON 格式输出，不要包含任何额外说明文字。")
                .user("提取以下产品的结构化信息：\n" + description)
                .call()
                .entity(ProductInfo.class);
    }

    /**
     * 手动错误处理：解析失败时返回默认值
     */
    public ProductInfo safeExtract(String description) {
        try {
            return extractWithRetry(description);
        } catch (Exception e) {
            log.warn("结构化输出解析失败，返回默认值。原因: {}", e.getMessage());
            // 返回包含原始描述的默认对象，避免请求失败
            return new ProductInfo(1L, "未能解析", 1, 0.0d, 0);
        }
    }
}

