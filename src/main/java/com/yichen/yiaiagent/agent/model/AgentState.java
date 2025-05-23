package com.yichen.yiaiagent.agent.model;

/**
 * @author mojie
 * @date 2025/5/22 21:48
 * @description: agent状态定义
 */
public enum AgentState {
    /**
     * 空闲状态
     */
    IDLE,
    /**
     * 运行状态
     */
    RUNNING,
    /**
     * 终止状态
     */
    TERMINATE,
    /**
     * 错误状态
     */
    ERROR
}
