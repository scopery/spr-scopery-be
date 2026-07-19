package com.company.scopery.modules.projectbaseline.changerequest.infrastructure.mapper;

import com.company.scopery.modules.projectbaseline.changerequest.domain.enums.*;
import com.company.scopery.modules.projectbaseline.changerequest.domain.model.ChangeRequest;
import com.company.scopery.modules.projectbaseline.changerequest.infrastructure.persistence.ChangeRequestJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ChangeRequestPersistenceMapper {
    public ChangeRequest toDomain(ChangeRequestJpaEntity e) {
        return new ChangeRequest(
                e.getId(), e.getProjectId(), e.getWorkspaceId(), e.getBaselineId(), e.getCode(), e.getTitle(),
                e.getDescription(), ChangeType.valueOf(e.getChangeType()),
                e.getPriority() == null ? null : ChangePriority.valueOf(e.getPriority()),
                ChangeRequestStatus.valueOf(e.getStatus()), e.getReason(), e.getRequestedBy(), e.getRequestedAt(),
                e.getSubmittedAt(), e.getSubmittedBy(), e.getApprovedAt(), e.getApprovedBy(),
                e.getRejectedAt(), e.getRejectedBy(), e.getRejectionReason(), e.getCancelledAt(), e.getCancelledBy(),
                e.getAppliedAt(), e.getAppliedBy(), e.getArchivedAt(), e.getArchivedBy(), e.getTraceId(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public ChangeRequestJpaEntity toJpaEntity(ChangeRequest d) {
        ChangeRequestJpaEntity e = new ChangeRequestJpaEntity();
        e.setId(d.id()); e.setProjectId(d.projectId()); e.setWorkspaceId(d.workspaceId());
        e.setBaselineId(d.baselineId()); e.setCode(d.code()); e.setTitle(d.title());
        e.setDescription(d.description()); e.setChangeType(d.changeType().name());
        e.setPriority(d.priority() == null ? null : d.priority().name());
        e.setStatus(d.status().name()); e.setReason(d.reason());
        e.setRequestedBy(d.requestedBy()); e.setRequestedAt(d.requestedAt());
        e.setSubmittedAt(d.submittedAt()); e.setSubmittedBy(d.submittedBy());
        e.setApprovedAt(d.approvedAt()); e.setApprovedBy(d.approvedBy());
        e.setRejectedAt(d.rejectedAt()); e.setRejectedBy(d.rejectedBy());
        e.setRejectionReason(d.rejectionReason()); e.setCancelledAt(d.cancelledAt()); e.setCancelledBy(d.cancelledBy());
        e.setAppliedAt(d.appliedAt()); e.setAppliedBy(d.appliedBy());
        e.setArchivedAt(d.archivedAt()); e.setArchivedBy(d.archivedBy());
        e.setTraceId(d.traceId()); e.setVersion(d.version());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
