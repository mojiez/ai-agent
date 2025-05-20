package com.yichen.yiaiagent.tools;

import org.junit.jupiter.api.Test;
import org.springframework.ai.tool.annotation.Tool;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author mojie
 * @date 2025/5/18 20:32
 * @description:
 */
class TerminalOperationToolTest {
    @Test
    public void testTerminalOperation() {
        String command = "ls -l";
        TerminalOperationTool tool = new TerminalOperationTool();
        String s = tool.executeTerminalCommand(command);
        assertNotNull(s);
    }

}