package com.example.aitestagent.dto;

public class BugAnalyzeRequest {

    private String issueDescription;

    public BugAnalyzeRequest() {
    }

    public BugAnalyzeRequest(String issueDescription) {
        this.issueDescription = issueDescription;
    }

    public String getIssueDescription() {
        return issueDescription;
    }

    public void setIssueDescription(String issueDescription) {
        this.issueDescription = issueDescription;
    }
}
