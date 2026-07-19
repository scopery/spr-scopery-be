package com.company.scopery.modules.trust.anonymization.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataAnonymizationPlanJpaRepository extends JpaRepository<AnonymizationPlanJpaEntity, UUID> {
    List<AnonymizationPlanJpaEntity> findByWorkspaceId(UUID workspaceId);
}
