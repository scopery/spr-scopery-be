package com.company.scopery.modules.profitability.profile.infrastructure.persistence;
import com.company.scopery.modules.profitability.profile.domain.model.*;
import com.company.scopery.modules.profitability.profile.infrastructure.mapper.ProjectProfitabilityProfilePersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaProjectProfitabilityProfileRepository implements ProjectProfitabilityProfileRepository {
    private final SpringDataProjectProfitabilityProfileJpaRepository springData; private final ProjectProfitabilityProfilePersistenceMapper mapper;
    public JpaProjectProfitabilityProfileRepository(SpringDataProjectProfitabilityProfileJpaRepository springData, ProjectProfitabilityProfilePersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public ProjectProfitabilityProfile save(ProjectProfitabilityProfile p) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(p))); }
    @Override public Optional<ProjectProfitabilityProfile> findByProjectId(UUID projectId) { return springData.findByProjectId(projectId).map(mapper::toDomain); }
    @Override public boolean existsByProjectId(UUID projectId) { return springData.existsByProjectId(projectId); }
}
