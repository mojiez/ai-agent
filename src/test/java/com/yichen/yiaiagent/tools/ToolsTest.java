package com.yichen.yiaiagent.tools;

import com.yichen.yiaiagent.app.LoveApp;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author mojie
 * @date 2025/5/16 15:01
 * @description:
 */
@SpringBootTest
class ToolsTest {
    @Resource
    LoveApp loveApp;

//    @Test
//    void testTools() {
//        String chatId = UUID.randomUUID().toString();
//        // 第一轮
//        String message = "我想知道今天昆明的天气怎么样";
//
//        // 得到工具对象
//        ToolCallback[] tools = ToolCallbacks.from(new WeatherTools());
//        String content = loveApp.doChatWithTools(message, chatId, tools);
//        Assertions.assertNotNull(content);
//    }
}