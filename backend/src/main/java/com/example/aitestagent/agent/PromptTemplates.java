package com.example.aitestagent.agent;

import org.springframework.stereotype.Component;

@Component
public class PromptTemplates {

    public String requirementUnderstandingPrompt(String requirementText) {
        String template = """
                You are a testing analysis agent. Analyze the requirement and return strict JSON only:
                {
                  "summary": "one-line summary",
                  "coreFunctions": ["function-1", "function-2"],
                  "risks": ["risk-1", "risk-2"]
                }

                Requirement:
                {{requirementText}}
                """;
        return template.replace("{{requirementText}}", requirementText == null ? "" : requirementText);
    }

    public String testCaseGenerationPrompt(String requirementText) {
        String template = """
                Generate structured test cases and return strict JSON only:
                {
                  "feature": "module name",
                  "testCases": [
                    {
                      "id": "TC-001",
                      "title": "case title",
                      "priority": "P1/P2/P3",
                      "precondition": "precondition",
                      "steps": ["step-1", "step-2"],
                      "expected": "expected result"
                    }
                  ]
                }

                Requirement:
                {{requirementText}}
                """;
        return template.replace("{{requirementText}}", requirementText == null ? "" : requirementText);
    }

    public String bugAnalysisPrompt(String issueDescription) {
        String template = """
                Analyze the failure symptom or error log and return strict JSON only:
                {
                  "issueType": "type",
                  "possibleRootCauses": ["cause-1", "cause-2"],
                  "suggestedFixes": ["fix-1", "fix-2"],
                  "riskLevel": "HIGH/MEDIUM/LOW"
                }

                Input:
                {{issueDescription}}
                """;
        return template.replace("{{issueDescription}}", issueDescription == null ? "" : issueDescription);
    }

    public String testReportPrompt(String requirementText, String testResultSummary) {
        String template = """
                Generate a test report from requirement and execution summary, return strict JSON only:
                {
                  "report": "full report text",
                  "overview": "summary",
                  "metrics": {"total": 0, "passed": 0, "failed": 0, "passRate": "0%"},
                  "topRisks": ["risk-1", "risk-2"],
                  "nextActions": ["action-1", "action-2"]
                }

                Requirement:
                {{requirementText}}

                Execution Summary:
                {{testResultSummary}}
                """;
        return template
                .replace("{{requirementText}}", requirementText == null ? "" : requirementText)
                .replace("{{testResultSummary}}", testResultSummary == null ? "" : testResultSummary);
    }
}
