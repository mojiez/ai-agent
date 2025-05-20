package com.yichen.yiaiagent.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * @author mojie
 * @date 2025/5/16 14:55
 * @description: 提供给SpringAI的天气工具类
 */
public class WeatherTools {
    @Tool(description = "获取指定城市的当前天气情况")
    String getWeather(@ToolParam(description = "城市名称") String city) {
        return String.format("%s今天晴朗,气温25摄氏度",city);
    }
}
