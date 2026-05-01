package com.example.aitestagent.repository;

import com.example.aitestagent.entity.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TestCaseRepository extends JpaRepository<TestCase, Long> {
    Optional<TestCase> findByCaseNo(String caseNo);
    List<TestCase> findByRequirementTaskId(Long requirementTaskId);
}
