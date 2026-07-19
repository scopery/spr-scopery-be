package com.company.scopery.modules.traceability.requirement.infrastructure.persistence;
import com.company.scopery.modules.traceability.requirement.domain.model.*; import com.company.scopery.modules.traceability.requirement.infrastructure.mapper.RequirementPersistenceMapper;
import org.springframework.stereotype.Repository; import java.util.*;
@Repository
public class JpaRequirementRepository implements RequirementRepository {
    private final SpringDataRequirementJpaRepository springData; private final RequirementPersistenceMapper mapper;
    public JpaRequirementRepository(SpringDataRequirementJpaRepository springData, RequirementPersistenceMapper mapper){this.springData=springData;this.mapper=mapper;}
    @Override public Requirement save(Requirement e){return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e)));}
    @Override public Optional<Requirement> findByIdAndProjectId(UUID id, UUID projectId){return springData.findByIdAndProjectId(id,projectId).map(mapper::toDomain);}
    @Override public List<Requirement> findByProjectId(UUID projectId){return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList();}
}
