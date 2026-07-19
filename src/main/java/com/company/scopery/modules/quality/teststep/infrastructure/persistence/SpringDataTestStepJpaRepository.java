package com.company.scopery.modules.quality.teststep.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataTestStepJpaRepository extends JpaRepository<TestStepJpaEntity, UUID> {
    Optional<TestStepJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    List<TestStepJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
    List<TestStepJpaEntity> findByProjectIdAndTestCaseIdOrderByStepOrderAsc(UUID projectId, UUID parentId);

}
