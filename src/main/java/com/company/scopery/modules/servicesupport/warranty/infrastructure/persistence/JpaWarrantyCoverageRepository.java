package com.company.scopery.modules.servicesupport.warranty.infrastructure.persistence;

import com.company.scopery.modules.servicesupport.warranty.domain.model.WarrantyCoverage;
import com.company.scopery.modules.servicesupport.warranty.domain.model.WarrantyCoverageRepository;
import com.company.scopery.modules.servicesupport.warranty.infrastructure.mapper.WarrantyCoveragePersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.UUID;

@Repository
public class JpaWarrantyCoverageRepository implements WarrantyCoverageRepository {
    private final SpringDataWarrantyCoverageJpaRepository spring;
    private final WarrantyCoveragePersistenceMapper mapper;
    public JpaWarrantyCoverageRepository(SpringDataWarrantyCoverageJpaRepository spring, WarrantyCoveragePersistenceMapper mapper) {
        this.spring = spring; this.mapper = mapper;
    }
    @Override public WarrantyCoverage save(WarrantyCoverage c) { return mapper.toDomain(spring.saveAndFlush(mapper.toJpa(c))); }
    @Override public java.util.Optional<WarrantyCoverage> findById(UUID id) { return spring.findById(id).map(mapper::toDomain); }
    @Override public List<WarrantyCoverage> findByWorkspaceId(UUID workspaceId) {
        return spring.findByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList();
    }
    @Override public List<WarrantyCoverage> findByProjectId(UUID projectId) {
        return spring.findByProjectId(projectId).stream().map(mapper::toDomain).toList();
    }
}
