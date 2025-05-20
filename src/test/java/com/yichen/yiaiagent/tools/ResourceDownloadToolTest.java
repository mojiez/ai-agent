package com.yichen.yiaiagent.tools;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author mojie
 * @date 2025/5/18 20:39
 * @description:
 */
class ResourceDownloadToolTest {
    @Test
    public void testResourceDownload() {
        ResourceDownloadTool tool = new ResourceDownloadTool();
        String url = "https://www.codefather.cn/logo.png";
        String fileName = "logo.png";
        String content = tool.downloadResource(url, fileName);
        assertNotNull(content);
    }
}