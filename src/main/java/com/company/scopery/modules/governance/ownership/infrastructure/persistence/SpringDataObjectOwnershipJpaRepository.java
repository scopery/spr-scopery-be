package com.company.scopery.modules.governance.ownership.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SpringDataObjectOwnershipJpaRepository extends JpaRepository<ObjectOwnershipJpaEntity, UUID> {
    Optional<ObjectOwnershipJpaEntity> findByObjectTypeCodeAndTargetIdAndStatus(String objectTypeCode, UUID targetId, String status);
    List<ObjectOwnershipJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
}
