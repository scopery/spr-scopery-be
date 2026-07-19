package com.company.scopery.modules.quality.coverage.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataTestCaseCoverageJpaRepository extends JpaRepository<TestCaseCoverageJpaEntity, UUID> {
    Optional<TestCaseCoverageJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    List<TestCaseCoverageJpaEntity> findByProjectIdAndTestCaseIdOrderByCreatedAtDesc(UUID projectId, UUID testCaseId);
}
