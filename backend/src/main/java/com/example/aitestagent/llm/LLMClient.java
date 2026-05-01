package com.example.aitestagent.llm;

import com.example.aitestagent.config.AiProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class  LLMClient {

    private static final Logger log = LoggerFactory.getLogger(LLMClient.class);

    private final AiProperties aiProperties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public LLMClient(AiProperties aiProperties) {
        this.aiProperties = aiProperties;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public String chatJson(String scene, String prompt) {
        log.info("LLM request config: mockMode={}, provider={}, baseUrl={}, model={}, apiKeyConfigured={}",
                aiProperties.isMockMode(),
                aiProperties.getProvider(),
                aiProperties.getBaseUrl(),
                aiProperties.getModel(),
                StringUtils.hasText(aiProperties.getApiKey()));

        if (aiProperties.isMockMode()) {
            log.info("LLM mock mode is enabled, using mock response. scene={}", scene);
            return buildMockResponse(scene);
        }

        log.info("LLM mock mode is disabled, calling real AI API. scene={}", scene);
        return callRemote(scene, prompt);
    }

    private String callRemote(String scene, String prompt) {
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
        JsonNode body;
        try {
            ResponseEntity<JsonNode> response = restTemplate.postForEntity(url, request, JsonNode.class);
            body = response.getBody();
            log.info("LLM raw response = {}", body == null ? "null" : body.toString());
        } catch (HttpStatusCodeException ex) {
            log.error("Real AI API call failed. status={}, body={}, message={}",
                    ex.getStatusCode(),
                    ex.getResponseBodyAsString(),
                    ex.getMessage(),
                    ex);
            throw ex;
        } catch (Exception ex) {
            log.error("Real AI API call failed. status=N/A, body=N/A, message={}",
                    ex.getMessage(),
                    ex);
            throw ex;
        }

        if (body == null || body.path("choices").isMissingNode() || body.path("choices").isEmpty()) {
            log.error("LLM response missing choices. fullResponse={}", body);
            throw new IllegalStateException("LLM response is empty");
        }

        String content = body.path("choices").path(0).path("message").path("content").asText("");
        if (!StringUtils.hasText(content)) {
            log.error("LLM response content is empty at choices[0].message.content. fullResponse={}", body);
            throw new IllegalStateException("LLM response content is empty (choices[0].message.content)");
        }
        return stripMarkdownCodeFence(content);
    }

    private String buildMockResponse(String scene) {
        String json;
        switch (scene) {
            case "requirement-understanding" -> json = """
                    {
                      "summary": "用户登录后可创建任务并查看执行历史。",
                      "coreFunctions": ["登录鉴权", "任务创建", "历史查询"],
                      "risks": ["未授权访问", "并发提交导致重复任务"]
                    }
                    """;
            case "testcase-generation" -> json = """
                    {
                      "feature": "任务创建",
                      "testCases": [
                        {"id": "TC-001", "title": "正常创建任务", "priority": "P1", "precondition": "用户已登录", "steps": ["输入合法参数", "提交"], "expected": "返回200且任务状态为PENDING"},
                        {"id": "TC-002", "title": "缺少必填字段", "priority": "P1", "precondition": "用户已登录", "steps": ["不填任务名称", "提交"], "expected": "返回400并提示字段校验失败"},
                        {"id": "TC-003", "title": "并发重复提交", "priority": "P2", "precondition": "用户已登录", "steps": ["短时间内重复提交相同参数", "观察结果"], "expected": "系统去重或返回重复提交提示"}
                      ]
                    }
                    """;
            case "bug-analysis" -> json = """
                    {
                      "issueType": "参数校验异常",
                      "possibleRootCauses": ["后端DTO缺少@NotBlank约束", "前端字段命名与后端不一致"],
                      "suggestedFixes": ["补充后端参数校验", "统一前后端字段映射并补充接口测试"],
                      "riskLevel": "HIGH"
                    }
                    """;
            case "report-generation" -> json = """
                    {
                      "report": "测试报告\n1. 需求概述：本轮围绕任务创建能力进行测试。\n2. 执行结果：共执行12条用例，通过10条，失败2条，通过率83.3%。\n3. 主要风险：鉴权边界覆盖不足，异常分支自动化不足。\n4. 建议：补齐鉴权异常用例并加强日志结构化。",
                      "overview": "本轮共执行12条用例，通过10条，失败2条。",
                      "metrics": {"total": 12, "passed": 10, "failed": 2, "passRate": "83.3%"},
                      "topRisks": ["鉴权边界未覆盖", "异常分支自动化不足"],
                      "nextActions": ["补齐鉴权异常测试", "提升失败日志结构化程度"]
                    }
                    """;
            default -> json = """
                    {"message": "mock ok", "scene": "unknown"}
                    """;
        }

        return json;
    }

    private String stripMarkdownCodeFence(String content) {
        if (!StringUtils.hasText(content)) {
            return "";
        }
        String trimmed = content.trim();
        if (trimmed.startsWith("```") && trimmed.endsWith("```")) {
            int firstNewLine = trimmed.indexOf('\n');
            if (firstNewLine >= 0) {
                String head = trimmed.substring(0, firstNewLine).trim();
                if (head.equals("```") || head.equalsIgnoreCase("```json")) {
                    String inner = trimmed.substring(firstNewLine + 1, trimmed.length() - 3);
                    return inner.trim();
                }
            }
        }
        return trimmed;
    }

    private String trimEndSlash(String value) {
        if (!StringUtils.hasText(value)) {
            return "";
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }

    public JsonNode chatJsonNode(String s, String prompt) {
        String json = chatJson(s, prompt);
        if (!StringUtils.hasText(json)) {
            return objectMapper.createObjectNode();
        }
        try {
            return objectMapper.readTree(json);
        } catch (Exception ex) {
            log.error("Failed to parse LLM JSON response. scene={}, message={}", s, ex.getMessage(), ex);
            throw new IllegalStateException("LLM response is not valid JSON", ex);
        }
    }
}
