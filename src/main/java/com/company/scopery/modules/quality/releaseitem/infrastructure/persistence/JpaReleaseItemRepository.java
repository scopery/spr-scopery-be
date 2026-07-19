package com.company.scopery.modules.quality.releaseitem.infrastructure.persistence;
import com.company.scopery.modules.quality.releaseitem.domain.model.*;
import com.company.scopery.modules.quality.releaseitem.infrastructure.mapper.ReleaseItemPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaReleaseItemRepository implements ReleaseItemRepository {
    private final SpringDataReleaseItemJpaRepository springData;
    private final ReleaseItemPersistenceMapper mapper;
    public JpaReleaseItemRepository(SpringDataReleaseItemJpaRepository springData, ReleaseItemPersistenceMapper mapper) {
        this.springData=springData; this.mapper=mapper;
    }
    @Override public ReleaseItem save(ReleaseItem e) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<ReleaseItem> findByIdAndProjectId(UUID id, UUID projectId) {
        return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain);
    }
    @Override public List<ReleaseItem> findByProjectIdAndReleasePackageId(UUID projectId, UUID releasePackageId) {
        return springData.findByProjectIdAndReleasePackageIdOrderByCreatedAtDesc(projectId, releasePackageId).stream().map(mapper::toDomain).toList();
    }
}
