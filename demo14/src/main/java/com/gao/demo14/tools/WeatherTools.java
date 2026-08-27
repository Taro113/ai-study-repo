package com.gao.demo14.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class WeatherTools {

    @Tool(description = "查询指定城市的天气，返回温度范围和天气状况")
    public String getWeather(
            @ToolParam(description = "城市名称（中文），如北京、上海") String city) {
        // 模拟天气 API 调用
        Map<String, String> weatherData = Map.of(
                "北京", "晴，12-20℃",
                "上海", "多云，15-22℃",
                "广州", "小雨，20-27℃"
        );
        return weatherData.getOrDefault(city, "暂无数据");
    }
}
