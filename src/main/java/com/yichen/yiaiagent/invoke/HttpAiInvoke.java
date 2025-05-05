package com.yichen.yiaiagent.invoke;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.yichen.yiaiagent.utils.TestApiKey;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author mojie
 * @date 2025/5/5 15:45
 * @description: 使用Http调用AI能力， 在不支持SDK调用的情况下，考虑Http直接调用
 */
public class HttpAiInvoke {
    public static void main(String[] args) {
        httpMethod2();
    }

    public static void httpMethod1() {
        String apiKey = TestApiKey.API_KEY; // 替换为你的实际 API Key

        // 构造请求体 JSON
        JSONObject requestBody = new JSONObject();
        requestBody.set("model", "qwen-plus");

        // 构造 input 对象
        JSONObject input = new JSONObject();
        JSONArray messages = new JSONArray();
        JSONObject systemMsg = new JSONObject();
        systemMsg.set("role", "system");
        systemMsg.set("content", "You are a helpful assistant.");
        messages.add(systemMsg);

        JSONObject userMsg = new JSONObject();
        userMsg.set("role", "user");
        userMsg.set("content", "你好，我是nidie");
        messages.add(userMsg);

        input.set("messages", messages);
        requestBody.set("input", input);

        // 构造 parameters 对象
        JSONObject parameters = new JSONObject();
        parameters.set("result_format", "message");
        requestBody.set("parameters", parameters);

        // 发送请求
        String url = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";
        HttpResponse response = HttpRequest.post(url)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .timeout(20000) // 超时时间，毫秒
                .execute();

        // 输出响应结果
        System.out.println("Status Code: " + response.getStatus());
        System.out.println("Response Body: " + response.body());
    }

    public static void httpMethod2() {
        // 替换为你的实际 API 密钥
        String url = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";

        // 设置请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + TestApiKey.API_KEY);
        headers.put("Content-Type", "application/json");

        // 设置请求体
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "qwen-plus");

        JSONObject input = new JSONObject();
        JSONObject[] messages = new JSONObject[2];

        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are a helpful assistant.");
        messages[0] = systemMessage;

        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", "woshinidie");
        messages[1] = userMessage;

        input.put("messages", messages);
        requestBody.put("input", input);

        JSONObject parameters = new JSONObject();
        parameters.put("result_format", "message");
        requestBody.put("parameters", parameters);

        // 发送请求
        HttpResponse response = HttpRequest.post(url)
                .addHeaders(headers)
                .body(requestBody.toString())
                .execute();

        // 处理响应
        if (response.isOk()) {
            System.out.println("请求成功，响应内容：");
            System.out.println(response.body());
        } else {
            System.out.println("请求失败，状态码：" + response.getStatus());
            System.out.println("响应内容：" + response.body());
        }

    }

}

