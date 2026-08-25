package com.gao.demo07;

import java.util.List;

public record MarketReport(
        String industry,
        String region,
        MarketSize marketSize,
        List<KeyPlayer> topPlayers,
        List<Trend> trends,
        RiskAssessment risks,
        String outlook          // 未来展望
) {
    // 嵌套 Record：市场规模
    public record MarketSize(
            double totalValueBillionUSD,
            double growthRatePercent,
            String period               // 数据周期，如 "2024"
    ) {}

    // 嵌套 Record：主要参与者
    public record KeyPlayer(
            String companyName,
            double marketSharePercent,
            String coreAdvantage
    ) {}

    // 嵌套 Record：趋势
    public record Trend(
            String name,
            String description,
            String impact   // high/medium/low
    ) {}

    // 嵌套 Record：风险评估
    public record RiskAssessment(
            List<String> majorRisks,
            String overallRiskLevel  // low/medium/high/critical
    ) {}
}
