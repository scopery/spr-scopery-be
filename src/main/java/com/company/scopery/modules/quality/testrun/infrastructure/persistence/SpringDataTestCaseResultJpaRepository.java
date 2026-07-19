package com.company.scopery.modules.quality.testrun.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface SpringDataTestCaseResultJpaRepository extends JpaRepository<TestCaseResultJpaEntity, UUID> {
    Optional<TestCaseResultJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    List<TestCaseResultJpaEntity> findByTestRunIdOrderByCreatedAtAsc(UUID testRunId);
}
