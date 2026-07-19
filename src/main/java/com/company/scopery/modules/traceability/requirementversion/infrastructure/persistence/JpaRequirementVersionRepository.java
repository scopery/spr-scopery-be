package com.company.scopery.modules.traceability.requirementversion.infrastructure.persistence;
import com.company.scopery.modules.traceability.requirementversion.domain.model.RequirementVersion;
import com.company.scopery.modules.traceability.requirementversion.domain.model.RequirementVersionRepository;
import com.company.scopery.modules.traceability.requirementversion.infrastructure.mapper.RequirementVersionPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaRequirementVersionRepository implements RequirementVersionRepository {
    private final SpringDataRequirementVersionJpaRepository springData;
    private final RequirementVersionPersistenceMapper mapper;
    public JpaRequirementVersionRepository(SpringDataRequirementVersionJpaRepository springData, RequirementVersionPersistenceMapper mapper) {
        this.springData = springData; this.mapper = mapper;
    }
    @Override public RequirementVersion save(RequirementVersion e) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<RequirementVersion> findByIdAndRequirementId(UUID id, UUID requirementId) {
        return springData.findByIdAndRequirementId(id, requirementId).map(mapper::toDomain);
    }
    @Override public List<RequirementVersion> findByRequirementId(UUID requirementId) {
        return springData.findByRequirementIdOrderByVersionNumberDesc(requirementId).stream().map(mapper::toDomain).toList();
    }
    @Override public int countByRequirementId(UUID requirementId) { return springData.countByRequirementId(requirementId); }
}
