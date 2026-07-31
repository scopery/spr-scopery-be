package com.company.scopery.modules.quality.teststep.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.*;
public interface SpringDataTestCaseStepJpaRepository extends JpaRepository<TestCaseStepJpaEntity, UUID> {
    Optional<TestCaseStepJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    List<TestCaseStepJpaEntity> findByTestCaseIdOrderBySortOrderAsc(UUID testCaseId);
    @Query("SELECT COALESCE(MAX(e.sortOrder), 0) FROM TestCaseStepJpaEntity e WHERE e.testCaseId = :testCaseId")
    int findMaxSortOrderByTestCaseId(@Param("testCaseId") UUID testCaseId);
    long countByTestCaseIdAndArchivedAtIsNull(UUID testCaseId);
}
