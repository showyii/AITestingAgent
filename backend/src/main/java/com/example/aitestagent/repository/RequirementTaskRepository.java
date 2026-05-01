package com.example.aitestagent.repository;

import com.example.aitestagent.entity.RequirementTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RequirementTaskRepository extends JpaRepository<RequirementTask, Long> {
    Optional<RequirementTask> findByTaskNo(String taskNo);
}
