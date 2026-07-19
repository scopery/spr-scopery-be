package com.company.scopery.modules.governance.grant.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*; import java.util.UUID;
public interface SpringDataObjectAccessGrantJpaRepository extends JpaRepository<ObjectAccessGrantJpaEntity, UUID> {
    List<ObjectAccessGrantJpaEntity> findByObjectTypeCodeAndTargetIdAndStatus(String objectTypeCode, UUID targetId, String status);
    List<ObjectAccessGrantJpaEntity> findByProjectId(UUID projectId);
}
