package com.company.scopery.modules.governance.ownership.infrastructure.persistence;
import com.company.scopery.modules.governance.ownership.domain.model.*;
import com.company.scopery.modules.governance.ownership.infrastructure.mapper.ObjectOwnershipPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaObjectOwnershipRepository implements ObjectOwnershipRepository {
    private final SpringDataObjectOwnershipJpaRepository springData; private final ObjectOwnershipPersistenceMapper mapper;
    public JpaObjectOwnershipRepository(SpringDataObjectOwnershipJpaRepository springData, ObjectOwnershipPersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public ObjectOwnership save(ObjectOwnership o) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(o))); }
    @Override public Optional<ObjectOwnership> findActive(String objectTypeCode, UUID targetId) { return springData.findByObjectTypeCodeAndTargetIdAndStatus(objectTypeCode, targetId, "ACTIVE").map(mapper::toDomain); }
    @Override public List<ObjectOwnership> findByProjectId(UUID projectId) { return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList(); }
}
