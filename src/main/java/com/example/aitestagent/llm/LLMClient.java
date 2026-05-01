package com.example.aitestagent.llm;

import com.example.aitestagent.config.AiProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class LLMClient {

    private final AiProperties aiProperties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public LLMClient(AiProperties aiProperties) {
        this.aiProperties = aiProperties;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public JsonNode chatJson(String scene, String prompt) {
        if (aiProperties.isMockMode()) {
            return buildMockResponse(scene);
        }
        return callRemote(scene, prompt);
    }

    private JsonNode callRemote(String scene, String prompt) {
        if (!StringUtils.hasText(aiProperties.getApiKey())) {
            throw new IllegalStateException("app.ai.apiKey is empty while mockMode=false");
        }

        String url = trimEndSlash(aiProperties.getBaseUrl()) + "/chat/completions";

        Map<String, Object> payload = Map.of(
                "model", aiProperties.getModel(),
                "response_format", Map.of("type", "json_object"),
                "messages", List.of(
                        Map.of("role", "system", "content", "You are a testing agent. Always return JSON."),
                        Map.of("role", "user", "content", "Scene: " + scene + "\n" + prompt)
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(aiProperties.getApiKey());

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
        ResponseEntity<JsonNode> response = restTemplate.postForEntity(url, request, JsonNode.class);
        JsonNode body = response.getBody();

        if (body == null || body.path("choices").isMissingNode() || body.path("choices").isEmpty()) {
            throw new IllegalStateException("LLM response is empty");
        }

        String content = body.path("choices").path(0).path("message").path("content").asText("{}");
        try {
            return objectMapper.readTree(content);
        } catch (Exception ex) {
            throw new IllegalStateException("LLM content is not valid JSON: " + content, ex);
        }
    }

    private JsonNode buildMockResponse(String scene) {
        String json;
        switch (scene) {
            case "requirement-understanding" -> json = """
                    {
                      "summary": "用户登录后可创建任务并查看执行历史",
                      "coreFunctions": ["登录鉴权", "任务创建", "历史查询"],
                      "risks": ["未授权访问", "并发提交导致重复任务"]
                    }
                    """;
            case "testcase-generation" -> json = """
                    {
                      "feature": "任务创建",
                      "testCases": [
                        {"id": "TC-001", "title": "正常创建任务", "priority": "P1", "steps": ["输入合法参数", "提交"], "expected": "返回200且任务状态为PENDING"},
                        {"id": "TC-002", "title": "缺少必填字段", "priority": "P1", "steps": ["不填任务名称", "提交"], "expected": "返回400并提示字段校验失败"}
                      ]
                    }
                    """;
            case "bug-analysis" -> json = """
                    {
                      "issueType": "参数校验异常",
                      "possibleRootCauses": ["后端DTO未加@NotBlank", "前端字段名与接口不一致"],
                      "suggestedFixes": ["补充后端校验注解", "统一字段映射并补充契约测试"]
                    }
                    """;
            case "report-generation" -> json = """
                    {
                      "overview": "本轮共执行12条用例，通过10条，失败2条",
                      "metrics": {"total": 12, "passed": 10, "failed": 2, "passRate": "83.3%"},
                      "topRisks": ["鉴权边界未覆盖", "异常分支自动化不足"],
                      "nextActions": ["补齐鉴权异常测试", "提升失败日志结构化程度"]
                    }
                    """;
            default -> json = """
                    {"message": "mock ok", "scene": "unknown"}
                    """;
        }

        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new IllegalStateException("mock json parse error", e);
        }
    }

    private String trimEndSlash(String value) {
        if (!StringUtils.hasText(value)) {
            return "";
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }
}

