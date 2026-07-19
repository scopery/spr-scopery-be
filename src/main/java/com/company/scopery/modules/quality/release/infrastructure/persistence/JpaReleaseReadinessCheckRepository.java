package com.company.scopery.modules.quality.release.infrastructure.persistence;
import com.company.scopery.modules.quality.release.domain.model.*; import com.company.scopery.modules.quality.release.infrastructure.mapper.ReleaseReadinessCheckPersistenceMapper;
import org.springframework.stereotype.Repository; import java.util.*;
@Repository
public class JpaReleaseReadinessCheckRepository implements ReleaseReadinessCheckRepository {
    private final SpringDataReleaseReadinessCheckJpaRepository springData; private final ReleaseReadinessCheckPersistenceMapper mapper;
    public JpaReleaseReadinessCheckRepository(SpringDataReleaseReadinessCheckJpaRepository springData, ReleaseReadinessCheckPersistenceMapper mapper) {
        this.springData=springData; this.mapper=mapper;
    }
    @Override public ReleaseReadinessCheck save(ReleaseReadinessCheck e) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public List<ReleaseReadinessCheck> findByReleasePackageId(UUID id) { return springData.findByReleasePackageIdOrderByCreatedAtAsc(id).stream().map(mapper::toDomain).toList(); }
    @Override public void deleteByReleasePackageId(UUID id) { springData.deleteByReleasePackageId(id); }
}
