package com.company.scopery.modules.traceability.tracelink.infrastructure.mapper;
import com.company.scopery.modules.traceability.tracelink.domain.enums.TraceLinkStatus;
import com.company.scopery.modules.traceability.tracelink.domain.enums.TraceLinkType;
import com.company.scopery.modules.traceability.tracelink.domain.model.TraceLink;
import com.company.scopery.modules.traceability.tracelink.infrastructure.persistence.TraceLinkJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class TraceLinkPersistenceMapper {
    public TraceLink toDomain(TraceLinkJpaEntity e) {
        return new TraceLink(e.getId(), e.getProjectId(), e.getSourceType(), e.getSourceId(),
                e.getTargetType(), e.getTargetId(),
                e.getLinkType() != null ? TraceLinkType.valueOf(e.getLinkType()) : TraceLinkType.RELATED_TO,
                e.getStatus() != null ? TraceLinkStatus.valueOf(e.getStatus()) : TraceLinkStatus.ACTIVE,
                e.getArchivedAt(), e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt());
    }
    public TraceLinkJpaEntity toJpaEntity(TraceLink d) {
        TraceLinkJpaEntity e = new TraceLinkJpaEntity();
        e.setId(d.id()); e.setProjectId(d.projectId());
        e.setSourceType(d.sourceType()); e.setSourceId(d.sourceId());
        e.setTargetType(d.targetType()); e.setTargetId(d.targetId());
        e.setLinkType(d.linkType().name()); e.setStatus(d.status().name());
        e.setArchivedAt(d.archivedAt());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
