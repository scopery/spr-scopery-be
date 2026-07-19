package com.company.scopery.modules.quality.testsuite.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataTestSuiteJpaRepository extends JpaRepository<TestSuiteJpaEntity, UUID> {
    Optional<TestSuiteJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    List<TestSuiteJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
    List<TestSuiteJpaEntity> findByProjectIdAndTestPlanIdOrderBySortOrderAscCreatedAtDesc(UUID projectId, UUID testPlanId);
}
