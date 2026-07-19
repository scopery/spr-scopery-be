package com.company.scopery.modules.trust.evidence.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataComplianceEvidenceJpaRepository extends JpaRepository<ComplianceEvidenceRecordJpaEntity, UUID> {
    List<ComplianceEvidenceRecordJpaEntity> findByWorkspaceId(UUID workspaceId);
}
