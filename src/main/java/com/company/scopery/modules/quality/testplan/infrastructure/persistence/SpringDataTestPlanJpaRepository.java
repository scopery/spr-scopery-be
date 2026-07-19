package com.company.scopery.modules.quality.testplan.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataTestPlanJpaRepository extends JpaRepository<TestPlanJpaEntity, UUID> {
    Optional<TestPlanJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    List<TestPlanJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);

}
