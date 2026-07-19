package com.company.scopery.modules.traceability.application.infrastructure.persistence;
import com.company.scopery.modules.traceability.application.domain.model.*;
import com.company.scopery.modules.traceability.application.infrastructure.mapper.RegistryApplicationPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaRegistryApplicationRepository implements RegistryApplicationRepository {
    private final SpringDataRegistryApplicationJpaRepository springData;
    private final RegistryApplicationPersistenceMapper mapper;
    public JpaRegistryApplicationRepository(SpringDataRegistryApplicationJpaRepository springData, RegistryApplicationPersistenceMapper mapper) {
        this.springData=springData; this.mapper=mapper;
    }
    @Override public RegistryApplication save(RegistryApplication e) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<RegistryApplication> findByIdAndWorkspaceId(UUID id, UUID workspaceId) {
        return springData.findByIdAndWorkspaceId(id, workspaceId).map(mapper::toDomain);
    }
    @Override public List<RegistryApplication> findByWorkspaceId(UUID workspaceId) {
        return springData.findByWorkspaceIdOrderByCreatedAtDesc(workspaceId).stream().map(mapper::toDomain).toList();
    }
}
