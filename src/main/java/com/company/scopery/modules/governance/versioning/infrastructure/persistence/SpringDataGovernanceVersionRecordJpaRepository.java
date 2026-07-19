package com.company.scopery.modules.governance.versioning.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*; import java.util.UUID;
public interface SpringDataGovernanceVersionRecordJpaRepository extends JpaRepository<GovernanceVersionRecordJpaEntity, UUID> {
    List<GovernanceVersionRecordJpaEntity> findByObjectTypeCodeAndTargetIdOrderByVersionNumberDesc(String objectTypeCode, UUID targetId);
    Optional<GovernanceVersionRecordJpaEntity> findByObjectTypeCodeAndTargetIdAndCurrentFlagTrue(String objectTypeCode, UUID targetId);
    List<GovernanceVersionRecordJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
}
