package com.company.scopery.modules.raid.decision.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataDecisionRecordJpaRepository extends JpaRepository<DecisionRecordJpaEntity, UUID> {
    Optional<DecisionRecordJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    List<DecisionRecordJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
}
