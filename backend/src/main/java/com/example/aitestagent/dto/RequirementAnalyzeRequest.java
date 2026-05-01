package com.example.aitestagent.dto;

public class RequirementAnalyzeRequest {

    private String requirement;

    public RequirementAnalyzeRequest() {
    }

    public RequirementAnalyzeRequest(String requirement) {
        this.requirement = requirement;
    }

    public String getRequirement() {
        return requirement;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }
}
