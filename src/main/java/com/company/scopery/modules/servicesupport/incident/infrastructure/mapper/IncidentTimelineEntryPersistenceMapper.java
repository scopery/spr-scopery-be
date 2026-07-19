package com.company.scopery.modules.servicesupport.incident.infrastructure.mapper;
import com.company.scopery.modules.servicesupport.incident.domain.model.IncidentTimelineEntry;
import com.company.scopery.modules.servicesupport.incident.infrastructure.persistence.IncidentTimelineEntryJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class IncidentTimelineEntryPersistenceMapper {
    public IncidentTimelineEntryJpaEntity toJpa(IncidentTimelineEntry d) {
        var e = new IncidentTimelineEntryJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setIncidentId(d.incidentId());
        e.setEntryType(d.entryType()); e.setVisibility(d.visibility()); e.setMessage(d.message());
        e.setOccurredAt(d.occurredAt()); e.setCreatedAt(d.createdAt());
        return e;
    }
    public IncidentTimelineEntry toDomain(IncidentTimelineEntryJpaEntity e) {
        return new IncidentTimelineEntry(e.getId(), e.getWorkspaceId(), e.getIncidentId(), e.getEntryType(),
                e.getVisibility(), e.getMessage(), e.getOccurredAt(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
