package com.company.scopery.modules.trust.legalhold.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataLegalHoldJpaRepository extends JpaRepository<LegalHoldJpaEntity, UUID> {
    List<LegalHoldJpaEntity> findByWorkspaceId(UUID workspaceId);
    List<LegalHoldJpaEntity> findByWorkspaceIdAndStatus(UUID workspaceId, String status);
}
