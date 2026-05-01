package com.example.aitestagent.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "test_case")
public class TestCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "case_no", nullable = false, unique = true, length = 64)
    private String caseNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requirement_task_id", nullable = false)
    private RequirementTask requirementTask;

    @Column(name = "case_title", nullable = false, length = 255)
    private String caseTitle;

    @Column(name = "case_type", nullable = false, length = 32)
    private String caseType;

    @Column(name = "preconditions", columnDefinition = "TEXT")
    private String preconditions;

    @Column(name = "test_steps", nullable = false, columnDefinition = "TEXT")
    private String testSteps;

    @Column(name = "expected_result", nullable = false, columnDefinition = "TEXT")
    private String expectedResult;

    @Column(name = "actual_result", columnDefinition = "TEXT")
    private String actualResult;

    @Column(name = "execute_status", nullable = false, length = 32)
    private String executeStatus;

    @Column(name = "severity", nullable = false, length = 16)
    private String severity;

    @Column(name = "automation_flag", nullable = false)
    private Boolean automationFlag;

    @Column(name = "tags", length = 255)
    private String tags;

    @Column(name = "generated_by_ai", nullable = false)
    private Boolean generatedByAi;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCaseNo() { return caseNo; }
    public void setCaseNo(String caseNo) { this.caseNo = caseNo; }
    public RequirementTask getRequirementTask() { return requirementTask; }
    public void setRequirementTask(RequirementTask requirementTask) { this.requirementTask = requirementTask; }
    public String getCaseTitle() { return caseTitle; }
    public void setCaseTitle(String caseTitle) { this.caseTitle = caseTitle; }
    public String getCaseType() { return caseType; }
    public void setCaseType(String caseType) { this.caseType = caseType; }
    public String getPreconditions() { return preconditions; }
    public void setPreconditions(String preconditions) { this.preconditions = preconditions; }
    public String getTestSteps() { return testSteps; }
    public void setTestSteps(String testSteps) { this.testSteps = testSteps; }
    public String getExpectedResult() { return expectedResult; }
    public void setExpectedResult(String expectedResult) { this.expectedResult = expectedResult; }
    public String getActualResult() { return actualResult; }
    public void setActualResult(String actualResult) { this.actualResult = actualResult; }
    public String getExecuteStatus() { return executeStatus; }
    public void setExecuteStatus(String executeStatus) { this.executeStatus = executeStatus; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public Boolean getAutomationFlag() { return automationFlag; }
    public void setAutomationFlag(Boolean automationFlag) { this.automationFlag = automationFlag; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public Boolean getGeneratedByAi() { return generatedByAi; }
    public void setGeneratedByAi(Boolean generatedByAi) { this.generatedByAi = generatedByAi; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
