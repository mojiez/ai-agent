package com.yichen.yiaiagent.app;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetriever;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetrieverOptions;
import com.yichen.yiaiagent.utils.TestApiKey;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.rag.Query;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
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

    @Test
    void testWithRag() {
        String chatId = UUID.randomUUID().toString();

        String message = "我现在单身，我想线上交友，应该注意什么";
        String content = loveApp.doChatWithRag(message, chatId);
        Assertions.assertNotNull(content);
    }

    @Test
    void testRetriever() {
        DashScopeApi dashScopeApi = new DashScopeApi(TestApiKey.API_KEY);
        // 创建文档检索器
        DashScopeDocumentRetriever retriever = new DashScopeDocumentRetriever(dashScopeApi, DashScopeDocumentRetrieverOptions.builder().withIndexName("恋爱大师").build());

        // 测试从云知识库中查询
        List<Document> documentList = retriever.retrieve(new Query("我是单身，我想线上交友，有什么建议"));
        for (Document document : documentList) System.out.println(document);
    }

    @Test
    void testRagCloud() {
        String chatId = UUID.randomUUID().toString();

        String message = "我现在单身，我想线上交友，应该注意什么";
        String content = loveApp.doChatWithCloudRag(message, chatId);
        Assertions.assertNotNull(content);
    }

    @Autowired
    VectorStore vectorStore;

    @Test
    void testPGVector() {
        List<Document> documents = List.of(
                new Document("Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!!", Map.of("meta1", "meta1")),
                new Document("The World is Big and Salvation Lurks Around the Corner"),
                new Document("You walk forward facing the past and you turn back toward the future.", Map.of("meta2", "meta2")));
// Add the documents to PGVector
        vectorStore.add(documents);

// Retrieve documents similar to a query
        List<Document> results = this.vectorStore.similaritySearch(SearchRequest.builder().query("Spring").topK(5).build());
    }

    @Test
    void doChatWithRemotePgRag() {
        String chatId = UUID.randomUUID().toString();

        String message = "我想知道老王夫妇是怎么处理好夫妻关系的";
        String content = loveApp.doChatWithRemotePgRag(message, chatId);
        Assertions.assertNotNull(content);

    }

    @Test
    void doChatWithRemotePgRagWithRetrieval() {
        String chatId = UUID.randomUUID().toString();

        String message = "我想知道老王夫妇是怎么处理好夫妻关系的";
        String content = loveApp.doChatWithRemotePgRagWithRetrieval(message, chatId);
        Assertions.assertNotNull(content);
    }
}