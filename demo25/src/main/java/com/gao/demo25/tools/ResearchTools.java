package com.gao.demo25.tools;

import com.gao.demo25.entity.SearchResult;
import com.gao.demo25.gateway.service.WebSearchService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.List;
import java.util.Map;

@Component
public class ResearchTools {

    private final JdbcTemplate jdbcTemplate;
    private final WebSearchService webSearchService;

    public ResearchTools(JdbcTemplate jdbcTemplate, WebSearchService webSearchService) {
        this.jdbcTemplate = jdbcTemplate;
        this.webSearchService = webSearchService;
    }

    @Tool(description = "搜索互联网上的最新信息。适用于需要实时数据、新闻、最新技术动态的场景。")
    public String searchWeb(
            @ToolParam(description = "搜索关键词，用英文效果更好") String query,
            @ToolParam(description = "返回结果数量，默认3，最多10") int maxResults
    ) {
        List<SearchResult> results = webSearchService.search(query, maxResults);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < results.size(); i++) {
            SearchResult r = results.get(i);
            sb.append(String.format("[%d] %s\n%s\n来源: %s\n\n",
                    i + 1, r.getTitle(), r.getSnippet(), r.getUrl()));
        }
        return sb.toString();
    }

    @Tool(description = "查询内部业务数据库，获取产品销售数据、用户统计等业务指标。")
    public String queryDatabase(
            @ToolParam(description = "要查询的指标类型：sales/users/products") String metricType,
            @ToolParam(description = "时间范围：today/this_week/this_month/this_year") String timeRange
    ) {
        // 将自然语言时间范围转换为 SQL 条件
        String dateFilter = buildDateFilter(timeRange);
        String sql = buildQuery(metricType, dateFilter);

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        return formatAsTable(rows);
    }

    @Tool(description = "将研究结果保存为结构化报告，返回报告ID。")
    public String saveReport(
            @ToolParam(description = "报告标题") String title,
            @ToolParam(description = "报告正文内容（Markdown格式）") String content
    ) {
        // 保存到数据库
        String reportId = "RPT-" + System.currentTimeMillis();
        jdbcTemplate.update(
                "INSERT INTO reports (id, title, content, created_at) VALUES (?, ?, ?, NOW())",
                reportId, title, content
        );
        return "报告已保存，ID: " + reportId;
    }

    private String buildDateFilter(String timeRange) {
        return switch (timeRange) {
            case "today" -> "DATE(created_at) = CURDATE()";
            case "this_week" -> "YEARWEEK(created_at) = YEARWEEK(NOW())";
            case "this_month" -> "MONTH(created_at) = MONTH(NOW()) AND YEAR(created_at) = YEAR(NOW())";
            default -> "YEAR(created_at) = YEAR(NOW())";
        };
    }

    private String buildQuery(String metricType, String dateFilter) {
        return switch (metricType) {
            case "sales" -> "SELECT DATE(created_at) as date, SUM(amount) as total_sales, COUNT(*) as order_count FROM orders WHERE " + dateFilter + " GROUP BY DATE(created_at) ORDER BY date DESC LIMIT 10";
            case "users" -> "SELECT DATE(created_at) as date, COUNT(*) as new_users FROM users WHERE " + dateFilter + " GROUP BY DATE(created_at) ORDER BY date DESC LIMIT 10";
            default -> "SELECT * FROM products WHERE " + dateFilter + " LIMIT 10";
        };
    }

    private String formatAsTable(List<Map<String, Object>> rows) {
        if (rows.isEmpty()) return "无数据";
        StringBuilder sb = new StringBuilder();
        // 表头
        sb.append(String.join(" | ", rows.get(0).keySet())).append("\n");
        sb.append("-".repeat(50)).append("\n");
        // 数据行
        for (Map<String, Object> row : rows) {
            sb.append(String.join(" | ", row.values().stream()
                    .map(v -> v == null ? "N/A" : v.toString())
                    .toList())).append("\n");
        }
        return sb.toString();
    }
}
