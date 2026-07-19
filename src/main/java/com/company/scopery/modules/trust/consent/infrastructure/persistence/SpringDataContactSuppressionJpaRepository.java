package com.company.scopery.modules.trust.consent.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataContactSuppressionJpaRepository extends JpaRepository<ContactSuppressionJpaEntity, UUID> {
    List<ContactSuppressionJpaEntity> findByWorkspaceId(UUID workspaceId);
}
