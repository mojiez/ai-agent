package com.yichen.yiaiagent.tools;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author mojie
 * @date 2025/5/19 12:48
 * @description: 集中注册工具类， 统一管理和绑定所有工具
 */
@Configuration
public class ToolRegistration {
    @Value("${search-api.api-key}")
    private String searchApiKey;

    @Bean
    public ToolCallback[] allTools() {
        FileOperationTool fileOperationTool = new FileOperationTool();
        PDFGenerationTool pdfGenerationTool = new PDFGenerationTool();
        ResourceDownloadTool resourceDownloadTool = new ResourceDownloadTool();
        TerminalOperationTool terminalOperationTool = new TerminalOperationTool();
        WeatherTools weatherTools = new WeatherTools();
        WebScrapingTool webScrapingTool = new WebScrapingTool();
        WebSearchTool webSearchTool = new WebSearchTool(searchApiKey);
        TerminateTool terminateTool = new TerminateTool();
        ToolCallback[] tools = ToolCallbacks.from(
                fileOperationTool,
                pdfGenerationTool,
                resourceDownloadTool,
                terminalOperationTool,
                weatherTools,
                webScrapingTool,
                webSearchTool,
                terminateTool
        );
        return tools;
    }
}
