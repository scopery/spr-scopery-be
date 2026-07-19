package com.company.scopery.modules.servicesupport.incident.infrastructure.mapper;
import com.company.scopery.modules.servicesupport.incident.domain.model.SupportIncidentRecord;
import com.company.scopery.modules.servicesupport.incident.infrastructure.persistence.SupportIncidentRecordJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class SupportIncidentRecordPersistenceMapper {
    public SupportIncidentRecordJpaEntity toJpa(SupportIncidentRecord d) {
        var e = new SupportIncidentRecordJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setServiceProfileId(d.serviceProfileId());
        e.setProjectId(d.projectId()); e.setIncidentNumber(d.incidentNumber()); e.setTitle(d.title());
        e.setDescription(d.description()); e.setSeverity(d.severity()); e.setStatus(d.status());
        e.setImpactSummary(d.impactSummary()); e.setClientVisibleSummary(d.clientVisibleSummary());
        e.setOwnerUserId(d.ownerUserId()); e.setDetectedAt(d.detectedAt()); e.setAcknowledgedAt(d.acknowledgedAt());
        e.setResolvedAt(d.resolvedAt()); e.setClosedAt(d.closedAt()); e.setCreatedAt(d.createdAt());
        return e;
    }
    public SupportIncidentRecord toDomain(SupportIncidentRecordJpaEntity e) {
        return new SupportIncidentRecord(e.getId(), e.getWorkspaceId(), e.getServiceProfileId(), e.getProjectId(),
                e.getIncidentNumber(), e.getTitle(), e.getDescription(), e.getSeverity(), e.getStatus(),
                e.getImpactSummary(), e.getClientVisibleSummary(), e.getOwnerUserId(),
                e.getDetectedAt(), e.getAcknowledgedAt(), e.getResolvedAt(), e.getClosedAt(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
