package com.company.scopery.modules.governance.grant.infrastructure.persistence;
import com.company.scopery.modules.governance.grant.domain.model.*;
import com.company.scopery.modules.governance.grant.infrastructure.mapper.ObjectAccessGrantPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaObjectAccessGrantRepository implements ObjectAccessGrantRepository {
    private final SpringDataObjectAccessGrantJpaRepository springData; private final ObjectAccessGrantPersistenceMapper mapper;
    public JpaObjectAccessGrantRepository(SpringDataObjectAccessGrantJpaRepository springData, ObjectAccessGrantPersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public ObjectAccessGrant save(ObjectAccessGrant g) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpa(g))); }
    @Override public Optional<ObjectAccessGrant> findById(UUID id) { return springData.findById(id).map(mapper::toDomain); }
    @Override public List<ObjectAccessGrant> findActiveByTarget(String objectTypeCode, UUID targetId) {
        return springData.findByObjectTypeCodeAndTargetIdAndStatus(objectTypeCode, targetId, "ACTIVE").stream().map(mapper::toDomain).toList();
    }
    @Override public List<ObjectAccessGrant> findByProjectId(UUID projectId) {
        return springData.findByProjectId(projectId).stream().map(mapper::toDomain).toList();
    }
}
