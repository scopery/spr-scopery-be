package com.company.scopery.modules.externalparty.organization.infrastructure.persistence;
import com.company.scopery.modules.externalparty.organization.domain.model.*;
import com.company.scopery.modules.externalparty.organization.infrastructure.mapper.ExternalOrganizationPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaExternalOrganizationRepository implements ExternalOrganizationRepository {
    private final SpringDataExternalOrganizationJpaRepository springData;
    private final ExternalOrganizationPersistenceMapper mapper;
    public JpaExternalOrganizationRepository(SpringDataExternalOrganizationJpaRepository springData, ExternalOrganizationPersistenceMapper mapper) {
        this.springData=springData; this.mapper=mapper;
    }
    @Override public ExternalOrganization save(ExternalOrganization e) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<ExternalOrganization> findByIdAndWorkspaceId(UUID id, UUID workspaceId) {
        return springData.findByIdAndWorkspaceId(id, workspaceId).map(mapper::toDomain);
    }
    @Override public List<ExternalOrganization> findByWorkspaceId(UUID workspaceId) {
        return springData.findByWorkspaceIdOrderByCreatedAtDesc(workspaceId).stream().map(mapper::toDomain).toList();
    }
}
