package com.example.aitestagent.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "test_report")
public class TestReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "report_no", nullable = false, unique = true, length = 64)
    private String reportNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requirement_task_id", nullable = false)
    private RequirementTask requirementTask;

    @Column(name = "report_title", nullable = false, length = 255)
    private String reportTitle;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @Column(name = "total_cases", nullable = false)
    private Integer totalCases;

    @Column(name = "passed_cases", nullable = false)
    private Integer passedCases;

    @Column(name = "failed_cases", nullable = false)
    private Integer failedCases;

    @Column(name = "blocked_cases", nullable = false)
    private Integer blockedCases;

    @Column(name = "skipped_cases", nullable = false)
    private Integer skippedCases;

    @Column(name = "defect_count", nullable = false)
    private Integer defectCount;

    @Column(name = "pass_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal passRate;

    @Column(name = "quality_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal qualityScore;

    @Column(name = "conclusion", nullable = false, length = 32)
    private String conclusion;

    @Column(name = "report_content", columnDefinition = "LONGTEXT")
    private String reportContent;

    @Column(name = "generated_by", length = 64)
    private String generatedBy;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

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
    public String getReportNo() { return reportNo; }
    public void setReportNo(String reportNo) { this.reportNo = reportNo; }
    public RequirementTask getRequirementTask() { return requirementTask; }
    public void setRequirementTask(RequirementTask requirementTask) { this.requirementTask = requirementTask; }
    public String getReportTitle() { return reportTitle; }
    public void setReportTitle(String reportTitle) { this.reportTitle = reportTitle; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public Integer getTotalCases() { return totalCases; }
    public void setTotalCases(Integer totalCases) { this.totalCases = totalCases; }
    public Integer getPassedCases() { return passedCases; }
    public void setPassedCases(Integer passedCases) { this.passedCases = passedCases; }
    public Integer getFailedCases() { return failedCases; }
    public void setFailedCases(Integer failedCases) { this.failedCases = failedCases; }
    public Integer getBlockedCases() { return blockedCases; }
    public void setBlockedCases(Integer blockedCases) { this.blockedCases = blockedCases; }
    public Integer getSkippedCases() { return skippedCases; }
    public void setSkippedCases(Integer skippedCases) { this.skippedCases = skippedCases; }
    public Integer getDefectCount() { return defectCount; }
    public void setDefectCount(Integer defectCount) { this.defectCount = defectCount; }
    public BigDecimal getPassRate() { return passRate; }
    public void setPassRate(BigDecimal passRate) { this.passRate = passRate; }
    public BigDecimal getQualityScore() { return qualityScore; }
    public void setQualityScore(BigDecimal qualityScore) { this.qualityScore = qualityScore; }
    public String getConclusion() { return conclusion; }
    public void setConclusion(String conclusion) { this.conclusion = conclusion; }
    public String getReportContent() { return reportContent; }
    public void setReportContent(String reportContent) { this.reportContent = reportContent; }
    public String getGeneratedBy() { return generatedBy; }
    public void setGeneratedBy(String generatedBy) { this.generatedBy = generatedBy; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
