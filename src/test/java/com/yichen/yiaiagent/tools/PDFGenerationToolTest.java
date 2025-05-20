package com.yichen.yiaiagent.tools;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author mojie
 * @date 2025/5/19 12:45
 * @description:
 */
class PDFGenerationToolTest {
    @Test
    public void testPDFGenerate() {
        PDFGenerationTool tool = new PDFGenerationTool();
        String result = tool.generatePDF("编程大师.pdf", "我是一个编程大师");
        assertNotNull(result);
    }
}