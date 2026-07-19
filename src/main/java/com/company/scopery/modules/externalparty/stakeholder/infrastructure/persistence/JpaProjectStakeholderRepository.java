package com.company.scopery.modules.externalparty.stakeholder.infrastructure.persistence;
import com.company.scopery.modules.externalparty.stakeholder.domain.model.*; import com.company.scopery.modules.externalparty.stakeholder.infrastructure.mapper.ProjectStakeholderPersistenceMapper;
import org.springframework.stereotype.Repository; import java.util.*;
@Repository
public class JpaProjectStakeholderRepository implements ProjectStakeholderRepository {
    private final SpringDataProjectStakeholderJpaRepository springData; private final ProjectStakeholderPersistenceMapper mapper;
    public JpaProjectStakeholderRepository(SpringDataProjectStakeholderJpaRepository springData, ProjectStakeholderPersistenceMapper mapper){this.springData=springData;this.mapper=mapper;}
    @Override public ProjectStakeholder save(ProjectStakeholder e){return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e)));}
    @Override public Optional<ProjectStakeholder> findByIdAndProjectId(UUID id, UUID projectId){return springData.findByIdAndProjectId(id,projectId).map(mapper::toDomain);}
    @Override public List<ProjectStakeholder> findByProjectId(UUID projectId){return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList();}
}
