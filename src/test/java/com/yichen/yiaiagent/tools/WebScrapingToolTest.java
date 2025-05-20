package com.yichen.yiaiagent.tools;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author mojie
 * @date 2025/5/18 20:19
 * @description:
 */
class WebScrapingToolTest {
    @Test
    public void testWebScrape() {
        WebScrapingTool tool = new WebScrapingTool();
        String url = "https://www.codefather.cn";
        String content = tool.scrapeWebPage(url);
        assertNotNull(content);
    }
}