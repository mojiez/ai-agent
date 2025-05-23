package com.yichen.yiaiagent.app;

import com.yichen.yiaiagent.advisor.MyLoggerAdvisor;
import com.yichen.yiaiagent.chatmemory.FileBasedChatMemory;
import com.yichen.yiaiagent.tools.WeatherTools;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

/**
 * @author mojie
 * @date 2025/5/5 19:49
 * @description: 初始化ChatClient
 */

@Component
@Slf4j
public class LoveApp {

    private final ChatClient chatClient;
    private static final String SYSTEM_PROMPT = "扮演深耕恋爱心理领域的专家。开场向用户表明身份，告知用户可倾诉恋爱难题。" +
            "围绕单身、恋爱、已婚三种状态提问：单身状态询问社交圈拓展及追求心仪对象的困扰；" +
            "恋爱状态询问沟通、习惯差异引发的矛盾；已婚状态询问家庭责任与亲属关系处理的问题。" +
            "引导用户详述事情经过、对方反应及自身想法，以便给出专属解决方案。";

    record LoveReport(String title, List<String> suggestions) {
    }

    public LoveApp(ChatModel dashscopeChatModel) {
        // 初始化基于内存的对话记忆
        ChatMemory chatMemory = new InMemoryChatMemory();

//        // 自定义的文件持久化 多轮对话
//        String fileDir = System.getProperty("user.dir") + "/chat-memory";
//        ChatMemory chatMemory = new FileBasedChatMemory(fileDir);
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory),
                        // 自定义日志Advisor
                        new MyLoggerAdvisor()
                )
                .build();
    }

    public String doChat(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                // 这里的函数式编程相当于是 mao映射， 把传递的参数进行修改后再返回, 也就是绑定对应拦截器的会话id， 和最大会话长度
                .advisors(advisorSpec -> advisorSpec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId).param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .chatResponse();

        String content = chatResponse.getResult().getOutput().getText();
//        log.info("content: {}", content);
        return content;
    }

    public LoveReport doChatWithReport(String message, String chatId) {
        LoveReport loveReport = chatClient
                .prompt()
                .system(SYSTEM_PROMPT + "每次对话后都要生成恋爱结果，标题为{用户名}的恋爱报告，内容为建议列表")
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .entity(LoveReport.class);
        return loveReport;
    }

    /**
     * 使用RAG进行问答
     * 本地的RAG知识库
     */
    @Resource
    private VectorStore vectorStore;
    public String doChatWithRag(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .system(SYSTEM_PROMPT)
                .user(message)
                .advisors(advisorSpec -> advisorSpec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId).param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .advisors(new QuestionAnswerAdvisor(vectorStore))
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

    /**
     * 使用云知识库的RAG
     */

    // 根据名称注入
    @Resource
    Advisor loveAppRagCloudAdvisor;

    public String doChatWithCloudRag(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .system(SYSTEM_PROMPT)
                .user(message)
                .advisors(advisorSpec -> advisorSpec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId).param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
//                .advisors(new QuestionAnswerAdvisor(vectorStore))
                .advisors(loveAppRagCloudAdvisor)
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

//    /**
//     * 使用远程云数据库 PgVector 进行RAG检索
//     * 使用QuestionAnswerAdvisor
//     */
//
//    @Resource
//    private VectorStore pgVectorVectorStore;
//
//    public String doChatWithRemotePgRag(String message, String chatId) {
//        ChatResponse chatResponse = chatClient
//                .prompt()
//                .system(SYSTEM_PROMPT)
//                .user(message)
//                .advisors(advisorSpec -> advisorSpec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId).param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
////                .advisors(new QuestionAnswerAdvisor(vectorStore))
////                .advisors(loveAppRagCloudAdvisor)
//                .advisors(new QuestionAnswerAdvisor(pgVectorVectorStore))
//                .call()
//                .chatResponse();
//        String content = chatResponse.getResult().getOutput().getText();
//        log.info("content: {}", content);
//        return content;
//    }
//
//    /**
//     * 使用远程云数据库 PgVector 进行RAG检索
//     * 使用RetrievalAugmentationAdvisor
//     */
//    @Resource(name = "PgRetrievalAugmentationAdvisor")
//    private RetrievalAugmentationAdvisor pgRetrievalAugmentationAdvisor;
//    public String doChatWithRemotePgRagWithRetrieval(String message, String chatId) {
//        ChatResponse chatResponse = chatClient
//                .prompt()
//                .system(SYSTEM_PROMPT)
//                .user(message)
//                .advisors(advisorSpec -> advisorSpec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId).param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
////                .advisors(new QuestionAnswerAdvisor(vectorStore))
////                .advisors(loveAppRagCloudAdvisor)
////                .advisors(new QuestionAnswerAdvisor(pgVectorVectorStore))
//                .advisors(pgRetrievalAugmentationAdvisor)
//                .call()
//                .chatResponse();
//        String content = chatResponse.getResult().getOutput().getText();
//        log.info("content: {}", content);
//        return content;
//    }

    public String doChatWithTools(String message, String chatId, ToolCallback[] toolCallbacks) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .system(SYSTEM_PROMPT)
                .user(message)
                .advisors(advisorSpec -> advisorSpec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId).param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
//                .advisors(new QuestionAnswerAdvisor(vectorStore))
//                .advisors(loveAppRagCloudAdvisor)
//                .advisors(new QuestionAnswerAdvisor(pgVectorVectorStore))
                .tools(toolCallbacks)
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

    @Resource
    private ToolCallback[] allTools;
    public String doChatWithAllTools(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .system(SYSTEM_PROMPT)
                .user(message)
                .advisors(advisorSpec -> advisorSpec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId).param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
//                .advisors(new QuestionAnswerAdvisor(vectorStore))
//                .advisors(loveAppRagCloudAdvisor)
//                .advisors(new QuestionAnswerAdvisor(pgVectorVectorStore))
                .tools(allTools)
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

    @Resource
    private ToolCallbackProvider toolCallbackProvider;
    public String doChatWithMcpTools(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .system(SYSTEM_PROMPT)
                .user(message)
                .advisors(advisorSpec -> advisorSpec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId).param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
//                .advisors(new QuestionAnswerAdvisor(vectorStore))
//                .advisors(loveAppRagCloudAdvisor)
//                .advisors(new QuestionAnswerAdvisor(pgVectorVectorStore))
                .tools(toolCallbackProvider)
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }
}
