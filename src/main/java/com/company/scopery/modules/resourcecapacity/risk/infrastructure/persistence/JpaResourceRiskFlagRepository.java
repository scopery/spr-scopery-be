package com.company.scopery.modules.resourcecapacity.risk.infrastructure.persistence;
import com.company.scopery.modules.resourcecapacity.risk.domain.model.*;
import com.company.scopery.modules.resourcecapacity.risk.infrastructure.mapper.ResourceRiskFlagPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaResourceRiskFlagRepository implements ResourceRiskFlagRepository {
    private final SpringDataResourceRiskFlagJpaRepository spring; private final ResourceRiskFlagPersistenceMapper mapper;
    public JpaResourceRiskFlagRepository(SpringDataResourceRiskFlagJpaRepository spring, ResourceRiskFlagPersistenceMapper mapper) {
        this.spring=spring; this.mapper=mapper;
    }
    @Override public ResourceRiskFlag save(ResourceRiskFlag f) { return mapper.toDomain(spring.saveAndFlush(mapper.toJpaEntity(f))); }
    @Override public Optional<ResourceRiskFlag> findById(UUID id) { return spring.findById(id).map(mapper::toDomain); }
    @Override public List<ResourceRiskFlag> findByProjectId(UUID projectId) { return spring.findByProjectId(projectId).stream().map(mapper::toDomain).toList(); }
}
