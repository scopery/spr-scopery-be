package com.company.scopery.modules.quality.testcase.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface SpringDataTestCaseJpaRepository extends JpaRepository<TestCaseJpaEntity, UUID> {
    Optional<TestCaseJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    List<TestCaseJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
    boolean existsByProjectIdAndCode(UUID projectId, String code);
}
