package com.company.scopery.modules.scope.scopepackage.infrastructure.persistence;
import com.company.scopery.modules.scope.scopepackage.domain.model.ScopePackage;
import com.company.scopery.modules.scope.scopepackage.domain.model.ScopePackageRepository;
import com.company.scopery.modules.scope.scopepackage.infrastructure.mapper.ScopePackagePersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaScopePackageRepository implements ScopePackageRepository {
    private final SpringDataScopePackageJpaRepository springData;
    private final ScopePackagePersistenceMapper mapper;
    public JpaScopePackageRepository(SpringDataScopePackageJpaRepository springData, ScopePackagePersistenceMapper mapper) {
        this.springData = springData; this.mapper = mapper;
    }
    @Override public ScopePackage save(ScopePackage pkg){ return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(pkg))); }
    @Override public Optional<ScopePackage> findByIdAndProjectId(UUID id, UUID projectId){ return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain); }
    @Override public List<ScopePackage> findByProjectId(UUID projectId){ return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList(); }
    @Override public boolean existsByProjectIdAndCode(UUID projectId, String code){ return springData.existsByProjectIdAndCode(projectId, code); }
    @Override public void clearCurrentFlag(UUID projectId){ springData.clearCurrentFlag(projectId); }
}
