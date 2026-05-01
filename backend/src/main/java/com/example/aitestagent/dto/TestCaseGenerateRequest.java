package com.example.aitestagent.dto;

public class TestCaseGenerateRequest {

    private String requirement;

    public TestCaseGenerateRequest() {
    }

    public TestCaseGenerateRequest(String requirement) {
        this.requirement = requirement;
    }

    public String getRequirement() {
        return requirement;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }
}
