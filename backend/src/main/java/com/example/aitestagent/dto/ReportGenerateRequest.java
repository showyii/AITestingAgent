package com.example.aitestagent.dto;

public class ReportGenerateRequest {

    private String requirement;
    private String testResultSummary;

    public ReportGenerateRequest() {
    }

    public ReportGenerateRequest(String requirement, String testResultSummary) {
        this.requirement = requirement;
        this.testResultSummary = testResultSummary;
    }

    public String getRequirement() {
        return requirement;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    public String getTestResultSummary() {
        return testResultSummary;
    }

    public void setTestResultSummary(String testResultSummary) {
        this.testResultSummary = testResultSummary;
    }
}
