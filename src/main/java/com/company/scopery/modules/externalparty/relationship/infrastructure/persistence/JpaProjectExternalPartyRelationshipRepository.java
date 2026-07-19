package com.company.scopery.modules.externalparty.relationship.infrastructure.persistence;
import com.company.scopery.modules.externalparty.relationship.domain.model.*;
import com.company.scopery.modules.externalparty.relationship.infrastructure.mapper.ProjectExternalPartyRelationshipPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaProjectExternalPartyRelationshipRepository implements ProjectExternalPartyRelationshipRepository {
    private final SpringDataProjectExternalPartyRelationshipJpaRepository springData;
    private final ProjectExternalPartyRelationshipPersistenceMapper mapper;
    public JpaProjectExternalPartyRelationshipRepository(SpringDataProjectExternalPartyRelationshipJpaRepository springData, ProjectExternalPartyRelationshipPersistenceMapper mapper) {
        this.springData=springData; this.mapper=mapper;
    }
    @Override public ProjectExternalPartyRelationship save(ProjectExternalPartyRelationship e) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<ProjectExternalPartyRelationship> findByIdAndProjectId(UUID id, UUID projectId) {
        return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain);
    }
    @Override public List<ProjectExternalPartyRelationship> findByProjectId(UUID projectId) {
        return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList();
    }
}
