package com.atyichen.yiimagesearchmcpserver;

import com.atyichen.yiimagesearchmcpserver.tool.ImageSearchTool;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class YiImageSearchMcpServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(YiImageSearchMcpServerApplication.class, args);
    }

    // 在主类中 通过 定义 ToolCallBackProvider 来注册工具
    @Bean
    public ToolCallbackProvider imageSearchTools(ImageSearchTool imageSearchTool) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(imageSearchTool)
                .build();
    }
}
