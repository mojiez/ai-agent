package com.yichen.yiaiagent.agent.model;

/**
 * @author mojie
 * @date 2025/5/23 10:38
 * @description:
 */
public abstract class ReActAgent extends BaseAgent{
    /**
     * 每个步骤都要先思考，再执行
     * 思考什么具体是看使用什么来实现ReAck， 如果是ToolCallAgent， 就是思考使用什么工具来解决当下这个步骤的问题 ack就是调用这个工具
     * @return
     */
    @Override
    public String step() {
        try {
            boolean shouldAck = think();
            if (!shouldAck) {
                return "思考完成-无需行动";
            }
            String result = act();
            return result; // 这里的结果在step中被加入到结果列表中
        } catch (Exception e) {
            // 记录异常日志
            e.printStackTrace();
            return "步骤执行失败: " + e.getMessage();
        }
    }

    /**
     * 返回值 boolean， 即是否行动
     * 如果模型判断不需要使用工具， 即工具列表为空， 返回false
     * @return
     */
    public abstract boolean think();
    public abstract String act();
}
