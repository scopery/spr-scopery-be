package com.company.scopery.modules.clientportal.grant.infrastructure.persistence;
import com.company.scopery.modules.clientportal.grant.domain.model.*;
import com.company.scopery.modules.clientportal.grant.infrastructure.mapper.ExternalProjectAccessGrantPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaExternalProjectAccessGrantRepository implements ExternalProjectAccessGrantRepository {
    private final SpringDataExternalProjectAccessGrantJpaRepository springData;
    private final ExternalProjectAccessGrantPersistenceMapper mapper;
    public JpaExternalProjectAccessGrantRepository(SpringDataExternalProjectAccessGrantJpaRepository springData, ExternalProjectAccessGrantPersistenceMapper mapper) {
        this.springData=springData; this.mapper=mapper;
    }
    @Override public ExternalProjectAccessGrant save(ExternalProjectAccessGrant e) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<ExternalProjectAccessGrant> findByIdAndProjectId(UUID id, UUID projectId) {
        return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain);
    }
    @Override public List<ExternalProjectAccessGrant> findByProjectId(UUID projectId) {
        return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList();
    }
    @Override public Optional<ExternalProjectAccessGrant> findByProjectIdAndPortalAccountId(UUID projectId, UUID portalAccountId) {
        return springData.findByProjectIdAndPortalAccountId(projectId, portalAccountId).map(mapper::toDomain);
    }
    @Override public List<ExternalProjectAccessGrant> findByPortalAccountId(UUID portalAccountId) {
        return springData.findByPortalAccountIdOrderByCreatedAtDesc(portalAccountId).stream().map(mapper::toDomain).toList();
    }
}
