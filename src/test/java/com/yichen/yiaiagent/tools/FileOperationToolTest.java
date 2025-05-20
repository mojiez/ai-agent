package com.yichen.yiaiagent.tools;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author mojie
 * @date 2025/5/18 15:11
 * @description:
 */
class FileOperationToolTest {
    @Test
    public void testReadFile() {
        FileOperationTool tool = new FileOperationTool();
        String fileName = "编程大师.txt";
        String s = tool.readFile(fileName);
        assertNotNull(s);
    }

    @Test
    public void testWriteFile() {
        FileOperationTool tool = new FileOperationTool();
        String fileName = "编程大师.txt";
        String content = "我是一个编程大师，精通leetcode hot 100，代码随想录等题库， 尽情向我提问吧";
        String s = tool.writeFile(fileName, content);
        assertNotNull(s);
    }
}