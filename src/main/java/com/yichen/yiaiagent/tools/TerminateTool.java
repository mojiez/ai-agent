package com.yichen.yiaiagent.tools;

import org.springframework.ai.tool.annotation.Tool;

/**
 * @author mojie
 * @date 2025/5/23 13:35
 * @description: 终止工具
 */
public class TerminateTool {

    @Tool(description = """  
            Terminate the interaction when the request is met OR if the assistant cannot proceed further with the task.  
            "When you have finished all the tasks, call this tool to end the work.  
            """)
    public String doTerminate() {
        return "任务结束";
    }
}

