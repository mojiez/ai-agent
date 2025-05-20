package com.yichen.yiaiagent.tools;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author mojie
 * @date 2025/5/18 15:33
 * @description:
 */
@SpringBootTest
class WebSearchToolTest {
    @Value("${search-api.api-key}")
    private String searchApiKey;
    @Test
    public void testSearchWeb() {
        System.out.println("API Key: " + searchApiKey); // 看是否注入成功
        WebSearchTool tool = new WebSearchTool(searchApiKey);
        String result = tool.searchWeb("编程大师");
        assertNotNull(result);
    }
}