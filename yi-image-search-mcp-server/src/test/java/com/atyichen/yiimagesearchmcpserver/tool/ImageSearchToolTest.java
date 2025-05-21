package com.atyichen.yiimagesearchmcpserver.tool;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author mojie
 * @date 2025/5/20 14:47
 * @description:
 */
@SpringBootTest
class ImageSearchToolTest {
    @Resource
    private ImageSearchTool imageSearchTool;
    @Test
    void testImageSearchTool() {
        String result = imageSearchTool.searchImage("flower");
        assertNotNull(result);
    }
}