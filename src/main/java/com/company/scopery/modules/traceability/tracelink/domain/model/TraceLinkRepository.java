package com.company.scopery.modules.traceability.tracelink.domain.model;
import java.util.*;
public interface TraceLinkRepository {
    TraceLink save(TraceLink entity);
    Optional<TraceLink> findByIdAndProjectId(UUID id, UUID projectId);
    List<TraceLink> findByProjectId(UUID projectId);
}
