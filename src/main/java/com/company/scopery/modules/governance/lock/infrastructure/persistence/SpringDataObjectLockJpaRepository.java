package com.company.scopery.modules.governance.lock.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SpringDataObjectLockJpaRepository extends JpaRepository<ObjectLockJpaEntity, UUID> {
    Optional<ObjectLockJpaEntity> findByObjectTypeCodeAndTargetIdAndLockTypeAndStatus(String objectTypeCode, UUID targetId, String lockType, String status);
    List<ObjectLockJpaEntity> findByProjectId(UUID projectId);
    List<ObjectLockJpaEntity> findByProjectIdAndStatus(UUID projectId, String status);
}
