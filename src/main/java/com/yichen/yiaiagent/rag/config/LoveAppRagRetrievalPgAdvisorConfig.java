package com.yichen.yiaiagent.rag.config;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetriever;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetrieverOptions;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author mojie
 * @date 2025/5/6 15:11
 * @description: 初始化基于云数据库PgVector的检索增强Advisor
 */
@Configuration
@Slf4j
public class LoveAppRagRetrievalPgAdvisorConfig {
    private QueryTransformer queryTransformer;

    @Resource
    private VectorStore pgVectorVectorStore;

    @Bean
    public RetrievalAugmentationAdvisor PgRetrievalAugmentationAdvisor(ChatModel dashscopeChatModel) {
        // 检索前
        ChatClient.Builder builder = ChatClient.builder(dashscopeChatModel);
        // 1. 使用重写转换器
        queryTransformer = RewriteQueryTransformer.builder()
                .chatClientBuilder(builder)
                .build();
        // 2. 使用多查询扩展器
        MultiQueryExpander queryExpander = MultiQueryExpander.builder()
                .chatClientBuilder(builder)
                .numberOfQueries(3)
                .build();

        // 检索中
        // 1. 设置相似度阈值 topk 指定文档检索的数据库
        Filter.Expression expression = new FilterExpressionBuilder()
                .eq("status", "已婚")
                .build();

        DocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(pgVectorVectorStore)
                .similarityThreshold(0.5) //相似度阈值
                .filterExpression(expression) // 2. 配置文档过滤规则， 只需要已婚的
                .topK(5)
                .build();
        // 检索后
        // 1. 空上下文检索
        ContextualQueryAugmenter queryAugmenter = ContextualQueryAugmenter.builder()
                .allowEmptyContext(false)
                .build();

        RetrievalAugmentationAdvisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
                .queryTransformers(queryTransformer)
                .queryExpander(queryExpander)
                .documentRetriever(documentRetriever)
                .queryAugmenter(queryAugmenter)
                .build();

        return retrievalAugmentationAdvisor;
    }

}
