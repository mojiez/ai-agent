package com.yichen.yiaiagent.agent.model;

import com.alibaba.cloud.ai.agent.Agent;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mojie
 * @date 2025/5/23 09:30
 * @description: 智能体基类， 定义了执行流程和状态的转换
 */
@Slf4j
@Data
public abstract class BaseAgent {
    private String name;
    private String systemPrompt;
    private String nextStepPrompt;
    private AgentState state = AgentState.IDLE;
    private int maxStep = 20;
    private int curStep = 0;
    private ChatClient chatClient;

    /**
     * Memory 需要自主维护会话上下文
     * 即持久对话
     * 要保存三类prompt
     * systemPrompt
     * userPrompt
     * assistantPrompt
     */
    private List<Message> messageList = new ArrayList<>();

    /**
     * 执行的流程
     *
     * @return
     */
    public String run(String userPrompt) {
        if (state != AgentState.IDLE) {
            throw new RuntimeException("模型初始状态错误: state:" + state.toString());
        }
        state = AgentState.RUNNING;
        // 记录消息上下文
        messageList.add(new UserMessage(userPrompt));
        // 保存结果列表
        List<String> results = new ArrayList<>();
        try {
            // 现在这里的状态是不会变化的， 要加一个Terminate工具， 当大模型申请调用Terminate工具的时候， 把状态转为TERMINATE
            while (state != AgentState.TERMINATE && curStep < maxStep) {
                // 执行状态
                curStep++;
                // todo 这里的message上下文是在step里面管理的吗
                String stepResult = step();
                String result = "Step " + curStep + 1 + ": " + stepResult;
                results.add(result);
            }

            // 检查是否超出步数限制
            if (curStep >= maxStep) {
                state = AgentState.TERMINATE;
                results.add("Terminated: Reached max steps (" + maxStep + ")");
            }
            // 当在try块或者catch块中遇到return语句时，finally语句块将在方法返回之前被执行
            return String.join("\n", results);
        } catch (Exception e) {
            state = AgentState.ERROR;
            log.error("Error executing agent", e);

            // 当在try块或者catch块中遇到return语句时，finally语句块将在方法返回之前被执行 return被延迟到了finally执行后再返回
            return "执行错误" + e.getMessage();
        } finally {

            this.cleanup();
            // 注意 不要在finally语句中使用return， 当try和finally中都有return，try中的return会被忽略
            // 这是因为 try 语句中的 return 返回值会先被暂存在一个本地变量中，当执行到 finally 语句中的 return 之后，这个本地变量的值就变为了 finally 语句中的 return 返回
        }

    }

    public abstract String step();

    public void cleanup() {
        // 子类可以重写此方法来清理资源
    }
}
