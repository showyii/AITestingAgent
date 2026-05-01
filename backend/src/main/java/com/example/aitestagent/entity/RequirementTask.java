package com.example.aitestagent.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "requirement_task")
public class RequirementTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_no", nullable = false, unique = true, length = 64)
    private String taskNo;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "requirement_text", nullable = false, columnDefinition = "TEXT")
    private String requirementText;

    @Column(name = "source_type", nullable = false, length = 32)
    private String sourceType;

    @Column(name = "priority", nullable = false, length = 16)
    private String priority;

    @Column(name = "status", nullable = false, length = 32)
    private String status;

    @Column(name = "biz_domain", length = 64)
    private String bizDomain;

    @Column(name = "expected_deadline")
    private LocalDateTime expectedDeadline;

    @Column(name = "ai_model", length = 128)
    private String aiModel;

    @Column(name = "created_by", length = 64)
    private String createdBy;

    @Column(name = "updated_by", length = 64)
    private String updatedBy;

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
    public String getTaskNo() { return taskNo; }
    public void setTaskNo(String taskNo) { this.taskNo = taskNo; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getRequirementText() { return requirementText; }
    public void setRequirementText(String requirementText) { this.requirementText = requirementText; }
    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getBizDomain() { return bizDomain; }
    public void setBizDomain(String bizDomain) { this.bizDomain = bizDomain; }
    public LocalDateTime getExpectedDeadline() { return expectedDeadline; }
    public void setExpectedDeadline(LocalDateTime expectedDeadline) { this.expectedDeadline = expectedDeadline; }
    public String getAiModel() { return aiModel; }
    public void setAiModel(String aiModel) { this.aiModel = aiModel; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
