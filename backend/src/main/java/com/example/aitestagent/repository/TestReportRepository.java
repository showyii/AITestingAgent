package com.example.aitestagent.repository;

import com.example.aitestagent.entity.TestReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TestReportRepository extends JpaRepository<TestReport, Long> {
    Optional<TestReport> findByReportNo(String reportNo);
    List<TestReport> findByRequirementTaskId(Long requirementTaskId);
}
