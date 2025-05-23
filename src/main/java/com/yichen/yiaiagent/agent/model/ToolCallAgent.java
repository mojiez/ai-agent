package com.yichen.yiaiagent.agent.model;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbacks;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author mojie
 * @date 2025/5/23 11:19
 * @description: 工具调用agent
 * 基于Spring AI，但是不让Spring AI接管工具调用， 自主实现 think ack observe的流程
 */
@Slf4j
public class ToolCallAgent extends ReActAgent {
    // 可用的工具
    private final ToolCallback[] availableTools;

    // 保存了 大模型的响应结果 （要调用什么工具）
    private ChatResponse toolCallChatResponse;

    // 工具调用管理者
    private final ToolCallingManager toolCallingManager;
    // 配置禁用内置的工具调用机制，自己维护上下文
    private final ChatOptions chatOptions;

    public ToolCallAgent(ToolCallback[] availableTools) {
        super(); // 这里就算不写也没事，因为会在第一行默认插入super， 但是这是无参构造的情况，有参数构造还是要写
        this.availableTools = availableTools;
        this.toolCallingManager = ToolCallingManager.builder().build();
        // 禁用Spring AI内置的工具调用机制，自己来维护消息
        this.chatOptions = DashScopeChatOptions.builder()
                .withProxyToolCalls(true)
                .build();
    }

    /**
     * @return
     */
    @Override
    public boolean think() {
        if (getNextStepPrompt() != null && !getNextStepPrompt().isEmpty()) {
            UserMessage userMessage = new UserMessage(getNextStepPrompt());
            getMessageList().add(userMessage);
        }
        List<Message> messageList = getMessageList();
        Prompt prompt = new Prompt(messageList, chatOptions); // 每次思考前， 通过上下文和option创建prompt
        try {
            // 获取带工具选项的响应
            ChatResponse chatResponse = getChatClient().prompt(prompt)
                    .system(getSystemPrompt())
                    .tools(availableTools)
                    .call()
                    .chatResponse();
            // 记录这个响应， 用于ack
            this.toolCallChatResponse = chatResponse;
            // 管理上下文， 记录assistantMessage
            AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
            // 日志打印提示信息
            String result = assistantMessage.getText();
            List<AssistantMessage.ToolCall> toolCallList = assistantMessage.getToolCalls();
            log.info(getName() + "的思考: " + result);
            log.info(getName() + "选择调用" + toolCallList.size() + "个工具");
            // 解析要调用的工具
            String toolCallInfo = toolCallList.stream()
                    .map(toolCallBack -> {
                        return String.format("工具名称: %s, 参数: %s", toolCallBack.name(), toolCallBack.arguments());
                    })
                    .collect(Collectors.joining("\n"));
            log.info("详细工具信息:");
            log.info(toolCallInfo);

            // 继续记录上下文
            // 只有在不调用工具的时候，才记录助手消息， 因为调用工具时会自动记录
            if (toolCallList.isEmpty()) {
                messageList.add(assistantMessage);
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            log.error(getName() + "的思考过程遇到了问题: " + e.getMessage());
            // 继续管理上下文
            messageList.add(new AssistantMessage("处理时遇到错误: " + e.getMessage()));
            return false;
        }
    }

    /**
     * 每一步ack的结果都会被记录到List列表中(会被重新放进大模型中吗？)
     *
     * @return
     */
    @Override
    public String act() {
        // 调用对应的工具
        if (!toolCallChatResponse.hasToolCalls()) {
            return "没有工具调用";
        }
        // 调用工具 相比think之前的list， 没有什么变化 ，只是说每次ack之后， list会更新
        Prompt prompt = new Prompt(getMessageList(), chatOptions);
        ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, toolCallChatResponse);
        // 记录消息上下文
        List<Message> messages = toolExecutionResult.conversationHistory();
        // 在这里更新message，这里的messages包含了之前的所有消息， 以及think调用后的消息(助手消息)，还有工具调用的结果, 看源码分析可以知道
        setMessageList(messages);

        // 当前工具调用的结果信息打印
        // 工具调用的结果
        ToolResponseMessage toolResponseMessage = (ToolResponseMessage) CollUtil.getLast(messages);
        String results = toolResponseMessage.getResponses().stream()
                .map(response -> "工具: " + response.name() + "完成了它的任务,结果是: " + response.responseData())
                .collect(Collectors.joining("\n"));
        // 如果说调用了Terminate工具，修改状态
        boolean terminateToolCalled = toolResponseMessage.getResponses().stream()
                .anyMatch(response -> "doTerminate".equals(response.name()));
        if (terminateToolCalled) {
            setState(AgentState.TERMINATE);
        }
        log.info(results);
        return results;
    }

}
