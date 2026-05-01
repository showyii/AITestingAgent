package com.example.aitestagent.controller;

import com.example.aitestagent.dto.ApiResponse;
import com.example.aitestagent.dto.BugAnalyzeRequest;
import com.example.aitestagent.dto.ReportGenerateRequest;
import com.example.aitestagent.dto.RequirementAnalyzeRequest;
import com.example.aitestagent.dto.TestCaseGenerateRequest;
import com.example.aitestagent.service.AgentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/agent")
public class AgentController {

    private final AgentService agentService;

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<ApiResponse<Map<String, Object>>> analyze(@RequestBody RequirementAnalyzeRequest request) {
        Map<String, Object> data = agentService.analyzeRequirement(request == null ? null : request.getRequirement());
        return ResponseEntity.ok(ApiResponse.ok("需求分析完成", data));
    }

    @PostMapping("/generate-cases")
    public ResponseEntity<ApiResponse<Map<String, Object>>> generateCases(@RequestBody TestCaseGenerateRequest request) {
        Map<String, Object> data = agentService.generateTestCases(request == null ? null : request.getRequirement());
        return ResponseEntity.ok(ApiResponse.ok("测试用例生成完成", data));
    }

    @PostMapping("/analyze-bug")
    public ResponseEntity<ApiResponse<Map<String, Object>>> analyzeBug(@RequestBody BugAnalyzeRequest request) {
        Map<String, Object> data = agentService.analyzeBug(request == null ? null : request.getIssueDescription());
        return ResponseEntity.ok(ApiResponse.ok("缺陷分析完成", data));
    }

    @PostMapping("/report")
    public ResponseEntity<ApiResponse<Map<String, Object>>> generateReport(@RequestBody ReportGenerateRequest request) {
        Map<String, Object> data = agentService.generateReport(
                request == null ? null : request.getRequirement(),
                request == null ? null : request.getTestResultSummary()
        );
        return ResponseEntity.ok(ApiResponse.ok("测试报告生成完成", data));
    }
}
