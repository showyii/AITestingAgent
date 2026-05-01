package com.example.aitestagent.repository;

import com.example.aitestagent.entity.BugAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BugAnalysisRepository extends JpaRepository<BugAnalysis, Long> {
    Optional<BugAnalysis> findByAnalysisNo(String analysisNo);
    List<BugAnalysis> findByRequirementTaskId(Long requirementTaskId);
    List<BugAnalysis> findByTestCaseId(Long testCaseId);
}
