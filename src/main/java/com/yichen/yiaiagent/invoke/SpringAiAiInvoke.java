package com.yichen.yiaiagent.invoke;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author mojie
 * @date 2025/5/5 16:20
 * @description: 使用 Spring AI接入大模型
 */
// commandLineRunner 在 SpringBoot 项目启动时执行， 要实现 run方法， 同时要交给Spring容器管理这个类
@Component
public class SpringAiAiInvoke implements CommandLineRunner {

    @Resource
    private ChatModel dashscopeChatModel; // 自动注入， 引入了 spring ai alibaba的sdk， 配置文件中写好了配置

    @Override
    public void run(String... args) throws Exception {
        AssistantMessage output = dashscopeChatModel.call(new Prompt("你好，我是zkjj"))
                .getResult()
                .getOutput();
        System.out.println(output.getText());
    }
}

