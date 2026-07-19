package com.company.scopery.modules.traceability.requirementsource.infrastructure.persistence;
import com.company.scopery.modules.traceability.requirementsource.domain.model.RequirementSource;
import com.company.scopery.modules.traceability.requirementsource.domain.model.RequirementSourceRepository;
import com.company.scopery.modules.traceability.requirementsource.infrastructure.mapper.RequirementSourcePersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaRequirementSourceRepository implements RequirementSourceRepository {
    private final SpringDataRequirementSourceJpaRepository springData;
    private final RequirementSourcePersistenceMapper mapper;
    public JpaRequirementSourceRepository(SpringDataRequirementSourceJpaRepository springData, RequirementSourcePersistenceMapper mapper) {
        this.springData = springData; this.mapper = mapper;
    }
    @Override public RequirementSource save(RequirementSource e) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<RequirementSource> findByIdAndRequirementId(UUID id, UUID requirementId) {
        return springData.findByIdAndRequirementId(id, requirementId).map(mapper::toDomain);
    }
    @Override public List<RequirementSource> findByRequirementId(UUID requirementId) {
        return springData.findByRequirementIdOrderByCreatedAtDesc(requirementId).stream().map(mapper::toDomain).toList();
    }
}
