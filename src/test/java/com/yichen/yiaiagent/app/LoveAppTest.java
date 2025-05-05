package com.yichen.yiaiagent.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author mojie
 * @date 2025/5/5 19:54
 * @description: 单元测试 多轮会话
 */
@SpringBootTest
class LoveAppTest {
    @Resource
    LoveApp loveApp;

    @Test
    void testChat() {
        String chatId = UUID.randomUUID().toString();

        // 第一轮
        String message = "你好，我是zkjj";
        String answer = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);

        // 第二轮
        message = "我想让你帮我策划一起浪漫的约会，和我的伴侣hsx";
        answer = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);

        // 第二轮
        message = "我叫什么名字？我的伴侣叫什么名字？";
        answer = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void testWithReport() {
        String chatId = UUID.randomUUID().toString();

        // 第一轮
        String message = "你好，我是zkjj，我想策划一场浪漫约会但是不知道应该怎么做";
        LoveApp.LoveReport loveReport = loveApp.doChatWithReport(message, chatId);
        System.out.println(loveReport);
    }
}