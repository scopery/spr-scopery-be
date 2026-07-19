package com.company.scopery.modules.governance.lock.domain.model;
import java.util.Optional; import java.util.UUID;
import java.util.List;
public interface ObjectLockRepository {
    ObjectLock save(ObjectLock lock);
    Optional<ObjectLock> findById(UUID id);
    Optional<ObjectLock> findActive(String objectTypeCode, UUID targetId, String lockType);
    List<ObjectLock> findByProjectId(UUID projectId);
    List<ObjectLock> findActiveByProject(UUID projectId);
}
