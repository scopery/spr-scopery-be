package com.company.scopery.modules.aiplanning.contextsnapshot.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SpringDataAiPlanningContextSnapshotJpaRepository extends JpaRepository<AiPlanningContextSnapshotJpaEntity, UUID> {
}
