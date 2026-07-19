package com.company.scopery.modules.quality.testrun.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface SpringDataTestRunJpaRepository extends JpaRepository<TestRunJpaEntity, UUID> {
    Optional<TestRunJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    List<TestRunJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
}
