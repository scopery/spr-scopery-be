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
        this.springData=springData; this.mapper=mapper;
    }
    @Override public TraceLink save(TraceLink e) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<TraceLink> findByIdAndProjectId(UUID id, UUID projectId) {
        return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain);
    }
    @Override public List<TraceLink> findByProjectId(UUID projectId) {
        return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList();
    }
}
