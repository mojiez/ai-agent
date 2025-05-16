package com.yichen.yiaiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author mojie
 * @date 2025/5/6 13:33
 * @description: 初始化向量数据库并保存文档,这个是一个简单的向量数据库SimpleVectorStore，存储在内存中，可以使用json文件形式持久化保存
 */
@Configuration
public class LoveAppVectorStoreConfig {
    // 为什么可以实现自动注入，构造函数不是只有一个有参构造吗？
    /**
     * Resource默认是按照名称进行注入的
     * Spring会自动识别对应的有参构造函数, 注入ResourcePatternResolver实例
     */
    @Resource
    private LoveAppDocumentLoader loveAppDocumentLoader;

    @Bean
    VectorStore loveAppVectorStore(EmbeddingModel dashscopeEmbeddingModel) {
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel)
                .build();
        // 加载文档
        List<Document> documents = loveAppDocumentLoader.loadMarkdowns();
        // 存储为向量
        simpleVectorStore.add(documents);
        return simpleVectorStore;
    }
}
