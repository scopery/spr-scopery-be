package com.company.scopery.modules.traceability.requirementcriteria.infrastructure.persistence;
import com.company.scopery.modules.traceability.requirementcriteria.domain.model.RequirementCriteria;
import com.company.scopery.modules.traceability.requirementcriteria.domain.model.RequirementCriteriaRepository;
import com.company.scopery.modules.traceability.requirementcriteria.infrastructure.mapper.RequirementCriteriaPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaRequirementCriteriaRepository implements RequirementCriteriaRepository {
    private final SpringDataRequirementCriteriaJpaRepository springData;
    private final RequirementCriteriaPersistenceMapper mapper;
    public JpaRequirementCriteriaRepository(SpringDataRequirementCriteriaJpaRepository springData, RequirementCriteriaPersistenceMapper mapper) {
        this.springData = springData; this.mapper = mapper;
    }
    @Override public RequirementCriteria save(RequirementCriteria e) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<RequirementCriteria> findByIdAndRequirementId(UUID id, UUID requirementId) {
        return springData.findByIdAndRequirementId(id, requirementId).map(mapper::toDomain);
    }
    @Override public List<RequirementCriteria> findByRequirementId(UUID requirementId) {
        return springData.findByRequirementIdOrderByDisplayOrderAsc(requirementId).stream().map(mapper::toDomain).toList();
    }
}
