package com.example.aitestagent.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bug_analysis")
public class BugAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "analysis_no", nullable = false, unique = true, length = 64)
    private String analysisNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requirement_task_id", nullable = false)
    private RequirementTask requirementTask;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_case_id")
    private TestCase testCase;

    @Column(name = "bug_title", nullable = false, length = 255)
    private String bugTitle;

    @Column(name = "bug_description", nullable = false, columnDefinition = "TEXT")
    private String bugDescription;

    @Column(name = "root_cause", columnDefinition = "TEXT")
    private String rootCause;

    @Column(name = "impact_scope", columnDefinition = "TEXT")
    private String impactScope;

    @Column(name = "fix_suggestion", columnDefinition = "TEXT")
    private String fixSuggestion;

    @Column(name = "risk_level", nullable = false, length = 16)
    private String riskLevel;

    @Column(name = "reproducibility", nullable = false, length = 16)
    private String reproducibility;

    @Column(name = "status", nullable = false, length = 32)
    private String status;

    @Column(name = "analyzed_by", length = 64)
    private String analyzedBy;

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
    public String getAnalysisNo() { return analysisNo; }
    public void setAnalysisNo(String analysisNo) { this.analysisNo = analysisNo; }
    public RequirementTask getRequirementTask() { return requirementTask; }
    public void setRequirementTask(RequirementTask requirementTask) { this.requirementTask = requirementTask; }
    public TestCase getTestCase() { return testCase; }
    public void setTestCase(TestCase testCase) { this.testCase = testCase; }
    public String getBugTitle() { return bugTitle; }
    public void setBugTitle(String bugTitle) { this.bugTitle = bugTitle; }
    public String getBugDescription() { return bugDescription; }
    public void setBugDescription(String bugDescription) { this.bugDescription = bugDescription; }
    public String getRootCause() { return rootCause; }
    public void setRootCause(String rootCause) { this.rootCause = rootCause; }
    public String getImpactScope() { return impactScope; }
    public void setImpactScope(String impactScope) { this.impactScope = impactScope; }
    public String getFixSuggestion() { return fixSuggestion; }
    public void setFixSuggestion(String fixSuggestion) { this.fixSuggestion = fixSuggestion; }
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    public String getReproducibility() { return reproducibility; }
    public void setReproducibility(String reproducibility) { this.reproducibility = reproducibility; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getAnalyzedBy() { return analyzedBy; }
    public void setAnalyzedBy(String analyzedBy) { this.analyzedBy = analyzedBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
