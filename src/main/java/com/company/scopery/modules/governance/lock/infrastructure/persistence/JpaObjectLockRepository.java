package com.company.scopery.modules.governance.lock.infrastructure.persistence;
import com.company.scopery.modules.governance.lock.domain.model.*;
import com.company.scopery.modules.governance.lock.infrastructure.mapper.ObjectLockPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaObjectLockRepository implements ObjectLockRepository {
    private final SpringDataObjectLockJpaRepository springData; private final ObjectLockPersistenceMapper mapper;
    public JpaObjectLockRepository(SpringDataObjectLockJpaRepository springData, ObjectLockPersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public ObjectLock save(ObjectLock lock) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(lock))); }
    @Override public Optional<ObjectLock> findById(UUID id) { return springData.findById(id).map(mapper::toDomain); }
    @Override public Optional<ObjectLock> findActive(String objectTypeCode, UUID targetId, String lockType) {
        return springData.findByObjectTypeCodeAndTargetIdAndLockTypeAndStatus(objectTypeCode, targetId, lockType, "ACTIVE").map(mapper::toDomain);
    }
    @Override public List<ObjectLock> findByProjectId(UUID projectId) {
        return springData.findByProjectId(projectId).stream().map(mapper::toDomain).toList();
    }
    @Override public List<ObjectLock> findActiveByProject(UUID projectId) {
        return springData.findByProjectIdAndStatus(projectId, "ACTIVE").stream().map(mapper::toDomain).toList();
    }
}
