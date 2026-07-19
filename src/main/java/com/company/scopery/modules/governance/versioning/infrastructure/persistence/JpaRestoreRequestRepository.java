package com.company.scopery.modules.governance.versioning.infrastructure.persistence;
import com.company.scopery.modules.governance.versioning.domain.model.*;
import com.company.scopery.modules.governance.versioning.infrastructure.mapper.VersioningPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaRestoreRequestRepository implements RestoreRequestRepository {
    private final SpringDataRestoreRequestJpaRepository springData; private final VersioningPersistenceMapper mapper;
    public JpaRestoreRequestRepository(SpringDataRestoreRequestJpaRepository springData, VersioningPersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public RestoreRequest save(RestoreRequest r) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpa(r))); }
    @Override public Optional<RestoreRequest> findById(UUID id) { return springData.findById(id).map(mapper::toDomain); }
    @Override public List<RestoreRequest> findByProjectId(UUID projectId) {
        return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList();
    }
}
