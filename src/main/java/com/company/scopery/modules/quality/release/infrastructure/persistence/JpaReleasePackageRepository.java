package com.company.scopery.modules.quality.release.infrastructure.persistence;
import com.company.scopery.modules.quality.release.domain.model.*; import com.company.scopery.modules.quality.release.infrastructure.mapper.ReleasePackagePersistenceMapper;
import org.springframework.stereotype.Repository; import java.util.*;
@Repository
public class JpaReleasePackageRepository implements ReleasePackageRepository {
    private final SpringDataReleasePackageJpaRepository springData; private final ReleasePackagePersistenceMapper mapper;
    public JpaReleasePackageRepository(SpringDataReleasePackageJpaRepository springData, ReleasePackagePersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public ReleasePackage save(ReleasePackage e) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<ReleasePackage> findByIdAndProjectId(UUID id, UUID projectId) { return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain); }
    @Override public List<ReleasePackage> findByProjectId(UUID projectId) { return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList(); }
    @Override public boolean existsByProjectIdAndCode(UUID projectId, String code) { return springData.existsByProjectIdAndCode(projectId, code); }
}
