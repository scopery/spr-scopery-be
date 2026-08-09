package com.company.scopery.modules.traceability.tracelink.infrastructure.persistence;

import com.company.scopery.modules.traceability.tracelink.domain.model.*;
import com.company.scopery.modules.traceability.tracelink.infrastructure.mapper.TraceLinkPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class JpaTraceLinkRepository implements TraceLinkRepository {

    private final SpringDataTraceLinkJpaRepository springData;
    private final TraceLinkPersistenceMapper mapper;

    public JpaTraceLinkRepository(SpringDataTraceLinkJpaRepository springData, TraceLinkPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public TraceLink save(TraceLink e) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e)));
    }

    @Override
    public Optional<TraceLink> findByIdAndProjectId(UUID id, UUID projectId) {
        return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain);
    }

    @Override
    public List<TraceLink> findByProjectId(UUID projectId) {
        return springData.findActiveByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<TraceLink> findActiveBySourceAndTarget(UUID projectId, String sourceType, UUID sourceId,
                                                        String targetType, UUID targetId, String linkType) {
        return springData.findActiveBySourceAndTarget(projectId, sourceType, sourceId, targetType, targetId, linkType)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsActiveLink(UUID projectId, String sourceType, UUID sourceId,
                                    String targetType, UUID targetId, String linkType) {
        return springData.existsActiveLink(projectId, sourceType, sourceId, targetType, targetId, linkType);
    }

    @Override
    public boolean hasAnyActiveLinkForEntity(String entityType, UUID entityId) {
        return springData.hasAnyActiveLinkForEntity(entityType, entityId);
    }
}
